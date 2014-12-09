package com.ddk.smmp.channel.maiyuan_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.maiyuan_http.handler.DeliverThread;
import com.ddk.smmp.channel.maiyuan_http.handler.ReportThread;
import com.ddk.smmp.channel.maiyuan_http.handler.SubmitThread;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class MaiYuan_HttpClient extends Client {
	private static final long serialVersionUID = -4959290410169810270L;

	private static final Logger logger = Logger.getLogger(MaiYuan_HttpClient.class);
	
	public MaiYuan_HttpClient(Channel channel) {
		super();
		this.channel = channel;
		channel.setClient(this);
	}
	
	public SubmitThread submitThread = null;
	public ReportThread reportThread = null;
	public DeliverThread deliverThread = null;
	
	@Override
	public void start() {
		try {
			ConstantUtils.updateChannelStatus(channel.getId(), 1);
			channel.setStatus(Channel.RUN_STATUS);
			
			if(null == submitThread){
				submitThread = new SubmitThread(channel);
				submitThread.start();
				ChannelLog.log(logger, "启动迈远短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			if(null == reportThread){
				reportThread = new ReportThread(channel);
				reportThread.start();
				ChannelLog.log(logger, "启动迈远报告处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			if(null == deliverThread){
				deliverThread = new DeliverThread(channel);
				deliverThread.start();
				ChannelLog.log(logger, "启动迈远上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
		} catch (Exception e) {
			ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e);
			
			ConstantUtils.updateChannelStatus(channel.getId(), 2);
			channel.setStatus(Channel.STOP_STATUS);
			
			this.stop();
		}
	}

	@Override
	public void stop() {
		if(null != submitThread){
			submitThread.stop_();
			submitThread = null;
			ChannelLog.log(logger, "停止迈远短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		if(null != reportThread){
			reportThread.stop_();
			reportThread = null;
			ChannelLog.log(logger, "停止迈远报告处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		if(null != deliverThread){
			deliverThread.stop_();
			deliverThread = null;
			ChannelLog.log(logger, "停止迈远上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
	}
}