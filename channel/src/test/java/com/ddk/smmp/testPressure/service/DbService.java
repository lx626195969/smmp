package com.ddk.smmp.testPressure.service;

import java.util.List;

import com.ddk.smmp.jdbc.BaseService;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.testPressure.dao.DbDao;

/**
 * @author leeson 2014-6-12 上午09:50:48 li_mr_ceo@163.com <br>
 * 
 */
public class DbService extends BaseService {
	public DbService() {
		super();
	}

	public DbService(DatabaseTransaction trans) {
		super(trans);
	}
	public List<String> getAllUser(){
		DbDao dao = new DbDao(getConnection());
		return dao.getAllUser();
	}
	
	public void removeAll(String[] tables) {
		DbDao dao = new DbDao(getConnection());
		dao.removeAll(tables);
	}

	public void insertDataToSubmit(int id, String phone, String batchNum,
			String dateStr, String userId) {
		DbDao dao = new DbDao(getConnection());
		dao.insertDataToSubmit(id, phone, batchNum, dateStr, userId);
	}

	public void insertDataToSubmitSuper(int id, String phone, String batchNum,
			String dateStr, String userId) {
		DbDao dao = new DbDao(getConnection());
		dao.insertDataToSubmitSuper(id, phone, batchNum, dateStr, userId);
	}

	public void insertDataToQueue(int id, String phone, String batchNum,
			String dateStr, String userId) {
		DbDao dao = new DbDao(getConnection());
		dao.insertDataToQueue(id, phone, batchNum, dateStr, userId);
	}

	public void insertDataToQueueSuper(int id, String phone, String batchNum,
			String dateStr, String userId) {
		DbDao dao = new DbDao(getConnection());
		dao.insertDataToQueueSuper(id, phone, batchNum, dateStr, userId);
	}
	
	/**
	 * 添加2000万测试数据
	 * 
	 * @param initVal
	 * @param packageNum
	 */
	public void insertMessage(int initVal, int packageNum){
		DbDao dao = new DbDao(getConnection());
		dao.insertMessage(initVal, packageNum);
	}
	
	public void updateMessage(int id, String submitState){
		DbDao dao = new DbDao(getConnection());
		dao.updateMessage(id, submitState);
	}
	
	public void updateMessage(String phone, String batchNum, String submitState){
		DbDao dao = new DbDao(getConnection());
		dao.updateMessage(phone, batchNum, batchNum);
	}
	
	public void addUserBlack(int uId, String phone){
		DbDao dao = new DbDao(getConnection());
		dao.addUserBlack(uId, phone);
	}
}