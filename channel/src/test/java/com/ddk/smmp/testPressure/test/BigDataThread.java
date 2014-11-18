package com.ddk.smmp.testPressure.test;

import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.testPressure.service.DbService;

/**
 * @author leeson 2014年8月6日 上午9:36:51 li_mr_ceo@163.com <br>
 * 
 */
public class BigDataThread implements Runnable {
	private Integer initVal;
	private Integer packageNum;
	public BigDataThread(Integer initVal, Integer packageNum) {
		super();
		this.initVal = initVal;
		this.packageNum = packageNum;
	}

	@Override
	public void run() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService service = new DbService(trans);

			service.insertMessage(initVal, packageNum);

			trans.commit();
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
			BigDataTest.threadNum.decrementAndGet();
		}
	}
}