package com.sioo.cmppgw.service;

import java.util.List;

import com.sioo.cmppgw.dao.DbDao;
import com.sioo.cmppgw.dao.DeliverMode;
import com.sioo.cmppgw.dao.RecordMode;
import com.sioo.cmppgw.dao.UserMode;
import com.sioo.cmppgw.jdbc.BaseService;
import com.sioo.cmppgw.jdbc.database.DatabaseTransaction;

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
	 * 获取所有用户的用户名、密码、产品ID、控流 信息
	 * 
	 * @return
	 */
	public List<UserMode> getAllUser() {
		DbDao dao = new DbDao(getConnection());
		return dao.getAllUser();
	}
	
	/**
	 * 添加需要推送报告的记录
	 * 
	 * @param recordModes
	 */
	public void addRecord(List<RecordMode> recordModes) {
		DbDao dao = new DbDao(getConnection());
		dao.addRecord(recordModes);
	}
	
	/**
	 * 获取可以推送的报告
	 * 
	 * @return
	 */
	public List<RecordMode> getReports(){
		DbDao dao = new DbDao(getConnection());
		return dao.getReports();
	}
	
	/**
	 * 批量删除已经推送的报告
	 * 
	 * @param ids
	 */
	public void delReports(String ids){
		DbDao dao = new DbDao(getConnection());
		dao.delReports(ids);
	}
	
	/**
	 * 获取上行短信
	 * 
	 * @return
	 */
	public List<DeliverMode> getDelivers(){
		DbDao dao = new DbDao(getConnection());
		return dao.getDelivers();
	}
	
	/**
	 * 批量修改已推送了的上行消息状态
	 * 
	 * @param ids
	 */
	public void modifyDelivState(String ids){
		DbDao dao = new DbDao(getConnection());
		dao.modifyDelivState(ids);
	}
}