package com.ddk.smmp.channel.smgp.handler;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.smgp.helper.LongSMByte;
import com.ddk.smmp.channel.smgp.helper.ShortMessage;
import com.ddk.smmp.channel.smgp.msg.Submit;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.service.DbService;

/**
 * @author leeson 2014-6-12 下午01:05:57 li_mr_ceo@163.com <br>
 *         提交短信的线程
 */
public class SubmitChildThread extends Thread {
	List<SmQueue> queueList = null;
	Channel channel = null;
	
	public SubmitChildThread(List<SmQueue> queueList, Channel channel) {
		setDaemon(true);
		this.queueList = queueList;
		this.channel = channel;
	}

	@Override
	public void run() {
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
			submit.setSrcTermId(queue.getSendCode());// 服务代码
			submit.setDestTermIdCount((byte) 1);
			submit.setDestTermId(new String[] { queue.getPhone() });// 接收号码
			submit.assignSequenceNumber();

			List<byte[]> smArray = LongSMByte.getLongByte(channel.getSupportLen(), channel.getSignNum(), queue.getContent());
			
			if (smArray.size() > 0) {
				// 长短信
				submit.setSuper(true);
				submit.setPkTotle(smArray.size());
				for (int j = 1; j <= smArray.size(); j++) {
					byte[] SM = smArray.get(j);
					ShortMessage sm = new ShortMessage();
					sm.setMessage(SM, (byte) 8);
					
					submit.setSm(sm);
					submit.setPkNumber(j);
					
					channel.getSession().write(submit);
				}
			} else {
				// 不超过140的短信
				ShortMessage sm = new ShortMessage();
				
				byte[] msgBytes = null;
				try {
					msgBytes = queue.getContent().getBytes("UnicodeBigUnmarked");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
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
		
		//批量添加消息提交数据
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService service = new DbService(trans);
			service.batchAddSubmit(submitVos);//保存提交消息
			//service.batchDelQueue(channel.getId(), idStringBuffer.toString());//批量删除队列表记录
			trans.commit();
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
}