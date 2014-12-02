package com.ddk.smmp.channel.maiyuan_http.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.maiyuan_http.utils.Tools;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.DateUtils;
import com.ddk.smmp.utils.HttpClient;
import com.ddk.smmp.utils.JaxbUtils;

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
		String encode = "utf-8";
		
		StringBuffer idStringBuffer = new StringBuffer();
		
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		//List<SmtDelivVo> smtDelivVos = new LinkedList<SmtDelivVo>();
		
		for(int i = 0; i < queueList.size(); i++){
			SmQueue queue = queueList.get(i);
			
			HttpClient httpClient = new HttpClient();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", channel.getCompanyCode());
			paramMap.put("account", channel.getAccount());
			paramMap.put("password", channel.getPassword());
			paramMap.put("mobile", queue.getPhone());
			paramMap.put("content", queue.getContent());
			paramMap.put("action", "send");
			paramMap.put("extno", queue.getSendCode());
			
			ChannelLog.log(
					logger,
					"send msg:userid=" + paramMap.get("userid") + ";account="
							+ paramMap.get("account") + ";password="
							+ paramMap.get("password") + ";mobile="
							+ paramMap.get("mobile") + ";content="
							+ paramMap.get("content") + ";extno="
							+ paramMap.get("extno") + ";",
					LevelUtils.getSucLevel(channel.getId()));

			Object obj = httpClient.post(channel.getSubmitUrl() + "/sms.aspx", paramMap, encode);
		
			SubmitRsp submitRsp = null;
			if(null != obj){
				submitRsp = JaxbUtils.converyToJavaBean(obj.toString(), SubmitRsp.class);
			}
			
			//拼接队列ID串 用于后面批量删除队列
			idStringBuffer.append(queue.getId());
			if(i != queueList.size() - 1){
				idStringBuffer.append(",");
			}
			
			ChannelLog.log(logger, "recv msg:" + submitRsp, LevelUtils.getSucLevel(channel.getId()));
			
			Integer seq = Tools.generateSeq();
			Long msgId = Long.parseLong(queue.getId() + "");//默认msgId
			String state = "MT:1:408";//默认代表未给响应
			
			if(null != submitRsp){
				state = getState(submitRsp.getMessage());
				if(submitRsp.getReturnstatus().equalsIgnoreCase("success")){
					msgId = Long.parseLong(submitRsp.getTaskID());
				}
			}
			
			submitVos.add(new SubmitVo(queue.getId(), seq, channel.getId()));
			//smtDelivVos.add(new SmtDelivVo(submitRsp.getTaskID(), queue.getNum(), channel.getId()));
			
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
		
		if (submitVos.size() > 0) {
			SmsCache.queue1.addAll(submitVos);
		}
		if (submitRspVos.size() > 0) {
			SmsCache.queue2.addAll(submitRspVos);
		}
		if (delivVos.size() > 0) {
			SmsCache.queue3.addAll(delivVos);
		}
	}
	
	/**
	 * 获取响应状态 对应字符串状态
	 * 
	 * @param message
	 * @return
	 */
	private String getState(String message){
		if(message.equalsIgnoreCase("ok")){
			return "MT:0";
		}else if (message.equals("用户名或密码不能为空")) {
			return "MT:1:1";
		}else if (message.equals("发送内容包含sql注入字符")) {
			return "MT:1:2";
		}else if (message.equals("用户名或密码错误")) {
			return "MT:1:3";
		}else if (message.equals("短信号码不能为空")) {
			return "MT:1:4";
		}else if (message.equals("短信内容不能为空")) {
			return "MT:1:5";
		}else if (message.equals("包含非法字符：")) {
			return "MT:1:6";
		}else if (message.equals("对不起，您当前要发送的量大于您当前余额")) {
			return "MT:1:7";
		}else if (message.equals("扩展子号只能是数字")) {
			return "MT:1:8";
		}else if (message.equals("其他错误")) {
			return "MT:1:9";
		}else {
			return "MT:1:-1";
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