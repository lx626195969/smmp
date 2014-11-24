package com.ddk.smmp.adapter.service;

import java.util.List;

import com.ddk.smmp.adapter.dao.DbDao;
import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.jdbc.BaseService;
import com.ddk.smmp.adapter.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.adapter.model.Deliver;
import com.ddk.smmp.adapter.model.Report;

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

	/**
	 * 获取所有用户
	 * 
	 * @return
	 */
	public List<UserMode> getAllUser() {
		DbDao dao = new DbDao(getConnection());
		return dao.getAllUser();
	}

	/**
	 * 查询用户可用余额
	 * 
	 * @param uId
	 * @return
	 */
	public Integer getAvailableBalance(String uId) {
		DbDao dao = new DbDao(getConnection());
		return dao.getAvailableBalance(uId);
	}

	/**
	 * 查询用户的产品余额
	 * 
	 * @param uId
	 * @return
	 */
	public List<String> getProductBalance(String uId) {
		DbDao dao = new DbDao(getConnection());
		return dao.getProductBalance(uId);
	}
	
	/**
	 * 查询用户的上行短信
	 * 
	 * @param uId
	 * @return
	 */
	public List<Deliver> getDeliverList(String uId){
		DbDao dao = new DbDao(getConnection());
		return dao.getDeliverList(uId);
	}
	
	/**
	 * 查询用户的短信报告
	 * 
	 * @param uId
	 * @return
	 */
	public List<Report> getReportList(String uId){
		DbDao dao = new DbDao(getConnection());
		return dao.getReportList(uId, 200);
	}
	
	/**
	 * 号码批量插入【重号过滤使用】
	 * 
	 * @param uId
	 * @param phones
	 */
	@Deprecated
	public void insertPhoneRecords(int uId, String[] phones){
		DbDao dao = new DbDao(getConnection());
		dao.insertPhoneRecords(uId, phones);
	}
	
	/**
	 * 定时删除重号过滤记录表
	 * 
	 * @param time
	 */
	@Deprecated
	public int deletePhoneRecords(int minute){
		DbDao dao = new DbDao(getConnection());
		return dao.deletePhoneRecords(minute);
	}
}