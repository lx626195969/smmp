package com.ddk.smmp.channel.sgip.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.sgip.msg.Bind;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 链路心跳检测线程
 * 
 * @author leeson 2014年6月26日 下午4:09:51 li_mr_ceo@163.com <br>
 *
 */
public class ActiveTestThread extends Thread {
	private static final Logger logger = Logger.getLogger(ActiveTestThread.class);
	
	private IoSession session = null;
	private long heartbeatInterval = 60000;
	private long heartbeatRetry = 3;
	private long reconnectInterval = 10000;
	private long lastCheckTime = 0;
	private long lastActiveTime = 0;
	private Channel channel;
	
	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	/**
	 * 链路心跳检测线程
	 * 
	 * @param s
	 */
	public ActiveTestThread(IoSession s, Channel channel) {
		setDaemon(true);
		this.session = s;
		lastCheckTime = System.currentTimeMillis();
		lastActiveTime = System.currentTimeMillis();
		this.channel = channel;
	}

	@Override
	public void run() {
		try {
			while (session.isConnected()) {
				long currentTime = System.currentTimeMillis();
				
				//校验间隔时间大于心跳时间
				if ((currentTime - lastCheckTime) > heartbeatInterval) {
					ChannelLog.log(logger, "CmppSession.checkConnection", LevelUtils.getSucLevel(channel.getId()));
					//校验间隔时间小于心跳时间*心跳次数
					if ((currentTime - lastActiveTime) < (heartbeatInterval * heartbeatRetry)) {
						lastCheckTime = currentTime;//设置最后心跳时间为当前时间
						
						//利用Bind命令实现和移动电信类似的心跳功能
						Bind request = new Bind();
						request.setLoginName(channel.getAccount());
						request.setLoginPwd(channel.getPassword());
						request.assignSequenceNumber(channel.getNodeId());
						session.write(request);
						
						setLastActiveTime(currentTime);
					} else {
						ChannelLog.log(logger, "connection lost!", LevelUtils.getSucLevel(channel.getId()));
						session.close(true);
						break;
					}
				}
				try {
					Thread.sleep(reconnectInterval);
				} catch (InterruptedException e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getSucLevel(channel.getId()), e);
				}
			}
		} catch (Exception e) {
			ChannelLog.log(logger, e.getMessage(), LevelUtils.getSucLevel(channel.getId()), e);
		}
	}
}