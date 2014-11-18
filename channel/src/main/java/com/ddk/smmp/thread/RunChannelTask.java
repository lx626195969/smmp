package com.ddk.smmp.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelAdapter;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014年7月29日 上午10:37:46 li_mr_ceo@163.com <br>
 *
 */
public class RunChannelTask extends TimerTask {
	private static final Logger logger = LoggerFactory.getLogger(RunChannelTask.class);
	
	Timer timer;
	
	public RunChannelTask(Timer timer) {
		super();
		this.timer = timer;
	}


	@Override
	public void run() {
		List<Channel> channels = new ArrayList<Channel>();
		
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			channels = new DbService(trans).getAllRunChannels();
			trans.commit();
			
			logger.info("查询到" + channels.size() + "个需要启动的通道");
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
    	
		for(Channel channel : channels){
			try {
				logger.info("开始启动通道:ID[" + channel.getId() + "]host[" + channel.getHost() + "]port[" + channel.getPort() + "]");
				
				ChannelAdapter.getInstance().start(channel);
				
				logger.info("启动完成通道:ID[" + channel.getId() + "]host[" + channel.getHost() + "]port[" + channel.getPort() + "]");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		timer.cancel();//取消定时任务 让定时任务只执行一次
		logger.info("销毁通道启动线程");
	}
}