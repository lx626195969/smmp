package com.ddk.smmp.channel.liancheng_http.handler;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.liancheng_http.utils.Tools;
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

	@SuppressWarnings("deprecation")
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
			paramMap.put("msgFormat", "1");
			paramMap.put("corpID", channel.getCompanyCode());
			paramMap.put("loginName", channel.getAccount());
			paramMap.put("password", channel.getPassword());
			paramMap.put("Mobs", queue.getPhone());
			paramMap.put("msg",  URLEncoder.encode(queue.getContent()));
			paramMap.put("mtLevel", "1");
			paramMap.put("subNumber", queue.getSendCode());
			paramMap.put("linkID", "");
			paramMap.put("kindFlag", "");
			paramMap.put("MD5str", "");

			ChannelLog.log(logger, "send msg:Mobs=" + paramMap.get("Mobs")
					+ ";msg=" + queue.getContent() + ";",
					LevelUtils.getSucLevel(channel.getId()));
			
			Object obj = httpClient.get(channel.getSubmitUrl() + "/putMt/", paramMap, encode);
			
			//拼接队列ID串 用于后面批量删除队列
			idStringBuffer.append(queue.getId());
			if(i != queueList.size() - 1){
				idStringBuffer.append(",");
			}
			
			ChannelLog.log(logger, "recv msg:" + obj, LevelUtils.getSucLevel(channel.getId()));
			
			Integer seq = Tools.generateSeq();
			Long msgId = Long.parseLong(queue.getId() + "");//默认msgId
			String state = "MT:0";//默认成功
			if(null != obj){
				String[] resultArray = obj.toString().split("\r\n|\n|\r");
				if(resultArray.length > 0 && resultArray[0].equals("100")){
					String[] msgStrArray = resultArray[1].split(",");
					msgId = Long.parseLong(msgStrArray[1]);
				}else{
					state = "MT:1:" + resultArray[0];
				}
			}else{
				state = "MT:1:408";//代表未给响应
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
}