package com.ddk.smmp.adapter.web.action;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddk.smmp.adapter.Server;
import com.ddk.smmp.adapter.http.HttpServer;
import com.ddk.smmp.adapter.socket.SocketServer;
import com.ddk.smmp.adapter.utils.CacheUtil;
import com.ddk.smmp.adapter.webservice.server.WebServiceServer;

/**
 * @author leeson 2014年7月9日 下午6:11:49 li_mr_ceo@163.com <br>
 * 
 */
public class BackStageAciton extends ServerResource {
	private static final Logger logger = LoggerFactory.getLogger(BackStageAciton.class);
	
	/** 参数错误 */
	private final String PARAM_ERROR = "Param Error";
	private final String SUCCESS = "success";

	public static final int START_HTTP = 1;
	public static final int START_SOCKET = 2;
	public static final int START_WEBSERVICE = 3;
	public static final int STOP_HTTP = 8001;
	public static final int STOP_SOCKET = 8002;
	public static final int STOP_WEBSERVICE = 8003;
	
	public static final String SERVER_CACHE_GROUP_KEY = "SMSI_SERVER";
	public static final String SERVER_CACHE_HTTP_KEY = "HTTP";
	public static final String SERVER_CACHE_SOCKET_KEY = "SOCKET";
	public static final String SERVER_CACHE_WEBSERVICE_KEY = "WEBSERVICE";

	/**
	 * 获取请求参数信息
	 * 
	 * @param representation
	 *            POST请求参数封装
	 * @return
	 */
	private Form getForm(Representation representation) {
		if (null == representation) {
			return getRequest().getResourceRef().getQueryAsForm();
		}
		return new Form(representation);
	}

	@Get
	public String get_() {
		try {
			Form form = getForm(null);
			int type = Integer.parseInt(form.getFirstValue("type"));
			int port = Integer.parseInt(form.getFirstValue("port"));
			switch (type) {
			case START_HTTP:
				Server httpServer = CacheUtil.get(Server.class, SERVER_CACHE_GROUP_KEY, SERVER_CACHE_HTTP_KEY);
				if(null == httpServer){
					httpServer = new HttpServer(port);
					httpServer.start();
					logger.info("http server running in port:" + port);
				}else{
					if(httpServer.status() == Server.RUN_STATUS){
						if(httpServer.port() == port){
							logger.info("http server alreay running in port:" + port);
						}else{
							httpServer.stop();
							logger.info("http server stop in port:" + httpServer.port());
							httpServer.setPort(port);
							httpServer.start();
							logger.info("http server running in port:" + port);
						}
					}else{
						httpServer.setPort(port);
						httpServer.start();
						logger.info("http server running in port:" + port);
					}
				}
				
				CacheUtil.put(SERVER_CACHE_GROUP_KEY, SERVER_CACHE_HTTP_KEY, httpServer);
				break;
			case START_SOCKET:
				Server socketServer = CacheUtil.get(Server.class, SERVER_CACHE_GROUP_KEY, SERVER_CACHE_SOCKET_KEY);
				if(null == socketServer){
					socketServer = new SocketServer(port);
					socketServer.start();
					logger.info("socket server running in port:" + port);
				}else{
					if(socketServer.status() == Server.RUN_STATUS){
						if(socketServer.port() == port){
							logger.info("socket server alreay running in port:" + port);
						}else{
							socketServer.stop();
							logger.info("socket server stop in port:" + socketServer.port());
							socketServer.setPort(port);
							socketServer.start();
							logger.info("socket server running in port:" + port);
						}
					}else{
						socketServer.setPort(port);
						socketServer.start();
						logger.info("socket server running in port:" + port);
					}
				}
				
				CacheUtil.put(SERVER_CACHE_GROUP_KEY, SERVER_CACHE_SOCKET_KEY, socketServer);
				break;
			case START_WEBSERVICE:
				Server webserviceServer = CacheUtil.get(Server.class, SERVER_CACHE_GROUP_KEY, SERVER_CACHE_WEBSERVICE_KEY);
				if(null == webserviceServer){
					webserviceServer = new WebServiceServer(port);
					webserviceServer.start();
					logger.info("webservice server running in port:" + port);
				}else{
					if(webserviceServer.status() == Server.RUN_STATUS){
						if(webserviceServer.port() == port){
							logger.info("webservice server alreay running in port:" + port);
						}else{
							webserviceServer.stop();
							logger.info("webservice server stop in port:" + webserviceServer.port());
							webserviceServer.setPort(port);
							webserviceServer.start();
							logger.info("webservice server running in port:" + port);
						}
					}else{
						webserviceServer.setPort(port);
						webserviceServer.start();
						logger.info("webservice server running in port:" + port);
					}
				}
				
				CacheUtil.put(SERVER_CACHE_GROUP_KEY, SERVER_CACHE_WEBSERVICE_KEY, webserviceServer);
				break;
			case STOP_HTTP:
				Server httpServer_ = CacheUtil.get(Server.class, SERVER_CACHE_GROUP_KEY, SERVER_CACHE_HTTP_KEY);
				if(null != httpServer_){
					if(httpServer_.status() == Server.RUN_STATUS){
						httpServer_.stop();
						logger.info("http server stop in port:" + httpServer_.port());
					}else{
						logger.info("http server already stop in port:" + httpServer_.port());
					}
					CacheUtil.put(SERVER_CACHE_GROUP_KEY, SERVER_CACHE_HTTP_KEY, httpServer_);
				}
				break;
			case STOP_SOCKET:
				Server socketServer_ = CacheUtil.get(Server.class, SERVER_CACHE_GROUP_KEY, SERVER_CACHE_SOCKET_KEY);
				if(null != socketServer_){
					if(socketServer_.status() == Server.RUN_STATUS){
						socketServer_.stop();
						logger.info("socket server stop in port:" + socketServer_.port());
					}else{
						logger.info("socket server already stop in port:" + socketServer_.port());
					}
					CacheUtil.put(SERVER_CACHE_GROUP_KEY, SERVER_CACHE_SOCKET_KEY, socketServer_);
				}
				break;
			case STOP_WEBSERVICE:
				Server webserviceServer_ = CacheUtil.get(Server.class, SERVER_CACHE_GROUP_KEY, SERVER_CACHE_WEBSERVICE_KEY);
				if(null != webserviceServer_){
					if(webserviceServer_.status() == Server.RUN_STATUS){
						webserviceServer_.stop();
						logger.info("webservice server stop in port:" + webserviceServer_.port());
					}else{
						logger.info("webservice server already stop in port:" + webserviceServer_.port());
					}
					
					CacheUtil.put(SERVER_CACHE_GROUP_KEY, SERVER_CACHE_WEBSERVICE_KEY, webserviceServer_);
				}
				break;
			default:
				return PARAM_ERROR;
			}

			return SUCCESS;
		} catch (Exception e) {
			return PARAM_ERROR;
		}
	}
}