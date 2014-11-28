package com.ddk.smmp.web;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.restlet.Component;
import org.restlet.data.Protocol;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelAdapter;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.jdbc.database.DruidDatabaseConnectionPool;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;
import com.ddk.smmp.thread.AddSmsTimer;
import com.ddk.smmp.thread.RunChannelTimer;

/**
 * @author leeson 2014年7月18日 下午3:07:53 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelServer {
	static{
		DOMConfigurator.configureAndWatch(Class.class.getClass().getResource("/").getPath() + "log4j.xml");
	}
	
	private static final Logger logger = Logger.getLogger(ChannelServer.class);
	
	// 增加或减少天数
	public static Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
	
	public static void main(String[] args) throws Exception {
		ChannelServer smsiServer = new ChannelServer();
		smsiServer.start();
	}
	
	Component comp = null;
	
	public void start() throws Exception {
		DruidDatabaseConnectionPool.startup();
		logger.info("========>DruidDatabaseConnectionPool is running......");
		
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		int bindPort = Integer.parseInt(bundle.getString("channel.server.bindport"));
		
		comp = new Component();
		comp.getClients().add(Protocol.HTTP);
		comp.getServers().add(Protocol.HTTP, bindPort);
		comp.getDefaultHost().attach("/", IndexAction.class);
		comp.getDefaultHost().attach("/channel", ChannelAciton.class);
		comp.start();
		
		logger.info("running channel server[" + bindPort + "] ......");
		/* ============================================================== */
		//23:58暂停发送
		Calendar s_cal = Calendar.getInstance();
		s_cal.set(Calendar.HOUR_OF_DAY, 23);
		s_cal.set(Calendar.MINUTE, 58);
		s_cal.set(Calendar.SECOND, 0);
		Date s_date = s_cal.getTime();
		if (s_date.before(new Date())) {
			s_date = addDay(s_date, 1);
		}
		Timer s_timer = new Timer();
		s_timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ConstantUtils.isPause = true;
			}
		}, s_date, 24 * 60 * 60 * 1000);
		//00:05恢复发送
		Calendar e_cal = Calendar.getInstance();
		e_cal.set(Calendar.HOUR_OF_DAY, 0);
		e_cal.set(Calendar.MINUTE, 5);
		e_cal.set(Calendar.SECOND, 0);
		
		Date e_date = e_cal.getTime();
		if (e_date.before(new Date())) {
			e_date = addDay(e_date, 1);
		}
		Timer e_timer = new Timer();
		e_timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ConstantUtils.isPause = false;
			}
		}, e_date, 24 * 60 * 60 * 1000);

		/* ============================================================== */
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				List<String> currentThreadNames = ChannelAdapter.findAllThreadNames();
				
				logger.error("当前通道线程数：" + currentThreadNames.size());
				logger.error("详细：" + Arrays.toString(currentThreadNames.toArray()));
				
				for(String threadName : ChannelAdapter.CHANNEL_THREAD_NAME_LIST){
					int channelId = Integer.parseInt(threadName.replaceAll("ChannelThread_", ""));
					
					if(!currentThreadNames.contains(threadName)){
						Channel channel = null;
						
						DatabaseTransaction trans = new DatabaseTransaction(true);
						try {
							DbService dbService = new DbService(trans);
							channel = dbService.getChannel(channelId);
							dbService.addChannelLog(channelId, channel.getName(), "通道重连");
							trans.commit();
						} catch (Exception ex) {
							trans.rollback();
						} finally {
							trans.close();
						}
						
						ChannelLog.log(logger, "监听到" + threadName + "关闭, 通道[" + channel.getName() + "-" + channel.getId() + "]开始重连", LevelUtils.getSucLevel(channel.getId()));
						
						ChannelAdapter.getInstance().start(channel);
					}
				}
			}
		}, 0, 1 * 30, TimeUnit.SECONDS);
		/* ============================================================== */
		
		/* ============================================================== */
		RunChannelTimer runChannelTimer = new RunChannelTimer();
		runChannelTimer.start();
		logger.info("========>RunChannelTimer is running......");
		/* ============================================================== */
		
		/* ============================================================== */
		AddSmsTimer addSmsTimer = new AddSmsTimer();
		addSmsTimer.start();
		logger.info("========>AddSmsTimer is running......");
		/* ============================================================== */
	}

	public void stop() throws Exception {
		DruidDatabaseConnectionPool.shutdown();
		logger.info("DruidDatabaseConnectionPool is stop......");
		
		if (null != comp) {
			comp.stop();
			comp = null;
		}
	}
}