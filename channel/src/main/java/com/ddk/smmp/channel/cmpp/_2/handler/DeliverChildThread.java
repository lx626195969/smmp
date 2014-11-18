package com.ddk.smmp.channel.cmpp._2.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.cmpp._2.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._2.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._2.msg.Deliver;
import com.ddk.smmp.channel.cmpp._2.utils.Tools;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;
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
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
				} catch (ParseException e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
				}
			}
			//短信
			else{
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					int index = 1;
					int totle = 1;
					if(deliver.getSm().isSuper()){
						byte[] contentBytes = deliver.getSm().getData().getBuffer();
						index = contentBytes[5];
						totle = contentBytes[4];
					}
					new DbService(trans).processMo(channel.getId(), deliver.getDstId(), 2, deliver.getSrcTermId(), deliver.getSm().getMessage(), index, totle);
					trans.commit();
				} catch (Exception ex) {
					ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}
		
		if(delivVos.size() > 0){
			SmsCache.queue3.addAll(delivVos);
		}
	}
}