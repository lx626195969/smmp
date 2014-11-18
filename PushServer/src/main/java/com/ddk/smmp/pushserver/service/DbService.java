package com.ddk.smmp.pushserver.service;

import java.util.List;

import com.ddk.smmp.pushserver.dao.DbDao;
import com.ddk.smmp.pushserver.dao.Deliver;
import com.ddk.smmp.pushserver.dao.Report;
import com.ddk.smmp.pushserver.dao.Tuple2;
import com.ddk.smmp.pushserver.dao.UserPushCfg;
import com.ddk.smmp.pushserver.jdbc.BaseService;
import com.ddk.smmp.pushserver.jdbc.database.DatabaseTransaction;

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
	 * 获取待推送报告
	 * 
	 * @param idStr 需要推送的用户ID
	 * @return
	 */
	public List<Report> getReports(String idStr){
		DbDao dao = new DbDao(getConnection());
		return dao.getReports(idStr);
	}
	
	/**
	 * 批量更新已推送的报告
	 * 
	 * @param reportIdList
	 */
	public void batchUpdateReportStatus(List<Tuple2<Integer, String>> reportIdList){
		DbDao dao = new DbDao(getConnection());
		dao.batchUpdateReportStatus(reportIdList);
	}
	
	/**
	 * 更新用户是否推送  状态
	 * @param isPush
	 * @param id
	 */
	public void updatePushStatus(boolean isPush, int id){
		DbDao dao = new DbDao(getConnection());
		dao.updatePushStatus(isPush, id);
	}
	
	/**
	 * 获取需要推送的用户信息
	 * 
	 * @return
	 */
	public List<UserPushCfg> getUserPushCfgs(){
		DbDao dao = new DbDao(getConnection());
		return dao.getUserPushCfgs();
	}
	
	/**
	 * 获取需要推送的上行
	 * 
	 * @param idStr 需要推送的用户ID
	 * @return
	 */
	public List<Deliver> getDelivers(String idStr){
		DbDao dao = new DbDao(getConnection());
		return dao.getDelivers(idStr);
	}
	
	/**
	 * 批量更新已推送的上行
	 * 
	 * @param sqlArray
	 */
	public void batchUpdateDelivStatus(List<Integer> delivIdList){
		DbDao dao = new DbDao(getConnection());
		dao.batchUpdateDelivStatus(delivIdList);
	}
}