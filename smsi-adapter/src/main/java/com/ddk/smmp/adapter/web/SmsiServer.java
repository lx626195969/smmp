package com.ddk.smmp.adapter.web;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.common.i18n.BundleUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.restlet.Component;
import org.restlet.data.Protocol;

import com.ddk.smmp.adapter.Server;
import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.http.HttpServer;
import com.ddk.smmp.adapter.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.adapter.jdbc.database.DruidDatabaseConnectionPool;
import com.ddk.smmp.adapter.service.DbService;
import com.ddk.smmp.adapter.socket.SocketServer;
import com.ddk.smmp.adapter.utils.CacheUtil;
import com.ddk.smmp.adapter.web.action.BackStageAciton;
import com.ddk.smmp.adapter.webservice.server.WebServiceServer;

/**
 * @author leeson 2014年7月18日 下午3:07:53 li_mr_ceo@163.com <br>
 * 
 */
public class SmsiServer {
	static {
		PropertyConfigurator.configure(Class.class.getClass().getResource("/").getPath() + "log4j.properties");
	}
	
	private static final Logger logger = Logger.getLogger((SmsiServer.class).getSimpleName());
	
	public static void main(String[] args) throws Exception {
		SmsiServer smsiServer = new SmsiServer();
		smsiServer.start();
	}

	public static final String USER_CACHE_KEY = "USER_CACHE";// 缓存中的用户缓存组key
	
	ScheduledThreadPoolExecutor userCacheUpdateThreadPool = null;
	ScheduledThreadPoolExecutor phoneRecordsThreadPool = null;
	Component comp = null;

	public void start() throws Exception {
		/**=============================splitline=============================*/
		ResourceBundle bundle = ResourceBundle.getBundle("server");
		//获取短信提交URL
		CacheUtil.put("SUBMIT_INFO", "submit.hostname", BundleUtils.getFormattedString(bundle, "submit.hostname"));
		CacheUtil.put("SUBMIT_INFO", "submit.port", Integer.parseInt(BundleUtils.getFormattedString(bundle, "submit.port")));
		CacheUtil.put("WEBSERVICE_IP", "webservice.bind.ip", BundleUtils.getFormattedString(bundle, "webservice.bind.ip"));
		CacheUtil.put("DOUBLE_PHONE_FILTER_LIMIT", "double.phone.filter.limit", Integer.parseInt(BundleUtils.getFormattedString(bundle, "double.phone.filter.limit")));
		/**=============================splitline=============================*/
		
		/**=============================splitline=============================*/
		//打开数据库连接池
		DruidDatabaseConnectionPool.startup();
		logger.info("running database connection pool ......");
		/**=============================splitline=============================*/
		
		/**=============================splitline=============================*/
		//查询所有用户到缓存（5分钟更新一次缓存）
		userCacheUpdateThreadPool = new ScheduledThreadPoolExecutor(1);
		userCacheUpdateThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					List<UserMode> userModes = new DbService(trans).getAllUser();
					trans.commit();
					for(UserMode um : userModes){
						CacheUtil.put(USER_CACHE_KEY, um.getUserName(), um);
					}
					logger.info("user cache num[" + userModes.size() + "] ......");
				} catch (Exception ex) {
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}, 2, 60 * 5, TimeUnit.SECONDS);
		logger.info("running user cache modify thread ......");
		/**=============================splitline=============================*/
		
		/**=============================splitline=============================*/
//		phoneRecordsThreadPool = new ScheduledThreadPoolExecutor(1);
//		phoneRecordsThreadPool.scheduleAtFixedRate(new Runnable() {
//			@Override
//			public void run() {
//				DatabaseTransaction trans = new DatabaseTransaction(true);
//				try {
//					int rows = new DbService(trans).deletePhoneRecords(Integer.parseInt(CacheUtil.get("DOUBLE_PHONE_FILTER_LIMIT", "double.phone.filter.limit").toString()));
//					trans.commit();
//					logger.info("delete phone records[" + rows + "] ......");
//				} catch (Exception ex) {
//					trans.rollback();
//				} finally {
//					trans.close();
//				}
//			}
//		}, 0, 60 * 1, TimeUnit.SECONDS);
//		logger.info("running phoneRecords timing clean up thread......");
		/**=============================splitline=============================*/
		
		/**=============================splitline=============================*/
		//初始化3套接口
		Server httpServer = new HttpServer();//http
		CacheUtil.put(BackStageAciton.SERVER_CACHE_GROUP_KEY, BackStageAciton.SERVER_CACHE_HTTP_KEY, httpServer);
		Server sockServer = new SocketServer();//socket
		CacheUtil.put(BackStageAciton.SERVER_CACHE_GROUP_KEY, BackStageAciton.SERVER_CACHE_SOCKET_KEY, sockServer);
		Server webserviceServer = new WebServiceServer();//webservice
		CacheUtil.put(BackStageAciton.SERVER_CACHE_GROUP_KEY, BackStageAciton.SERVER_CACHE_WEBSERVICE_KEY, webserviceServer);
		/**=============================splitline=============================*/
		
		/**=============================splitline=============================*/
		//启动后台管理服务器
		String userName = BundleUtils.getFormattedString(bundle, "username");
		String password = BundleUtils.getFormattedString(bundle, "password");
		int port = Integer.parseInt(BundleUtils.getFormattedString(bundle, "port"));
		
		comp = new Component();
		comp.getClients().add(Protocol.HTTP);
		comp.getServers().add(Protocol.HTTP, port);
		comp.getDefaultHost().attach(new BackStageApplication(userName, password));
		comp.start();
		logger.info("running Smsi-adapter server[" + port + "] ......");
		/**=============================splitline=============================*/
	}

	public void stop() throws Exception {
		if (null != comp) {
			comp.stop();
			comp = null;
		}
		if (null != userCacheUpdateThreadPool) {
			userCacheUpdateThreadPool.shutdown();
			userCacheUpdateThreadPool = null;
		}
		
		DruidDatabaseConnectionPool.shutdown();
	}
}