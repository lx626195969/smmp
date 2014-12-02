package com.ddk.smmp.channel.buyun_http.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.buyun_http.utils.Tools;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
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
		String encode = "UTF-8";
		
		StringBuffer idStringBuffer = new StringBuffer();
		
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		
		for(int i = 0; i < queueList.size(); i++){
			SmQueue queue = queueList.get(i);
			
			HttpClient httpClient = new HttpClient();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("account", channel.getAccount());
			paramMap.put("pswd", channel.getPassword());
			paramMap.put("mobile", queue.getPhone());
			paramMap.put("needstatus", "true");
			paramMap.put("extno", queue.getSendCode());
			
			try {
				paramMap.put("msg", URLEncoder.encode(queue.getContent(), encode));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			ChannelLog.log(
					logger,
					"send msg:account=" + paramMap.get("account") + ";pswd="
							+ paramMap.get("pswd") + ";mobile="
							+ paramMap.get("mobile") + ";msg="
							+ queue.getContent() + ";",
					LevelUtils.getSucLevel(channel.getId()));

			Object obj = httpClient.post(channel.getSubmitUrl() + "/HttpSendSM", paramMap, encode);
			
			//拼接队列ID串 用于后面批量删除队列
			idStringBuffer.append(queue.getId());
			if(i != queueList.size() - 1){
				idStringBuffer.append(",");
			}
			
			ChannelLog.log(logger, "recv msg:" + obj, LevelUtils.getSucLevel(channel.getId()));
			
			Integer seq = Tools.generateSeq();
			Long msgId = Long.parseLong(queue.getId() + "");//默认msgId
			String state = "MT:1:-1";//默认失败
			if(null != obj){
				String[] resultArray = obj.toString().split("\r\n|\n|\r");
				if(resultArray.length > 0){
					String[] msgStrArray = resultArray[0].split(",");
					if(msgStrArray.length == 2){
						if(msgStrArray[1].equals("0")){
							state = "MT:0";
							msgId = Long.parseLong(resultArray[1]);
						}else{
							state = "MT:1:" + msgStrArray[1];
						}
					}
				}
			}else{
				state = "MT:1:408";//代表未给响应或者异常
			}
			
			submitVos.add(new SubmitVo(queue.getId(), seq, channel.getId()));
			for(int j = 1;j <= queue.getNum(); j++){
				if(state.startsWith("MT:1:")){
					//系统产生对应发送数量的MR:0008的状态报告
					Long tempMsgId = Long.parseLong(msgId.toString() + j);
					submitRspVos.add(new SubmitRspVo(seq, queue.getId(), tempMsgId, channel.getId(), state));
					delivVos.add(new DelivVo(tempMsgId, channel.getId(), "MR:0008", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
				}else{
					submitRspVos.add(new SubmitRspVo(seq, queue.getId(), msgId, channel.getId(), state));
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