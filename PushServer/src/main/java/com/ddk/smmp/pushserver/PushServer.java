package com.ddk.smmp.pushserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ddk.smmp.pushserver.dao.Deliver;
import com.ddk.smmp.pushserver.dao.Report;
import com.ddk.smmp.pushserver.dao.Tuple2;
import com.ddk.smmp.pushserver.dao.Tuple3;
import com.ddk.smmp.pushserver.dao.UserPushCfg;
import com.ddk.smmp.pushserver.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.pushserver.jdbc.database.DruidDatabaseConnectionPool;
import com.ddk.smmp.pushserver.service.DbService;
import com.ddk.smmp.pushserver.utils.CacheUtil;

/**
 * @author leeson 2014年11月6日 下午3:25:04 li_mr_ceo@163.com <br>
 * 
 */
public class PushServer {
	static {
		PropertyConfigurator.configure(Class.class.getClass().getResource("/").getPath() + "log4j.properties");
	}
	
	private static final Logger logger = Logger.getLogger(PushServer.class);
	
	public static final String USER_CACHE_KEY = "USER_CACHE";// 缓存中的用户缓存组key
	
	private static ExecutorService pushReportThreadPool = Executors.newFixedThreadPool(10);
	private static ExecutorService pushDelivThreadPool = Executors.newFixedThreadPool(10);
	
	public static AtomicInteger report_RunThreadNum = new AtomicInteger(0);
	public static AtomicInteger deliver_RunThreadNum = new AtomicInteger(0);
	
	public static Map<Integer, Tuple3<Integer, BlockingQueue<Report>, BlockingQueue<Tuple2<Integer, String>>>> user_report_map = new HashMap<Integer, Tuple3<Integer, BlockingQueue<Report>, BlockingQueue<Tuple2<Integer, String>>>>();
	public static Map<Integer, Tuple3<Integer, BlockingQueue<Deliver>, BlockingQueue<Integer>>> user_deliver_map = new HashMap<Integer, Tuple3<Integer, BlockingQueue<Deliver>, BlockingQueue<Integer>>>();
	
	public static BlockingQueue<Tuple2<Integer, String>> reportIdQueue = new LinkedBlockingQueue<Tuple2<Integer, String>>();
	public static BlockingQueue<Integer> deliverIdQueue = new LinkedBlockingQueue<Integer>();
	
	public static void main(String[] args) {
		DruidDatabaseConnectionPool.startup();
		logger.info("running database connection pool ......");
		
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					List<UserPushCfg> userPushCfgs = new DbService(trans).getUserPushCfgs();
					trans.commit();
					for(UserPushCfg upc : userPushCfgs){
						CacheUtil.put(USER_CACHE_KEY, upc.getUserId(), upc);
					}
					logger.info("user cache num[" + userPushCfgs.size() + "] ......");
				} catch (Exception ex) {
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}, 2, 60 * 1, TimeUnit.SECONDS);
		logger.info("running user cache modify thread ......");
		
		lockAndWatchReport();
		logger.info("running lockAndWatchReport thread ......");
		
		pushReport();
		logger.info("running pushReport thread ......");
		
		lockAndWatchDeliver();
		logger.info("running lockAndWatchReport thread ......");
		
		pushDeliver();
		logger.info("running pushReport thread ......");
	}
	
	/**
	 * 锁定并抓取报告数据
	 */
	private static void lockAndWatchReport(){
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//锁定上次推送的报告数据
				if(reportIdQueue.size() > 0){
					List<Tuple2<Integer, String>> reportIdList = new ArrayList<Tuple2<Integer,String>>();
					reportIdQueue.retainAll(reportIdList);
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						new DbService(trans).batchUpdateReportStatus(reportIdList);
						trans.commit();
					} catch (Exception ex) {
						trans.rollback();
					} finally {
						trans.close();
					}
				}
				
				//拼装需要推送的用户ID串
				String idStr = "";
				for(Object obj : CacheUtil._GetCache(USER_CACHE_KEY, true).keySet()){
					idStr += obj + ",";
				}
				if(idStr.length() > 0){
					idStr = idStr.substring(0, idStr.length() - 1);
				}
				
				List<Report> temp = new ArrayList<Report>();
				
				//抓取需要推送的报告
				if(user_report_map.size() == 0 && report_RunThreadNum.intValue() == 0){
					logger.info("抓取报告");
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						temp = new DbService(trans).getReports(idStr);
						trans.commit();
					} catch (Exception ex) {
						trans.rollback();
					} finally {
						trans.close();
					}
					
					if(temp.size() > 0){
						for(Report report : temp){
							Integer uId = report.getUid();
							if(user_report_map.containsKey(uId)){
								user_report_map.get(uId).e2.add(report);
								user_report_map.get(uId).e3.add(new Tuple2<Integer, String>(report.getId(), report.getTbName()));
							}else{
								BlockingQueue<Report> blockingQueue = new LinkedBlockingQueue<Report>();
								blockingQueue.add(report);
							
								BlockingQueue<Tuple2<Integer, String>> blockingQueue2 = new LinkedBlockingQueue<Tuple2<Integer, String>>();
								blockingQueue2.add(new Tuple2<Integer, String>(report.getId(), report.getTbName()));
								
								user_report_map.put(uId, new Tuple3<Integer, BlockingQueue<Report>, BlockingQueue<Tuple2<Integer, String>>>(uId, blockingQueue, blockingQueue2));
							}
						}
					}
				}
			}
		}, 5000, 2000, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 推送报告
	 */
	private static void pushReport(){
		while (true) {
			logger.info("准备推送报告");
			if(user_report_map.size() != 0){
				for(Tuple3<Integer, BlockingQueue<Report>, BlockingQueue<Tuple2<Integer, String>>> reportTuple : user_report_map.values()){
					pushReportThreadPool.execute(new PushReportThread(reportTuple));
				}
				
				user_report_map.clear();
			}else{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e.getCause());
				}
			}
		}
	}
	
	/**
	 * 锁定并抓取上行数据
	 */
	private static void lockAndWatchDeliver(){
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//锁定上次推送的上行数据
				if(deliverIdQueue.size() > 0){
					List<Integer> delivIdList = new ArrayList<Integer>();
					deliverIdQueue.retainAll(delivIdList);
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						new DbService(trans).batchUpdateDelivStatus(delivIdList);
						trans.commit();
					} catch (Exception ex) {
						trans.rollback();
					} finally {
						trans.close();
					}
				}
				
				//拼装需要推送的用户ID串
				String idStr = "";
				for(Object obj : CacheUtil._GetCache(USER_CACHE_KEY, true).keySet()){
					idStr += obj + ",";
				}
				if(idStr.length() > 0){
					idStr = idStr.substring(0, idStr.length() - 1);
				}
				
				List<Deliver> temp = new ArrayList<Deliver>();
				
				//抓取需要推送的上行
				if(user_deliver_map.size() == 0 && deliver_RunThreadNum.intValue() == 0){
					logger.info("抓取上行");
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						temp = new DbService(trans).getDelivers(idStr);
						trans.commit();
					} catch (Exception ex) {
						trans.rollback();
					} finally {
						trans.close();
					}
					
					if(temp.size() > 0){
						for(Deliver deliver : temp){
							Integer uId = deliver.getUid();
							if(user_deliver_map.containsKey(uId)){
								user_deliver_map.get(uId).e2.add(deliver);
								user_deliver_map.get(uId).e3.add(deliver.getId());
							}else{
								BlockingQueue<Deliver> blockingQueue = new LinkedBlockingQueue<Deliver>();
								blockingQueue.add(deliver);
							
								BlockingQueue<Integer> blockingQueue2 = new LinkedBlockingQueue<Integer>();
								blockingQueue2.add(deliver.getId());
								
								user_deliver_map.put(uId, new Tuple3<Integer, BlockingQueue<Deliver>, BlockingQueue<Integer>>(uId, blockingQueue, blockingQueue2));
							}
						}
					}
				}
			}
		}, 5000, 5000, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 推送上行
	 */
	private static void pushDeliver(){
		while (true) {
			logger.info("准备推送上行");
			if(user_deliver_map.size() != 0){
				for(Tuple3<Integer, BlockingQueue<Deliver>, BlockingQueue<Integer>> deliverTuple : user_deliver_map.values()){
					pushDelivThreadPool.execute(new PushDeliverThread(deliverTuple));
				}
				
				user_deliver_map.clear();
			}else{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e.getCause());
				}
			}
		}
	}
	
	/**
	 * 计算当前待处理报告数量合理化10个线程分配的数量
	 * 
	 * @param totle
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static String[] calculateMin(int totle){
		String[] resultArray = new String[2];
		int divisor = totle/10;
		resultArray[0] = divisor + "," + (10 * divisor + 10 - totle);
		resultArray[1] = (divisor + 1) + "," + (totle - 10 * divisor);
		return resultArray;
	}
}