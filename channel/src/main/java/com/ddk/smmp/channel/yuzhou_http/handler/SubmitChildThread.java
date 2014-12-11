package com.ddk.smmp.channel.yuzhou_http.handler;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.yuzhou_http.utils.Tools;
import com.ddk.smmp.channel.yuzhou_http.utils.UrlConnection;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.DateUtils;
import com.ddk.smmp.utils.JaxbUtils;

/**
 * 
 * @author leeson 2014年10月22日 上午11:22:42 li_mr_ceo@163.com <br>
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
		String encode = "utf-8";
		
		List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
		List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
		List<DelivVo> delivVos = new LinkedList<DelivVo>();
		
		String timestamp = DateUtils.dateToString(new Date(), "yyMMddHHmm");
		String signature = DigestUtils.md5Hex(channel.getPassword() + timestamp);
		
		for(int i = 0; i < queueList.size(); i++){
			SmQueue queue = queueList.get(i);
			
			MtPacket mtPacket = new MtPacket(channel.getCompanyCode(), "0", queue.getId(), queue.getPhone(), StringUtils.isEmpty(queue.getSendCode()) ? "" : queue.getSendCode(), queue.getContent(), signature, timestamp, "0", "");
			
			String xml = JaxbUtils.convertToXml(mtPacket, encode);
			//System.out.println(xml);
			String resp = UrlConnection.doURL(channel.getSubmitUrl(), xml);
			
			ChannelLog.log(logger, "send msg:" + mtPacket.toString(), LevelUtils.getSucLevel(channel.getId()));

			MtResponse mtResponse = null;
			if(null != resp){
				mtResponse = JaxbUtils.converyToJavaBean(resp, MtResponse.class);
			}
			
			ChannelLog.log(logger, "recv msg:" + mtResponse, LevelUtils.getSucLevel(channel.getId()));
			
			Integer seq = Tools.generateSeq();
			String state = "MT:1:408";//默认为未给响应
			if(null != mtResponse && StringUtils.isNotEmpty(mtResponse.getResult())){
				state = mtResponse.getResult().equals("0") ? "MT:0" : "MT:1:" + mtResponse.getResult();
			}
			
			submitVos.add(new SubmitVo(queue.getId(), seq, channel.getId()));
			for(int j = 1;j <= queue.getNum(); j++){
				if(state.startsWith("MT:1:")){
					//系统产生对应发送数量的MR:0008的状态报告
					submitRspVos.add(new SubmitRspVo(seq, queue.getId(), queue.getId(), channel.getId(), state));
					delivVos.add(new DelivVo(queue.getId(), channel.getId(), "MR:0008", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
				}else{
					submitRspVos.add(new SubmitRspVo(seq, queue.getId(), queue.getId(), channel.getId(), state));
				}
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
	
	public static void main(String[] args) {
		MtPacket mtPacket = new MtPacket("123", "0", 1234, "15214388466", "123131", "test message", "13132131", "1411071743", "0", null);
		
		String resp = UrlConnection.doURL("http://localhost:10000/sms", JaxbUtils.convertToXml(mtPacket, "utf-8"));
		System.out.println(resp);
	}
}