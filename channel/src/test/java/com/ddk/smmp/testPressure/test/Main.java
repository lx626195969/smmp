package com.ddk.smmp.testPressure.test;

import org.apache.commons.lang.StringUtils;

import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.jdbc.database.DruidDatabaseConnectionPool;
import com.ddk.smmp.testPressure.service.DbService;

/**
 * @author leeson 2014年6月19日 下午6:10:06 li_mr_ceo@163.com <br>
 * 
 */
public class Main {

	public static void main(String[] args) {
		DruidDatabaseConnectionPool.startup();
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService service = new DbService(trans);
			
			service.removeAll(new String[] { "message_submit", "queue", "message" });
			
			trans.commit();
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
		
		DatabaseTransaction trans1 = new DatabaseTransaction(true);
		try {
			DbService service = new DbService(trans1);

			for (int i = 1; i <= 1; i++) {
				service.insertDataToSubmit(i, "1521438" + StringUtils.leftPad(i + "", 4, "0"), "2014061318000000", "2014-06-13 18:00:00", "1");
				service.insertDataToQueue(i, "1521438" + StringUtils.leftPad(i + "", 4, "0"), "2014061318000000", "2014-06-13 18:00:00", "1");
			}
//			for (int i = 3001; i <= 6000; i++) {
//				service.insertDataToSubmit(i, "1521438" + StringUtils.leftPad(i + "", 4, "0"), "2014061318000000", "2014-06-13 18:00:00", "2");
//				service.insertDataToQueue(i, "1521438" + StringUtils.leftPad(i + "", 4, "0"), "2014061318000000", "2014-06-13 18:00:00", "2");
//			}
//			for (int i = 6001; i <= 9000; i++) {
//				service.insertDataToSubmit(i, "1521438" + StringUtils.leftPad(i + "", 4, "0"), "2014061318000000", "2014-06-13 18:00:00", "3");
//				service.insertDataToQueue(i, "1521438" + StringUtils.leftPad(i + "", 4, "0"), "2014061318000000", "2014-06-13 18:00:00", "3");
//			}
//			for (int i = 10001; i <= 10001; i++) {
//				service.insertDataToSubmitSuper(i, "152111" + i + "", "2014061318000000", "2014-06-13 18:00:00", "1");
//				service.insertDataToQueueSuper(i, "152111" + i + "", "2014061318000000", "2014-06-13 18:00:00", "1");
//			}
//			for (int i = 13001; i <= 16000; i++) {
//				service.insertDataToSubmitSuper(i, "152111" + i + "", "2014061318000000", "2014-06-13 18:00:00", "2");
//				service.insertDataToQueueSuper(i, "152111" + i + "", "2014061318000000", "2014-06-13 18:00:00", "2");
//			}
//			for (int i = 16001; i <= 19000; i++) {
//				service.insertDataToSubmitSuper(i, "152111" + i + "", "2014061318000000", "2014-06-13 18:00:00", "2");
//				service.insertDataToQueueSuper(i, "152111" + i + "", "2014061318000000", "2014-06-13 18:00:00", "2");
//			}
			
			trans1.commit();
		} catch (Exception ex) {
			trans1.rollback();
		} finally {
			trans1.close();
		}
	}

}
