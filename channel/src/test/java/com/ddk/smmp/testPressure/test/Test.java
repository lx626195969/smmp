package com.ddk.smmp.testPressure.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.jdbc.database.DruidDatabaseConnectionPool;
import com.ddk.smmp.model.SmQueue;
import com.ddk.smmp.testPressure.service.DbService;

/**
 * @author leeson 2014年8月6日 上午10:28:49 li_mr_ceo@163.com <br>
 * 
 */
public class Test {
	@Before
	public void init(){
		DruidDatabaseConnectionPool.startup();
	}
	
	@After
	public void end(){
		DruidDatabaseConnectionPool.shutdown();
	}
	
	@Ignore
	public void testUpdateMessage() {
		long start = System.currentTimeMillis();
		
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService service = new DbService(trans);
			for(int i = 0; i < 10000; i++){
				service.updateMessage(49522168 + i, "WAIT");
			}
			trans.commit();
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
		
		long end = System.currentTimeMillis();
		System.out.println("excute: " + (end - start));
	}
	
	@Ignore
	public void testUpdateMessage1() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			System.out.println(System.currentTimeMillis() + " > 开始");
			DbService service = new DbService(trans);

			service.updateMessage(69521198, "WAIT");

			trans.commit();
			System.out.println(System.currentTimeMillis() + " > 结束");
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	@Ignore
	public void testUpdateMessage2() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			System.out.println(System.currentTimeMillis() + " > 开始");
			DbService service = new DbService(trans);

			service.updateMessage("15200011601", "2014080614271288", "WAIT");

			trans.commit();
			System.out.println(System.currentTimeMillis() + " > 结束");
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	@Ignore
	public void testUpdateMessage3() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			System.out.println(System.currentTimeMillis() + " > 开始");
			DbService service = new DbService(trans);

			service.updateMessage("15219999031", "2014080614271288", "WAIT");

			trans.commit();
			System.out.println(System.currentTimeMillis() + " > 结束");
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	@Ignore
	public void testGetQueueDate() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			System.out.println(System.currentTimeMillis() + " > 开始");
			com.ddk.smmp.service.DbService service = new com.ddk.smmp.service.DbService(trans);

			List<SmQueue> queues = service.getMsgFromQueueAndLockMsg(1, 100);
			for(SmQueue queue : queues){
				System.out.println(queue.getId());
			}
			trans.commit();
			System.out.println(System.currentTimeMillis() + " > 结束");
			
			List<SmQueue> tempList = new LinkedList<SmQueue>();
			BlockingQueue<SmQueue> queue = new LinkedBlockingDeque<SmQueue>(100);
			queue.addAll(queues);
			
			int num = queue.drainTo(tempList, 20);
			System.out.println(num);
			
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	@Ignore
	public void test11() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService service = new DbService(trans);
			for(String str : service.getAllUser()){
				System.out.println(str);
			}
			trans.commit();
			
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	@Ignore
	public void xx() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService service = new DbService(trans);
			service.addUserBlack(1, "15214388466");
			trans.commit();
			
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	@Ignore
	public void aa() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			List<DelivVo> list = new ArrayList<DelivVo>();
			list.add(new DelivVo(1234567890, 6, "DELIVRD", "2014-09-10 11:00"));
			list.add(new DelivVo(1234567890, 6, "DELIVRD", "2014-09-10 11:00"));
			com.ddk.smmp.service.DbService service = new com.ddk.smmp.service.DbService(trans);
			service.batchAddDeliv(list);
			trans.commit();
			
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
}