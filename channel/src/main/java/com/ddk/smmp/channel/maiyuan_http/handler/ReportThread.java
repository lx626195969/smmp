package com.ddk.smmp.channel.maiyuan_http.handler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.HttpClient;
import com.ddk.smmp.utils.JaxbUtils;

/**
 * 
 * @author leeson 2014年6月26日 下午4:57:05 li_mr_ceo@163.com <br>
 *
 */
public class ReportThread extends Thread {
	private static final Logger logger = Logger.getLogger(ReportThread.class);
	
	Channel channel = null;
	private ScheduledThreadPoolExecutor receiveDataThreadPool = null;
	private boolean isBatch = false;
	public ReportThread(Channel channel) {
		setDaemon(true);
		this.channel = channel;
		this.receiveDataThreadPool = new ScheduledThreadPoolExecutor(1);
		this.isBatch = (channel.getIsBatch().intValue() == 1);
	}

	@Override
	public void run() {
		receiveDataThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				List<DelivVo> delivVos = new LinkedList<DelivVo>();
				
				HttpClient httpClient = new HttpClient();
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("userid", channel.getCompanyCode());
				paramMap.put("account", channel.getAccount());
				paramMap.put("password", channel.getPassword());
				paramMap.put("action", "query");
				
				Object obj = httpClient.post(channel.getSubmitUrl() + "/statusApi.aspx", paramMap, "utf-8");
				
				if(null != obj){
					ChannelLog.log(logger, "receive reportXml:" + obj.toString(), LevelUtils.getSucLevel(channel.getId()));
					
					ReportRsp reportRsp = JaxbUtils.converyToJavaBean(obj.toString(), ReportRsp.class);
					if(null != reportRsp && null == reportRsp.getReportErr()){
						for(Report report : reportRsp.getReports()){
							Long msgId = -1l;
							if(isBatch){
								String taskId = report.getTaskid();
								// 取号码+TaskId后4位拼成MsgId
								if (taskId.length() >= 4) {
									taskId = taskId.substring(taskId.length() - 4);
								} else {
									taskId = StringUtils.leftPad(taskId, 4, "0");
								}
								msgId = Long.parseLong(report.getMobile() + taskId);// 取号码+TaskId后4位拼成MsgId
							}else{
								msgId = Long.parseLong(report.getTaskid());
							}
							String state = (report.getStatus().equals("10") ? "DELIVRD" : "UNDELIV");
							
							delivVos.add(new DelivVo(msgId, channel.getId(), state, report.getReceivetime()));
							
							ChannelLog.log(logger, "receive report:" + report, LevelUtils.getSucLevel(channel.getId()));
						}
						
						if(delivVos.size() > 0){
							SmsCache.queue3.addAll(delivVos);
						}
					}
				}
			}
		}, 1000, 10 * 1000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 终止线程
	 */
	public void stop_() {
		synchronized (this) {
			receiveDataThreadPool.shutdown();
		}
	}
}