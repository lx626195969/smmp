package com.ddk.smmp.channel.dingyuan_http.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.gdydADC.utils.Tools;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.DateUtils;
import com.ddk.smmp.utils.HttpClient;
import com.ddk.smmp.utils.MemCachedUtil;

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
		
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		
		for(int i = 0; i < queueList.size(); i++){
			SmQueue queue = queueList.get(i);
			
			HttpClient httpClient = new HttpClient();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("username", channel.getAccount());
			paramMap.put("password", DigestUtils.md5Hex(channel.getAccount() + DigestUtils.md5Hex(channel.getPassword())));
			paramMap.put("mobile", queue.getPhone());
			paramMap.put("content", queue.getContent());
			
			ChannelLog.log(logger,
					"send msg:username=" + paramMap.get("username")
							+ ";password=" + paramMap.get("password")
							+ ";mobile=" + paramMap.get("mobile") + ";content="
							+ queue.getContent() + ";",
					LevelUtils.getSucLevel(channel.getId()));

			Object obj = httpClient.post(channel.getSubmitUrl() + "/smsSend.do", paramMap, encode);
			
			ChannelLog.log(logger, "recv msg:" + obj, LevelUtils.getSucLevel(channel.getId()));
			
			Integer seq = Tools.generateSeq();
			Long msgId = Long.parseLong(queue.getId() + "");//默认msgId
			String state = "MT:0";//默认成功
			if(null != obj){
				Long result = Long.parseLong(obj.toString());
				if(result.longValue() > 0){
					msgId = result;
				}else{
					state = "MT:1:" + result;
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
			
			if(state.equals("MT:0")){
				//添加数量缓存方便报告回来时 生成对应条数报告
				MemCachedUtil.set("deliv_cache", channel.getId() + "_" + msgId, queue.getNum(), 3 * 24 * 60 * 60);
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
}