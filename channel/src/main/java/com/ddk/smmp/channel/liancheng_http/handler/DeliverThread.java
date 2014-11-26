package com.ddk.smmp.channel.liancheng_http.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;
import com.ddk.smmp.utils.HttpClient;

/**
 * 
 * @author leeson 2014年6月26日 下午4:57:05 li_mr_ceo@163.com <br>
 *
 */
public class DeliverThread extends Thread {
	private static final Logger logger = Logger.getLogger(DeliverThread.class);
	
	Channel channel = null;
	private ScheduledThreadPoolExecutor receiveDataThreadPool = null;
	
	public DeliverThread(Channel channel) {
		setDaemon(true);
		this.channel = channel;
		this.receiveDataThreadPool = new ScheduledThreadPoolExecutor(1);
	}

	@Override
	public void run() {
		receiveDataThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new HttpClient();
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("msgFormat", "1");
				paramMap.put("corpID", channel.getCompanyCode());
				paramMap.put("loginName", channel.getAccount());
				paramMap.put("password", channel.getPassword());
				paramMap.put("MD5str", "");
				
				Object obj = httpClient.get(channel.getSubmitUrl() + "/getMo/", paramMap, "GBK");
				
				if(null != obj){
					ChannelLog.log(logger, "receive deliverStr:" + obj.toString(), LevelUtils.getSucLevel(channel.getId()));
					
					String[] resultArray = obj.toString().split("\r\n|\n|\r");
					if(resultArray.length > 0 && resultArray[0].equals("100")){
						for(int i = 1;i < resultArray.length; i++){
							String[] delivArray = resultArray[i].split("#");
							String mobile = delivArray[3];
							String content = delivArray[5];
							DatabaseTransaction trans = new DatabaseTransaction(true);
							try {
								new DbService(trans).process_http_Mo(channel.getId(), mobile, content, channel.getAccount());
								trans.commit();
							} catch (Exception ex) {
								ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()));
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