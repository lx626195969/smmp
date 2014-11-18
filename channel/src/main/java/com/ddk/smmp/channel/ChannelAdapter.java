package com.ddk.smmp.channel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.jdbc.database.DruidDatabaseConnectionPool;
import com.ddk.smmp.service.DbService;

/**
 * @author leeson 2014年6月16日 上午11:51:39 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelAdapter {
	private static final Logger logger = Logger.getLogger(ChannelAdapter.class);
	
	private static ChannelAdapter adapter = null;
	private Object LOCK = new Object();

	public static ChannelAdapter getInstance() {
		if (null == adapter) {
			adapter = new ChannelAdapter();
		}
		return adapter;
	}
	
	public static void main(String[] args){
		try {
			DruidDatabaseConnectionPool.startup();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		synchronized (LOCK) {
			Object object = ChannelCacheUtil.get("client", "channel_" + channel.getId());
			if(null != object){
				Client client = (Client)object;
				if(client.getChannel().getStatus() != 2){
					logger.info("该通道正在运行或者重连中:" + "channel_" + channel.getId());
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
					client.start();
					
					Thread.sleep(10000);//10S等待通道启动完成
					
					//启动成功将通道加入缓存
					if(client.status().intValue() == 1){
						ChannelCacheUtil.put("client", "channel_" + channel.getId(), client);
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
	}

	/**
	 * 停止通道
	 * 
	 * @param channelId
	 */
	public void stop(int channelId) {
		synchronized (LOCK) {
			Object object = ChannelCacheUtil.get("client", "channel_" + channelId);
			if(null != object){
				Client client = (Client)object;
				client.stop();
					
				try {
					Thread.sleep(3000);
					ChannelCacheUtil.remove("client", "channel_" + channelId);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取通道运行状态
	 * 
	 * @param channelId
	 * @return
	 */
	public Integer status(int channelId) {
		synchronized (LOCK) {
			Object object = ChannelCacheUtil.get("client", "channel_" + channelId);
			if(null != object){
				Client client = (Client)object;
				return client.getChannel().getStatus();
			}
			
			return null;
		}
	}
}