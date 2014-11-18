package com.ddk.smmp.channel.smgp.handler;

import java.util.LinkedList;
import java.util.List;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.smgp.msg.SubmitResp;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;


/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitResponseChildThread extends Thread {
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
				submitRspVos.add(new SubmitRspVo(resp.getSequenceNumber(), rid, Long.parseLong(resp.getMsgId()), channel.getId(), getState(resp.getResult())));
			}
		}
		
		if(submitRspVos.size() > 0){
			//保存响应消息到数据库
			DatabaseTransaction trans = new DatabaseTransaction(true);
			try {
				new DbService(trans).batchAddSubmitRsp(submitRspVos);
				trans.commit();
			} catch (Exception ex) {
				trans.rollback();
			} finally {
				trans.close();
			}
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