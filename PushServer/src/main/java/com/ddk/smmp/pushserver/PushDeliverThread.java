package com.ddk.smmp.pushserver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.pushserver.dao.Deliver;
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
public class PushDeliverThread extends Thread {
	private static final Logger logger = Logger.getLogger(PushDeliverThread.class);
	
	Tuple3<Integer, BlockingQueue<Deliver>, BlockingQueue<Integer>> deliverTuple = null;
	
	public PushDeliverThread(Tuple3<Integer, BlockingQueue<Deliver>, BlockingQueue<Integer>> deliverTuple) {
		setDaemon(true);
		this.deliverTuple = deliverTuple;
	}

	@Override
	public void run() {
		UserPushCfg cfg = CacheUtil.get(UserPushCfg.class, PushServer.USER_CACHE_KEY, deliverTuple.e1);
		if(null != cfg){
			while(deliverTuple.e2.size() > 0){
				List<Deliver> tempList = new ArrayList<Deliver>();
				List<Integer> idList = new ArrayList<Integer>();
				
				int num = deliverTuple.e2.drainTo(tempList, 200);
				deliverTuple.e3.drainTo(idList, 200);
				
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
					
					Object resp = httpClient.post(cfg.getDlvUrl(), params, "UTF-8");
					
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
						
						logger.info("push deliver:" + args + "->FAIL");
					}else{
						logger.info("push deliver:" + args + "->SUCCESS");
						//拼接ID串 用于后面批量锁定
						PushServer.deliverIdQueue.addAll(idList);
					}
				}
			}
		}
		
		PushServer.deliver_RunThreadNum.decrementAndGet();
	}
}