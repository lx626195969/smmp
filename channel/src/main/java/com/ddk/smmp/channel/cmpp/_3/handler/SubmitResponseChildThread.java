package com.ddk.smmp.channel.cmpp._3.handler;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.cmpp._3.msg.SubmitResp;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;


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
		
		for(SubmitResp resp : tempList){
			//通过seq取出rid
			Integer rid = ChannelCacheUtil.get(Integer.class, "channel_" + channel.getId() + "_seq_cache", resp.getSequenceNumber());
			if(null != rid){
				submitRspVos.add(new SubmitRspVo(resp.getSequenceNumber(), rid, resp.getMsgId(), channel.getId(), getState(resp.getResult())));
			}else{
				ChannelLog.log(logger, "未找到序列:" + resp.getSequenceNumber(), LevelUtils.getErrLevel(channel.getId()));
			}
		}
		
		if(submitRspVos.size() > 0){
			SmsCache.queue2.addAll(submitRspVos);
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