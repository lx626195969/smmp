package com.ddk.smmp.channel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * @author leeson 2014年6月16日 上午11:51:39 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelAdapter {
	private static final Logger logger = Logger.getLogger(ChannelAdapter.class);
	
	private static ChannelAdapter adapter = null;
	
	public static Set<String> CHANNEL_THREAD_NAME_LIST = new HashSet<String>();

	public static ChannelAdapter getInstance() {
		if (null == adapter) {
			adapter = new ChannelAdapter();
		}
		return adapter;
	}
	
	/**
	 * 启动通道
	 * 
	 * @param channel
	 * @return true启动成功 false启动失败
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean start(Channel channel){
		ChannelCacheUtil.put("channel_cache", "channel_" + channel.getId(), channel);
		
		for(Thread thread : findAllThreads()){
			String name = thread.getName();
			if(name.equals("ChannelThread_" + channel.getId())){
				logger.info("该通道正在运行中:" + "channel_" + channel.getId());
				return false;
			}
		}
		
		String run_class = null;//协议 入口程序
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			run_class = new DbService(trans).getProtocolRunClass(channel.getProtocolType());
			trans.commit();
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
		
		if(StringUtils.isNotEmpty(run_class)){
			Client client = null;
			try {
				Class<? extends Client> clazz = (Class<? extends Client>) Class.forName(run_class);
				Class[] paramTypes = new Class[]{ Class.forName("com.ddk.smmp.channel.Channel") };
				Constructor<? extends Client> ctor = clazz.getConstructor(paramTypes);
				client = ctor.newInstance(new Object[]{ channel });
				
				Thread channelThread = new Thread(new ThreadGroup("ChannelThreadGroup"), new ChannelThread(client), "ChannelThread_" + channel.getId());
				channelThread.setDaemon(true);
				channelThread.start();
				
				CHANNEL_THREAD_NAME_LIST.add("ChannelThread_" + channel.getId());
				
				Thread.sleep(5 * 1000);//10S等待通道启动完成
				
				//启动成功将通道加入缓存
				if(channel.getStatus().intValue() == 1){
					return true;
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 停止通道
	 * 
	 * @param channelId
	 */
	@SuppressWarnings("deprecation")
	public void stop(int channelId) {
		Channel channel = ChannelCacheUtil.get(Channel.class, "channel_cache", "channel_" + channelId);
		
		if(null != channel){
			channel.setStatus(Channel.STOP_STATUS);
			
			for(Thread thread : findAllThreads()){
				String name = thread.getName();
				if(name.equals("ChannelThread_" + channelId)){
					thread.interrupt();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					thread.stop();
					
					break;
				}
			}
		}
		
		CHANNEL_THREAD_NAME_LIST.remove("ChannelThread_" + channelId);
		
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService dbService = new DbService(trans);
			dbService.addChannelLog(channel.getId(), channel.getName(), "通道停止");
			dbService.updateChannelStatus(channel.getId(), 2);
			trans.commit();
		} catch (Exception ex) {
			ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	public static Thread[] findAllThreads() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;

		// 遍历线程组树，获取根线程组
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}
		// 激活的线程数加倍
		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];
		// 获取根线程组的所有线程
		int actualSize = topGroup.enumerate(slackList);
		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);
		return list;
	}
	
	public static List<String> findAllThreadNames() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;

		// 遍历线程组树，获取根线程组
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}
		// 激活的线程数加倍
		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];
		// 获取根线程组的所有线程
		int actualSize = topGroup.enumerate(slackList);
		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);
		
		List<String> threadNames = new ArrayList<String>();
				
		for(Thread thread : list){
			if(thread.getName().startsWith("ChannelThread_")){
				threadNames.add(thread.getName());
			}
		}
		
		return threadNames;
	}
}