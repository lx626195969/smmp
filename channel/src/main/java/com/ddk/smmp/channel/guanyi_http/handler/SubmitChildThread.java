package com.ddk.smmp.channel.guanyi_http.handler;

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
import com.ddk.smmp.channel.guanyi_http.utils.Tools;
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
		String encode = "GBK";
		
		StringBuffer idStringBuffer = new StringBuffer();
		
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		
		for(int i = 0; i < queueList.size(); i++){
			SmQueue queue = queueList.get(i);
			
			HttpClient httpClient = new HttpClient();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("OperID", channel.getAccount());
			paramMap.put("OperPass", channel.getPassword());
			paramMap.put("DesMobile", queue.getPhone());
			try {
				paramMap.put("Content", URLEncoder.encode(queue.getContent(), encode));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			ChannelLog.log(
					logger,
					"send msg:OperID=" + paramMap.get("OperID") + ";OperPass="
							+ paramMap.get("OperPass") + ";DesMobile="
							+ paramMap.get("DesMobile") + ";Content="
							+ queue.getContent() + ";",
					LevelUtils.getSucLevel(channel.getId()));

			Object obj = httpClient.post(channel.getSubmitUrl() + "/submitMessageAll", paramMap, encode);
			
			//拼接队列ID串 用于后面批量删除队列
			idStringBuffer.append(queue.getId());
			if(i != queueList.size() - 1){
				idStringBuffer.append(",");
			}
			
			ChannelLog.log(logger, "recv msg:" + obj, LevelUtils.getSucLevel(channel.getId()));
			
			Integer seq = Tools.generateSeq();
			Long msgId = Long.parseLong(queue.getId() + "");//默认msgId
			int state = 3;//默认成功
			if(null != obj){
				if(obj.toString().startsWith("03")){
					msgId = Long.parseLong(obj.toString().substring(4));
				}else{
					state = Integer.parseInt(obj.toString());
				}
			}else{
				state = 408;//代表未给响应
			}
			submitVos.add(new SubmitVo(queue.getId(), seq, channel.getId()));
			String state_ = getState(state);
			for(int j = 1;j <= queue.getNum(); j++){
				if(state_.startsWith("MT:1:")){
					//系统产生对应发送数量的MR:0008的状态报告
					Long tempMsgId = Long.parseLong(msgId.toString() + j);
					submitRspVos.add(new SubmitRspVo(seq, queue.getId(), tempMsgId, channel.getId(), state_));
					delivVos.add(new DelivVo(tempMsgId, channel.getId(), "MR:0008", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
				}else{
					submitRspVos.add(new SubmitRspVo(seq, queue.getId(), msgId, channel.getId(), state_));
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
		if(result == 3){
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