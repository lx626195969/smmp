package com.ddk.smmp.channel.cmpp._2.handler;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.cmpp._2.helper.LongSMByte;
import com.ddk.smmp.channel.cmpp._2.helper.ShortMessage;
import com.ddk.smmp.channel.cmpp._2.msg.Submit;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.thread.SmsCache;

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
		long s = System.currentTimeMillis();
		
		StringBuffer idStringBuffer = new StringBuffer();
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		
		for(int i = 0; i < queueList.size(); i++){
			SmQueue queue = queueList.get(i);
			
			//拼接队列ID串 用于后面批量删除队列
			idStringBuffer.append(queue.getId());
			if(i != queueList.size() - 1){
				idStringBuffer.append(",");
			}
			
			Submit submit = new Submit();
			submit.setMsgId(0l);
			submit.setSrcId(queue.getSendCode());// 服务代码
			submit.setMsgSrc(channel.getCompanyCode());// 企业代码
			submit.setDestTermIdCount((byte) 1);
			submit.setDestTermId(new String[] { queue.getPhone() });// 接收号码
			submit.setServiceId("");
			submit.assignSequenceNumber();

			List<byte[]> smArray = LongSMByte.getLongByte(channel.getSupportLen(), channel.getSignNum(), queue.getContent());
			
			if (smArray.size() > 0) {
				// 长短信
				submit.setTpUdhi((byte) 1);
				for (byte[] SM : smArray) {
					ShortMessage sm = new ShortMessage();
					sm.setMessage(SM, (byte) 8);
					submit.setSm(sm);
					channel.getSession().write(submit);
				}
			} else {
				// 不超过140的短信
				ShortMessage sm = new ShortMessage();
				
				byte[] msgBytes = null;
				try {
					msgBytes = queue.getContent().getBytes("UnicodeBigUnmarked");
				} catch (UnsupportedEncodingException e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e);
				}
				
				if(null != msgBytes){
					sm.setMessage(msgBytes, (byte) 8);
					submit.setSm(sm);
					channel.getSession().write(submit);
				}
			}
			
			//将rid和seq关联 放入缓存
			ChannelCacheUtil.put("channel_" + channel.getId() + "_seq_cache", submit.getSequenceNumber(), queue.getId());
			//添加消息到集合 便于后面做批处理
			submitVos.add(new SubmitVo(queue.getId(), submit.getSequenceNumber(), channel.getId()));
		}
		
		if(submitVos.size() > 0){
			SmsCache.queue1.addAll(submitVos);
		}
		
		long e = System.currentTimeMillis();
		ChannelLog.log(logger, "发送消息[" + queueList.size() + "]条|耗时" + (e - s), LevelUtils.getErrLevel(channel.getId()));
	}
}