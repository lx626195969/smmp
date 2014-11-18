package com.ddk.smmp.adapter.webservice.server;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

import com.ddk.smmp.adapter.Server;
import com.ddk.smmp.adapter.utils.CacheUtil;

/**
 * 
 * @author leeson 2014年7月21日 上午9:28:25 li_mr_ceo@163.com <br>
 *
 */
public class WebServiceServer implements Server{
	JaxWsServerFactoryBean factoryBean = null;
	public int status = 0;
	public int port = 7003;//default
	
	public WebServiceServer(int port) {
		super();
		this.port = port;
	}
	
	public WebServiceServer() {
		super();
	}

	@Override
	public void start() throws Exception {
		Smsi smsi = new SmsiImpl();
		factoryBean = new JaxWsServerFactoryBean();
		
		factoryBean.setAddress("http://" + CacheUtil.get(String.class, "WEBSERVICE_IP", "webservice.bind.ip") + ":" + port + "/sioo");
		factoryBean.setServiceClass(Smsi.class);
		factoryBean.setServiceBean(smsi);
		factoryBean.getInInterceptors().add(new LoggingInInterceptor());
		factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		factoryBean.create();
		
		this.status = RUN_STATUS;
	}
	
	@Override
	public void stop() throws Exception {
		if(null != factoryBean){
			factoryBean.destroy();
		}
		this.status = STOP_STATUS;
	}
	
	@Override
	public int status() {
		return status;
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