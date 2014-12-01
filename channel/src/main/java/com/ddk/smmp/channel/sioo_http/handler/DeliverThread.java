package com.ddk.smmp.channel.sioo_http.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.MtVo;
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
				paramMap.put("uid", channel.getAccount());
				paramMap.put("auth", DigestUtils.md5Hex(channel.getCompanyCode() + channel.getPassword()));
				
				Object obj = httpClient.get(channel.getSubmitUrl() + "/mo", paramMap, "utf-8");
				
				if(null != obj){
					String data = obj.toString();
					if(!data.startsWith("0") && !data.startsWith("-")){
						
						logger.info("receive deliver:uid=" + paramMap.get("uid") + ";auth=" + paramMap.get("auth") + ";");
						logger.info(data);
						
						String[] mosArray = data.split("\n");
						for(int i = 1; i < mosArray.length; i++){
							String[] moArray = mosArray[i].split("##");
							
							try {
								mtVos.add(new MtVo(2, channel.getId(), moArray[2], URLDecoder.decode(moArray[3], "GBK"), channel.getAccount() + "#" + moArray[1]));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
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
}