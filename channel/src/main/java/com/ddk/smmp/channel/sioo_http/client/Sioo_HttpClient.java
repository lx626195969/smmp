package com.ddk.smmp.channel.sioo_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.sioo_http.handler.DeliverThread;
import com.ddk.smmp.channel.sioo_http.handler.ReportThread;
import com.ddk.smmp.channel.sioo_http.handler.SubmitThread;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class Sioo_HttpClient extends Client {
	private static final long serialVersionUID = -3705225141297223091L;
	private static final Logger logger = Logger.getLogger(Sioo_HttpClient.class);
	
	public Sioo_HttpClient(Channel channel) {
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
				logger.info("启动希奥短信提交处理线程......");
			}
			if(null == reportThread){
				reportThread = new ReportThread(channel);
				reportThread.start();
				logger.info("启动希奥报告处理线程......");
			}
			if(null == deliverThread){
				deliverThread = new DeliverThread(channel);
				deliverThread.start();
				logger.info("启动希奥上行处理线程......");
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
			logger.info("停止希奥短信提交处理线程......");
		}
		if(null != reportThread){
			reportThread.stop_();
			reportThread = null;
			logger.info("停止希奥报告处理线程......");
		}
		if(null != deliverThread){
			deliverThread.stop_();
			deliverThread = null;
			logger.info("停止希奥上行处理线程......");
		}
	}
}