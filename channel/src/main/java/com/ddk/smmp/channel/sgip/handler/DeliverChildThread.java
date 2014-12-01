package com.ddk.smmp.channel.sgip.handler;

import java.util.LinkedList;
import java.util.List;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.sgip.msg.Deliver;
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.thread.SmsCache;


/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverChildThread extends Thread {
	List<Deliver> tempList = null;
	Channel channel = null;
	
	public DeliverChildThread(List<Deliver> tempList, Channel channel) {
		setDaemon(true);
		this.channel = channel;
		this.tempList = tempList;
	}
	
	@Override
	public void run() {
		List<MtVo> mtVos = new LinkedList<MtVo>();
		
		for(Deliver deliver : tempList){
			int index = 1;
			int totle = 1;
			if(deliver.getSm().isSuper()){
				byte[] contentBytes = deliver.getSm().getData().getBuffer();
				index = contentBytes[5];
				totle = contentBytes[4];
			}
			
			String userNumber = deliver.getUserNumber();
			if(userNumber.length() > 11){
				userNumber = userNumber.substring(userNumber.length() - 11, userNumber.length());
			}
			
			mtVos.add(new MtVo(2, channel.getId(), deliver.getSpNumber(), 1, userNumber, deliver.getSm().getMessage(), index, totle));
		}
		
		if(mtVos.size() > 0){
			SmsCache.queue4.addAll(mtVos);
		}
	}
}