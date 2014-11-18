package com.ddk.smmp.channel.smgp.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.smgp.msg.Deliver;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014-6-11 下午05:16:14 li_mr_ceo@163.com <br>
 * 状态报告处理线程
 */
public class DeliverThread extends Thread {
	private static final Logger logger = Logger.getLogger(DeliverThread.class);
	
	/** 待处理报告消息队列 */
	public BlockingQueue<Deliver> queue = new LinkedBlockingQueue<Deliver>();
	Channel channel = null;
	boolean isContinue = true;
	private ExecutorService delivrdThreadPool = null;
	private long lastDrainToTime;
	
	public DeliverThread(Channel channel) {
		setDaemon(true);
		this.channel = channel;
		delivrdThreadPool = Executors.newFixedThreadPool(10);
		lastDrainToTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		while (isContinue) {
			List<Deliver> tempList = new LinkedList<Deliver>();
			try {
				//每晚23:58分暂停操作
				if(ConstantUtils.isPause_23_58()){
					Thread.sleep(10 * 60 * 1000);
				}
				
				if(queue.size() >= 200 || isDrainTo()){
					int num = queue.drainTo(tempList, 200);
					lastDrainToTime = System.currentTimeMillis();
					
					if(num > 0){
						delivrdThreadPool.execute(new DeliverChildThread(tempList, channel));
					}
				}else{
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
			}
		}
	}

	/**
	 * 终止线程
	 */
	public void stop_() {
		synchronized (this) {
			isContinue = false;
			delivrdThreadPool.shutdown();
		}
	}
	
	/**
	 * 距离上次处理响应时间是否过去5S
	 * 
	 * @return
	 */
	private boolean isDrainTo(){
		return ((System.currentTimeMillis() - lastDrainToTime) >= 1000 * 10);
	}
}