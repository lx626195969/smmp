package com.ddk.smmp.channel.jiaying_http.handler;

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
import com.ddk.smmp.channel.jiaying_http.utils.Tools;
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
 * 
 * @author leeson 2014年10月22日 上午11:22:42 li_mr_ceo@163.com <br>
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
		String encode = "GBK";
		
		StringBuffer idStringBuffer = new StringBuffer();
		
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		//List<SmtDelivVo> smtDelivVos = new LinkedList<SmtDelivVo>();
		
		for(int i = 0; i < queueList.size(); i++){
			Integer msgId = Tools.generateSeq();
			
			SmQueue queue = queueList.get(i);
			
			HttpClient httpClient = new HttpClient();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uid", channel.getAccount());
			paramMap.put("psw", DigestUtils.md5Hex(channel.getPassword()));
			paramMap.put("mobiles", queue.getPhone());
			paramMap.put("msg", queue.getContent());
			paramMap.put("cmd", "send");
			paramMap.put("msgid", msgId + "");
			
			ChannelLog.log(
					logger,
					"send msg:uid=" + paramMap.get("uid") + ";psw="
							+ paramMap.get("psw") + ";mobiles="
							+ paramMap.get("mobiles") + ";msg="
							+ paramMap.get("msg") + ";msgid="
							+ paramMap.get("msgid") + ";",
					LevelUtils.getSucLevel(channel.getId()));

			Object obj = httpClient.get(channel.getSubmitUrl(), paramMap, encode);
			
			//拼接队列ID串 用于后面批量删除队列
			idStringBuffer.append(queue.getId());
			if(i != queueList.size() - 1){
				idStringBuffer.append(",");
			}
			
			ChannelLog.log(logger, "recv msg:" + obj, LevelUtils.getSucLevel(channel.getId()));
			
			String state = "MT:0";
			if(null != obj){
				if(!"100".equals(obj.toString())){
					state = "MT:1:" + obj.toString();
				}
			}else{
				state = "MT:1:408";//代表没给响应
			}
			
			submitVos.add(new SubmitVo(queue.getId(), msgId, channel.getId()));
			//smtDelivVos.add(new SmtDelivVo(submitRsp.getTaskID(), queue.getNum(), channel.getId()));
			
			for(int j = 1;j <= queue.getNum(); j++){
				if (state.startsWith("MT:1:")) {
					//系统产生对应发送数量的MR:0008的状态报告
					submitRspVos.add(new SubmitRspVo(msgId, queue.getId(), msgId, channel.getId(), state));
					delivVos.add(new DelivVo(msgId, channel.getId(), "MR:0008", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
				} else {
					submitRspVos.add(new SubmitRspVo(msgId, queue.getId(), msgId, channel.getId(), state));
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