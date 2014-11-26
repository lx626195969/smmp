package com.ddk.smmp.channel.liancheng_http.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.HttpClient;

/**
 * 
 * @author leeson 2014年6月26日 下午4:57:05 li_mr_ceo@163.com <br>
 *
 */
public class ReportThread extends Thread {
	private static final Logger logger = Logger.getLogger(ReportThread.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	Channel channel = null;
	private ScheduledThreadPoolExecutor receiveDataThreadPool = null;
	
	public ReportThread(Channel channel) {
		setDaemon(true);
		this.channel = channel;
		this.receiveDataThreadPool = new ScheduledThreadPoolExecutor(1);
	}

	@Override
	public void run() {
		receiveDataThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				List<DelivVo> delivVos = new LinkedList<DelivVo>();
				
				HttpClient httpClient = new HttpClient();
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("corpID", channel.getCompanyCode());
				paramMap.put("loginName", channel.getAccount());
				paramMap.put("password", channel.getPassword());
				paramMap.put("MD5str", "");
				
				Object obj = httpClient.get(channel.getSubmitUrl() + "/getReport/", paramMap, "GBK");
				
				if(null != obj){
					ChannelLog.log(logger, "receive reportStr:" + obj.toString(), LevelUtils.getSucLevel(channel.getId()));
					
					String[] resultArray = obj.toString().split("\r\n|\n|\r");
					if(resultArray.length > 0 && resultArray[0].equals("100")){
						String time = sdf.format(new Date());
						
						for(int i = 1;i < resultArray.length; i++){
							String[] reportArray = resultArray[i].split(",");
							Long msgId = Long.parseLong(reportArray[0]);
							String state = reportArray[2].equals("0") ? "DELIVRD" : "UNDELIV";
							
							delivVos.add(new DelivVo(msgId, channel.getId(), state, time));
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