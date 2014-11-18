package com.ddk.smmp.testPressure.test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.ddk.smmp.jdbc.database.DruidDatabaseConnectionPool;

/**
 * @author leeson 2014年8月6日 上午9:26:18 li_mr_ceo@163.com <br>
 * 
 */
public class BigDataTest {
	private static final int STEP = 200;
	private static final int MAX_NUM = 5600000;
	public static AtomicInteger threadNum = new AtomicInteger(1);
	
	public static void main(String[] args) throws Exception {
		DruidDatabaseConnectionPool.startup();
		
		System.out.println(new Date() + " > 开始添加");
		
		for(int i = 600000; i <= MAX_NUM; i = i + STEP){
			if(threadNum.intValue() < 200){
				Thread thread = new Thread(new BigDataThread(i, STEP));
				threadNum.incrementAndGet();
				thread.start();
			}else{
				i = i - STEP;
			}
		}
		
		//DatabaseConnectionPool.shutdown();
		
		System.out.println(new Date() + " > 结束添加");
	}
}