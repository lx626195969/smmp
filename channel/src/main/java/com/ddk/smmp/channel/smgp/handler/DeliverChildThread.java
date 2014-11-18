package com.ddk.smmp.channel.smgp.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.msg.Deliver;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;


/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverChildThread extends Thread {
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
					String msgId = contentBuffer.removeStringEx(10);
					contentBuffer.removeBytes(3 + 3 + 10 + 10);
					String state = contentBuffer.removeStringEx(7);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					Date receiveTime = sdf.parse(deliver.getRecvTime());
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = sdf1.format(receiveTime);
					
					//添加报告到待处理集合
					delivVos.add(new DelivVo(Long.parseLong(msgId), channel.getId(), state, time));
				} catch (NotEnoughDataInByteBufferException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
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
					
					new DbService(trans).processMo(channel.getId(), deliver.getDstTermId(), 3, deliver.getSrcTermId(), deliver.getSm().getMessage(), index, totle);
					trans.commit();
				} catch (Exception ex) {
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}
	}
}