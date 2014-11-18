package com.ddk.smmp.pushserver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.pushserver.dao.Report;
import com.ddk.smmp.pushserver.dao.Tuple2;
import com.ddk.smmp.pushserver.dao.Tuple3;
import com.ddk.smmp.pushserver.dao.UserPushCfg;
import com.ddk.smmp.pushserver.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.pushserver.service.DbService;
import com.ddk.smmp.pushserver.utils.CacheUtil;
import com.ddk.smmp.pushserver.utils.HttpClient;

/**
 * 推送报告线程
 * 
 * @author leeson 2014年11月6日 下午5:02:55 li_mr_ceo@163.com <br>
 */
public class PushReportThread extends Thread {
	private static final Logger logger = Logger.getLogger(PushReportThread.class);
	
	Tuple3<Integer, BlockingQueue<Report>, BlockingQueue<Tuple2<Integer, String>>> reportTuple = null;
	
	public PushReportThread(Tuple3<Integer, BlockingQueue<Report>, BlockingQueue<Tuple2<Integer, String>>> reportTuple) {
		setDaemon(true);
		this.reportTuple = reportTuple;
	}

	@Override
	public void run() {
		System.out.println(PushServer.report_RunThreadNum.incrementAndGet());
		
		UserPushCfg cfg = CacheUtil.get(UserPushCfg.class, PushServer.USER_CACHE_KEY, reportTuple.e1);
		if(null != cfg){
			while(reportTuple.e2.size() > 0){
				List<Report> tempList = new ArrayList<Report>();
				List<Tuple2<Integer, String>> idList = new ArrayList<Tuple2<Integer, String>>();
				
				int num = reportTuple.e2.drainTo(tempList, 200);
				reportTuple.e3.drainTo(idList, 200);
				
				if(num > 0){
					HttpClient httpClient = new HttpClient();
					Map<String, String> params = new HashMap<String, String>();
					String args = JSON.toJSONString(tempList);
					try {
						params.put("args", URLEncoder.encode(args, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						logger.error(e.getMessage(), e.getCause());
						continue;
					}
					
					Object resp = httpClient.post(cfg.getRptUrl(), params, "UTF-8");
					
					if(null == resp){
						DatabaseTransaction trans = new DatabaseTransaction(true);
						try {
							new DbService(trans).updatePushStatus(false, cfg.getId());
							trans.commit();
						} catch (Exception ex) {
							trans.rollback();
						} finally {
							trans.close();
						}
						
						CacheUtil.remove(PushServer.USER_CACHE_KEY, cfg.getUserId());
						
						logger.info("push report:" + args + "->FAIL");
					}else{
						logger.info("push report:" + args + "->SUCCESS");
						//拼接ID串 用于后面批量锁定
						PushServer.reportIdQueue.addAll(idList);
					}
				}
			}
		}
		
		System.out.println(PushServer.report_RunThreadNum.decrementAndGet());
	}
	
	public static void main(String[] args) {
//		Report report = new Report(1, 17, "20141110155520001", "15214388466", "DELIVRD", "2014-11-10 15:56:12", "message_submit_1110");
//		String pushUrl = "http://210.5.152.30:9100/ddk_mr";
//		HttpClient httpClient = new HttpClient();
//		Map<String, String> params = new HashMap<String, String>();
//		try {
//			params.put("args", URLEncoder.encode(JSON.toJSONString(report), "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		Object resp = httpClient.post(pushUrl, params, "UTF-8");
//		System.out.println(resp);
//		
//		Deliver deliver = new Deliver(1, 17, "15214388466", "测试上行", "2014-11-10 15:56:12");
//		String pushUrl1 = "http://210.5.152.30:9100/ddk_mo";
//		HttpClient httpClient1 = new HttpClient();
//		Map<String, String> params1 = new HashMap<String, String>();
//		try {
//			params1.put("args", URLEncoder.encode(JSON.toJSONString(deliver), "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		Object resp1 = httpClient1.post(pushUrl1, params1, "UTF-8");
//		System.out.println(resp1);
		
//		BlockingQueue<Report> blockingQueue = new LinkedBlockingQueue<Report>();
//		BlockingQueue<Integer> blockingQueue2 = new LinkedBlockingQueue<Integer>();
//		Report report1 = new Report(1, 17, "20141110155520001", "15214388466", "DELIVRD", "2014-11-10 15:56:12", "message_submit_1110");
//		Report report2 = new Report(2, 17, "20141110155520002", "15214388466", "DELIVRD", "2014-11-10 15:56:12", "message_submit_1110");
//		Report report3 = new Report(3, 17, "20141110155520003", "15214388466", "DELIVRD", "2014-11-10 15:56:12", "message_submit_1110");
//		
//		blockingQueue.add(report1);
//		blockingQueue.add(report2);
//		blockingQueue.add(report3);
//		
//		blockingQueue2.add(1);
//		blockingQueue2.add(2);
//		blockingQueue2.add(3);
//		
//		List<Report> reports = new LinkedList<Report>();
//		List<Integer> idList = new LinkedList<Integer>();
//		blockingQueue.drainTo(reports, 2);
//		blockingQueue2.drainTo(idList, 2);
//		
//		System.out.println(JSON.toJSON(reports));
//		System.out.println(JSON.toJSON(idList));
		
//		BlockingQueue<Report> blockingQueue = new LinkedBlockingQueue<Report>();
//		Report report1 = new Report(1, 17, "20141110155520001", "15214388466", "DELIVRD", "2014-11-10 15:56:12", "message_submit_1110");
//		Report report2 = new Report(2, 17, "20141110155520002", "15214388466", "DELIVRD", "2014-11-10 15:56:12", "message_submit_1110");
//		Report report3 = new Report(3, 17, "20141110155520003", "15214388466", "DELIVRD", "2014-11-10 15:56:12", "message_submit_1110");
//
//		blockingQueue.add(report1);
//		blockingQueue.add(report2);
//		blockingQueue.add(report3);
//		
//		String jsonStr = JSON.toJSONString(blockingQueue);
//		System.out.println(jsonStr);
//		
//		List<Report> reportList = JSON.parseArray(jsonStr, Report.class);
//		for(Report rp : reportList){
//			System.out.println(rp);
//		}
	}
}