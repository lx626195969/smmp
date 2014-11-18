package com.ddk.smmp.channel.sgip.handler;

import java.util.List;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.sgip.msg.Deliver;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;


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
		this.channel = channel;
		this.tempList = tempList;
	}

	@Override
	public void run() {
		for(Deliver deliver : tempList){
			//短信
			DatabaseTransaction trans = new DatabaseTransaction(true);
			try {
				int index = 1;
				int totle = 1;
				if(deliver.getSm().isSuper()){
					byte[] contentBytes = deliver.getSm().getData().getBuffer();
					index = contentBytes[5];
					totle = contentBytes[4];
				}
				
				new DbService(trans).processMo(channel.getId(), deliver.getSpNumber(), 1, deliver.getUserNumber(), deliver.getSm().getMessage(), index, totle);
				
				trans.commit();
			} catch (Exception ex) {
				ChannelLog.log(logger, ex.getMessage(), LevelUtils.getSucLevel(channel.getId()), ex.getCause());
				trans.rollback();
			} finally {
				trans.close();
			}
		}
	}
}