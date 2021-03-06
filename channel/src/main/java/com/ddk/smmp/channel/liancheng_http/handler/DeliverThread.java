package com.ddk.smmp.channel.liancheng_http.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;
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
				List<MtVo> mtVos = new LinkedList<MtVo>();
				
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
							String[] delivArray = resultArray[i].split(",");
							String format = delivArray[0];
							String mobile = delivArray[3];
							String content = delivArray[5];
							
							try {
								if(format.equals("0")){
									content = new String(content.getBytes("GBK"), "UTF-8");
								}
								if(format.equals("1")){
									content = URLDecoder.decode(content, "GBK");
								}
								if(format.equals("3")){
									content = new String(content.getBytes("UnicodeBigUnmarked"), "UTF-8");
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							
							mtVos.add(new MtVo(2, channel.getId(), mobile, content, channel.getAccount()));
						}
					}
				}
				
				if(mtVos.size() > 0){
					SmsCache.queue4.addAll(mtVos);
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
	
	public static void main(String[] args) {

	}
}