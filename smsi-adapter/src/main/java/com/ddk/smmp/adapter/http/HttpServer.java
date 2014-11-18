package com.ddk.smmp.adapter.http;

import org.restlet.Component;
import org.restlet.data.Protocol;

import com.ddk.smmp.adapter.Server;
import com.ddk.smmp.adapter.http.action.BalanceAciton;
import com.ddk.smmp.adapter.http.action.SubmitAciton;

/**
 * @author leeson 2014年7月9日 下午6:11:08 li_mr_ceo@163.com <br>
 * 
 */
public class HttpServer implements Server{
	public int status = 0;
	Component comp = null;
	public int port = 7001;//default
	
	public HttpServer() {
		super();
	}

	public HttpServer(int port) {
		super();
		this.port = port;
	}

	@Override
	public void start() throws Exception{
		comp = new Component();
		comp.getClients().add(Protocol.HTTP);
		comp.getServers().add(Protocol.HTTP, port);
		
		comp.getDefaultHost().attach("/submit", SubmitAciton.class);
		//以下 已替换成Push方式  详见 PushServer模块
		//comp.getDefaultHost().attach("/report", ReportAciton.class);
		//comp.getDefaultHost().attach("/deliver", DeliverAciton.class);
		comp.getDefaultHost().attach("/balance", BalanceAciton.class);
		comp.start();
		
		this.status = RUN_STATUS;
	}
	
	@Override
	public void stop() throws Exception{
		if(null != comp){
			try {
				comp.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			comp = null;
		}
		
		this.status = STOP_STATUS;
	}

	@Override
	public int status() {
		return this.status;
	}
	
	@Override
	public int port() {
		return port;
	}
	
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPort() {
		return port;
	}
}