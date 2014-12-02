package com.ddk.smmp.channel.sgip.handler;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.sgip.msg.SubmitResp;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.DateUtils;


/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitResponseChildThread extends Thread {
	private static final Logger logger = Logger.getLogger(SubmitResponseChildThread.class);
	
	List<SubmitResp> tempList = null;
	Channel channel = null;
	
	public SubmitResponseChildThread(List<SubmitResp> tempList, Channel channel) {
		setDaemon(true);
		this.tempList = tempList;
		this.channel = channel;
	}

	@Override
	public void run() {
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		
		for(SubmitResp resp : tempList){
			int seq = resp.getSequenceNumber3();
			Long msgId = Long.parseLong(resp.getSequenceNumber2() + "" + resp.getSequenceNumber3());
			
			Integer rid = ChannelCacheUtil.get(Integer.class, "channel_" + channel.getId() + "_seq_cache", seq);
			
			if(null != rid){
				if(resp.getResult() != 0){
					//系统产生对应发送数量的MR:0008的状态报告
					submitRspVos.add(new SubmitRspVo(seq, rid, msgId, channel.getId(), getState(resp.getResult())));
					delivVos.add(new DelivVo(msgId, channel.getId(), "MR:0008", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
				}else{
					submitRspVos.add(new SubmitRspVo(seq, rid, msgId, channel.getId(), getState(resp.getResult())));
				}
			}else{
				ChannelLog.log(logger, "未找到序列:" + seq, LevelUtils.getErrLevel(channel.getId()));
			}
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
}