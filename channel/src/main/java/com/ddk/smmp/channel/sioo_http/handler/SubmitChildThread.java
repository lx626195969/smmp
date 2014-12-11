package com.ddk.smmp.channel.sioo_http.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.sioo_http.utils.Tools;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.DateUtils;
import com.ddk.smmp.utils.HttpClient;

/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 *         提交短信的线程
 */
public class SubmitChildThread extends Thread {
	private static final Logger logger = Logger.getLogger(SubmitChildThread.class);
	
	List<SmQueue> queueList = null;
	Channel channel = null;
	
	public SubmitChildThread(List<SmQueue> queueList, Channel channel) {
		setDaemon(true);
		this.queueList = queueList;
		this.channel = channel;
	}

	@Override
	public void run() {
		String encode = "utf-8";
		
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		
		for(int i = 0; i < queueList.size(); i++){
			SmQueue queue = queueList.get(i);
			
			HttpClient httpClient = new HttpClient();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uid", channel.getAccount());
			paramMap.put("auth", DigestUtils.md5Hex(channel.getCompanyCode() + channel.getPassword()));
			paramMap.put("mobile", queue.getPhone());
			String sign = getSign(queue.getContent());
			String finalContent = queue.getContent().replace("【" + sign + "】", "");
			paramMap.put("msg", finalContent);
			paramMap.put("expid", "0");
			paramMap.put("encode", encode);
			
			logger.info("send msg:uid=" + paramMap.get("uid") + ";auth=" + paramMap.get("auth") + ";mobile=" + paramMap.get("mobile") + ";msg=" + queue.getContent() + ";expid=0;");
			
			Object obj = httpClient.get(channel.getSubmitUrl() + "/", paramMap, encode);
			
			if(null != obj){
				logger.info("recv msg:" + obj);
				
				int seqOrMsgId = -1;
				int state = 0;
				if(obj.toString().startsWith("0")){
					seqOrMsgId = Integer.parseInt(obj.toString().substring(2));
				}else{
					state = Integer.parseInt(obj.toString());
					seqOrMsgId = Tools.generateSeq();
				}
				submitVos.add(new SubmitVo(queue.getId(), seqOrMsgId, channel.getId()));
				submitRspVos.add(new SubmitRspVo(seqOrMsgId, queue.getId(), seqOrMsgId, channel.getId(), getState(state)));
				if(state != 0){
					delivVos.add(new DelivVo(seqOrMsgId, channel.getId(), "MR:0008", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
				}
			}
		}
		
		if(submitVos.size() > 0){
			SmsCache.queue1.addAll(submitVos);
		}
		if(submitRspVos.size() > 0){
			SmsCache.queue2.addAll(submitRspVos);
		}
		if(delivVos.size() > 0){
			SmsCache.queue3.addAll(delivVos);
		}
	}
	
	/**
	 * 获取响应状态 对应字符串状态
	 * 
	 * @param result
	 * @return
	 */
	private String getState(int result){
		if(result == 0){
			return "MT:0";
		}else{
			return "MT:1:" + result;
		}
	}
	
	/**
     * 获取短信中的签名
     * 
     * @param content
     * @return
     */
    public static String getSign(String content) {
    	String sign = "";
	    Pattern pattern = Pattern.compile("(?<=\\【)[^\\】]+");  
	    Matcher matcher = pattern.matcher(content);
	    while(matcher.find())
	    {
	    	sign = matcher.group();
	    }
	    return sign;
	}
}