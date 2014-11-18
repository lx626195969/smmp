package com.ddk.smmp.channel.sioo_http.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;
import com.ddk.smmp.utils.HttpClient;

/**
 * 
 * @author leeson 2014年6月26日 下午4:57:05 li_mr_ceo@163.com <br>
 *
 */
public class ReportThread extends Thread {
	private static final Logger logger = Logger.getLogger(ReportThread.class);
	
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
				paramMap.put("uid", channel.getAccount());
				paramMap.put("auth", DigestUtils.md5Hex(channel.getCompanyCode() + channel.getPassword()));
				
				Object obj = httpClient.get(channel.getSubmitUrl() + "/rpt", paramMap, "utf-8");
				
				if(null != obj){
					String data = obj.toString();
					if(!data.startsWith("0") && !data.startsWith("-")){
						logger.info("receive report:uid=" + paramMap.get("uid") + ";auth=" + paramMap.get("auth") + ";");
						logger.info(data);
						
						String[] delivArray = data.split(";");
						for(String deliv : delivArray){
							try {
								String[] rptArray = deliv.split(",");
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = sdf.format(sdf.parse(rptArray[0]));
								long msgId = Long.parseLong(rptArray[1]);
								String state = rptArray[3];
								
								delivVos.add(new DelivVo(msgId, channel.getId(), state, time));
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
						if(delivVos.size() > 0){
							DatabaseTransaction trans = new DatabaseTransaction(true);
							try {
								//保存报告到数据库
								new DbService(trans).batchAddDeliv(delivVos);
								trans.commit();
							} catch (Exception ex) {
								trans.rollback();
							} finally {
								trans.close();
							}
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