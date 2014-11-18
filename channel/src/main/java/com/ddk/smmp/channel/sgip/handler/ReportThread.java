package com.ddk.smmp.channel.sgip.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.sgip.msg.Report;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014年6月26日 下午4:57:05 li_mr_ceo@163.com <br>
 *
 */
public class ReportThread extends Thread {
	private static final Logger logger = Logger.getLogger(ReportThread.class);
	
	public BlockingQueue<Report> queue = new LinkedBlockingQueue<Report>();
	Channel channel = null;
	boolean isContinue = true;
	private ExecutorService reportThreadPool = null;
	private long lastDrainToTime;
	
	public ReportThread(Channel channel) {
		setDaemon(true);
		this.channel = channel;
		reportThreadPool = Executors.newFixedThreadPool(10);
		lastDrainToTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		while (isContinue) {
			List<Report> tempList = new LinkedList<Report>();
			try {
				//每晚23:58分暂停操作
				if(ConstantUtils.isPause_23_58()){
					Thread.sleep(10 * 60 * 1000);
				}
				
				if(queue.size() >= 200 || isDrainTo()){
					int num = queue.drainTo(tempList, 200);
					lastDrainToTime = System.currentTimeMillis();
					
					if(num > 0){
						reportThreadPool.execute(new ReportChildThread(tempList, channel));
					}
				}else{
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				ChannelLog.log(logger, e.getMessage(), LevelUtils.getSucLevel(channel.getId()), e.getCause());
			}
		}
	}

	/**
	 * 终止线程
	 */
	public void stop_() {
		synchronized (this) {
			isContinue = false;
			reportThreadPool.shutdown();
		}
	}
	
	/**
	 * 距离上次处理响应时间是否过去5S
	 * 
	 * @return
	 */
	private boolean isDrainTo(){
		return ((System.currentTimeMillis() - lastDrainToTime) >= 1000 * 5);
	}
}