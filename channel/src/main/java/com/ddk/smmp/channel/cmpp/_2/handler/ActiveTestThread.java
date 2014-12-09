package com.ddk.smmp.channel.cmpp._2.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.cmpp._2.msg.ActiveTest;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014-6-10 上午11:36:40 li_mr_ceo@163.com <br>
 *         链路心跳检测线程
 */
public class ActiveTestThread extends Thread {
	private static final Logger logger = Logger.getLogger(ActiveTestThread.class);
	
	private IoSession session = null;
	private long heartbeatInterval = 60000;
	private long heartbeatRetry = 3;
	private long reconnectInterval = 10000;
	private long lastCheckTime = 0;
	private long lastActiveTime = 0;
	int cid;
	
	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	/**
	 * 链路心跳检测线程
	 * 
	 * @param s
	 */
	public ActiveTestThread(IoSession s, int cid) {
		setDaemon(true);
		this.session = s;
		lastCheckTime = System.currentTimeMillis();
		lastActiveTime = System.currentTimeMillis();
		this.cid = cid;
	}

	@Override
	public void run() {
		try {
			while (session.isConnected()) {
				long currentTime = System.currentTimeMillis();
				
				//校验间隔时间大于心跳时间
				if ((currentTime - lastCheckTime) > heartbeatInterval) {
					ChannelLog.log(logger, "CmppSession.checkConnection", LevelUtils.getSucLevel(cid));
					//校验间隔时间小于心跳时间*心跳次数
					if ((currentTime - lastActiveTime) < (heartbeatInterval * heartbeatRetry)) {
						lastCheckTime = currentTime;//设置最后心跳时间为当前时间
						ActiveTest activeTest = new ActiveTest();
						activeTest.assignSequenceNumber();
						activeTest.timeStamp = currentTime;
						session.write(activeTest);
					} else {
						ChannelLog.log(logger, "connection lost!", LevelUtils.getSucLevel(cid));
						session.close(true);
						break;
					}
				}
				try {
					Thread.sleep(reconnectInterval);
				} catch (InterruptedException e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(cid), e);
				}
			}
		} catch (Exception e) {
			ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(cid), e);
		}
	}
}