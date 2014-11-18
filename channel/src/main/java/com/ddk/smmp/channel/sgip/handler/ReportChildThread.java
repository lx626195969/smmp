package com.ddk.smmp.channel.sgip.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.sgip.msg.Report;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.thread.SmsCache;


/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 * 
 */
public class ReportChildThread extends Thread {
	private static final Logger logger = Logger.getLogger(ReportChildThread.class);
	
	List<Report> tempList = null;
	Channel channel = null;
	
	public ReportChildThread(List<Report> tempList, Channel channel) {
		setDaemon(true);
		this.tempList = tempList;
		this.channel= channel;
	}
	
	@Override
	public void run() {
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		
		for(Report report : tempList){
			long msgId = Long.parseLong(report.getSeq1() + "" + report.getSeq3());
			
			String state = (report.getState() == 0) ? "DELIVRD" : "EXPIRED";
			
			Date receiveTime = report.getReceiveDate();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf1.format(receiveTime);
			
			//添加报告到待处理集合
			delivVos.add(new DelivVo(msgId, channel.getId(), state, time));
		}
		
		if(delivVos.size() > 0){
			SmsCache.queue3.addAll(delivVos);
		}
	}
}