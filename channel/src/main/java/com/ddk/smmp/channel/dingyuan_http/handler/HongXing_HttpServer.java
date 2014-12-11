package com.ddk.smmp.channel.dingyuan_http.handler;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014年10月20日 下午4:07:15 li_mr_ceo@163.com <br>
 */
public class HongXing_HttpServer {
	private static final Logger logger = Logger.getLogger(HongXing_HttpServer.class);
	private Channel channel = null;
	private Server server = null;
	
	public HongXing_HttpServer() {
		super();
	}
	
	public HongXing_HttpServer(Channel channel) {
		super();
		this.channel = channel;
	}
	
	public void start() throws Exception {
		if(null != server){
			try {
				server.stop();
			} catch (Exception e) {
				ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			}
		}else{
			server = new Server(channel.getLocalPort());
	        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        context.setContextPath("/");
	        server.setHandler(context);
	        context.addServlet(new ServletHolder(new HongXing_HttpServlet()), "/");
	        context.addServlet(new ServletHolder(new HongXing_HttpServlet(channel)), "/smsMsg");
	        server.start();
	        
	        ChannelLog.log(logger, "running HongXing_HttpServer [" + channel.getLocalPort() + "] ......", LevelUtils.getSucLevel(channel.getId()));
		}
	}
	
	public void stop() throws Exception {
		if (null != server) {
			server.stop();
			server = null;
		}
	}
}