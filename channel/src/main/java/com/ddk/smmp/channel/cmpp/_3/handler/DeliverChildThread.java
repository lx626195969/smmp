package com.ddk.smmp.channel.cmpp._3.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.msg.Deliver;
import com.ddk.smmp.channel.cmpp._3.utils.Tools;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;


/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverChildThread extends Thread {
	private static final Logger logger = Logger.getLogger(DeliverChildThread.class);
	
	List<Deliver> tempList = null;
	Channel channel = null;
	
	public DeliverChildThread(List<Deliver> tempList, Channel channel) {
		setDaemon(true);
		this.tempList = tempList;
		this.channel= channel;
	}

	@Override
	public void run() {
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		List<MtVo> mtVos = new LinkedList<MtVo>();
		
		for(Deliver deliver : tempList){
			//状态报告
			if(deliver.getIsReport() == 1){
				try {
					ByteBuffer contentBuffer = deliver.getSm().getData();
					long msgId = Tools.bytesToLong(contentBuffer.removeBytes(8).getBuffer());
					String state = contentBuffer.removeStringEx(7);
					contentBuffer.removeBytes(10);
					
					String doTime = contentBuffer.removeStringEx(10);
					SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
					Date receiveTime = sdf.parse(doTime);
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = sdf1.format(receiveTime);
					
					//添加报告到待处理集合
					delivVos.add(new DelivVo(msgId, channel.getId(), state, time));
				} catch (NotEnoughDataInByteBufferException e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e);
				} catch (ParseException e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e);
				}
			}
			//短信
			else{
				int index = 1;
				int totle = 1;
				if(deliver.getSm().isSuper()){
					byte[] contentBytes = deliver.getSm().getData().getBuffer();
					index = contentBytes[5];
					totle = contentBytes[4];
				}
				
				mtVos.add(new MtVo(1, channel.getId(), deliver.getDstId(), 2, deliver.getSrcTermId(), deliver.getSm().getMessage(), index, totle));
			}
		}
		
		if(delivVos.size() > 0){
			SmsCache.queue3.addAll(delivVos);
		}
		if(mtVos.size() > 0){
			SmsCache.queue4.addAll(mtVos);
		}
	}
}