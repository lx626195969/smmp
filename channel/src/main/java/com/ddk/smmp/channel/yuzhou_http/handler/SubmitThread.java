package com.ddk.smmp.channel.yuzhou_http.handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014年10月22日 上午11:22:09 li_mr_ceo@163.com <br>
 */
public class SubmitThread extends Thread{
	/** 网关每秒提交量限制（即队列大小） */
	private int smgFlowLimit = 100;
	/** 待发送消息队列 */
	private BlockingQueue<SmQueue> queue = null;
	/** 队列数据进入 线程池(定时获取) */
	private ScheduledThreadPoolExecutor watchDataThreadPool = null;
	/** 消息提交线程池 */
	private ExecutorService submitDataThreadPool = null;
	/** 通道 */
	private Channel channel = null;
	
	private long lastDrainToTime;
	
	/** 标识位 用来结束阻塞 */
	private boolean isContinue = true;
	
	public SubmitThread(Channel channel) {
		setDaemon(true);
		this.channel = channel;
		if(null != channel.getSubmitRate()){
			this.smgFlowLimit = channel.getSubmitRate();
		}
		this.queue = new LinkedBlockingDeque<SmQueue>(smgFlowLimit);
		this.watchDataThreadPool = new ScheduledThreadPoolExecutor(1);
		this.submitDataThreadPool = Executors.newFixedThreadPool(2);
		
		lastDrainToTime= System.currentTimeMillis();
	}

	@Override
	public void run() {
		startWatchData();
		startSubmitData();
	}
	
	/**
	 * 改变标志位停止线程
	 */
	public void stop_() {
		synchronized (this) {
			isContinue = false;
			watchDataThreadPool.shutdown();
			submitDataThreadPool.shutdown();
		}
	}

	/**
	 * 从数据库获取待发送数据
	 */
	private void startWatchData() {
		watchDataThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//数据库查询出来并锁定的数据
				List<SmQueue> temp = new ArrayList<SmQueue>();
				
				int limit = smgFlowLimit - queue.size();
				if(limit > 0){
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						temp = new DbService(trans).getMsgFromQueueAndLockMsg(channel.getId(), limit);
						trans.commit();
					} catch (Exception ex) {
						ChannelLog.log(Logger.getLogger(SubmitThread.class), ex.getMessage(), LevelUtils.getErrLevel(channel.getId()));
						trans.rollback();
					} finally {
						trans.close();
					}
					
					if(temp.size() > 0){
						queue.addAll(temp);
					}
				}
			}
		}, 1000, 1000, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 将数据提交到运营商
	 */
	private void startSubmitData() {
		while(isContinue){
			List<SmQueue> tempList = new LinkedList<SmQueue>();
			
			try {
				//每晚23:58分暂停操作
				if(ConstantUtils.isPause_23_58()){
					Thread.sleep(10 * 60 * 1000);
				}
				
				if(queue.size() >= (smgFlowLimit/2) || isDrainTo()){
					int num = queue.drainTo(tempList, 20);
					lastDrainToTime= System.currentTimeMillis();
					
					if(num > 0){
						submitDataThreadPool.execute(new SubmitChildThread(tempList, channel));
					}
				}else{
					Thread.sleep(1000);
				}
			} catch (InterruptedException e1) {
				ChannelLog.log(Logger.getLogger(SubmitThread.class), e1.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			}
		}
	}
	
	/**
	 * 距离上次处理时间是否过去10S
	 * 
	 * @return
	 */
	private boolean isDrainTo(){
		return ((System.currentTimeMillis() - lastDrainToTime) >= 1000 * 10);
	}
}