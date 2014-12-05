package com.sioo.cmppgw.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sioo.cmppgw.jdbc.database.access.DataAccess;
import com.sioo.cmppgw.jdbc.database.convert.IntegerConverter;
import com.sioo.cmppgw.jdbc.database.convert.ResultConverter;

/**
 * @author leeson 2014-6-12 上午10:05:07 li_mr_ceo@163.com <br>
 * 
 */
public class DbDao extends DataAccess {
	public DbDao(Connection conn) {
		super(conn);
	}

	/**
	 * 获取所有用户的用户名、密码、产品ID、控流 信息
	 * 
	 * @return
	 */
	public List<UserMode> getAllUser() {
		String sql = "SELECT u.id, u.logname, u.password, u.expandCode, u.limitCurrent, u.bindip, pu.productid FROM user u LEFT JOIN product_user pu ON u.id = pu.userid GROUP BY u.logname";
		return super.queryForList(sql, new ResultConverter<UserMode>() {
			@Override
			public UserMode convert(ResultSet rs) throws SQLException {
				return new UserMode(rs.getInt("id"), rs.getString("logname"),
						rs.getString("password"), rs.getString("expandCode"),
						rs.getInt("limitCurrent"), rs.getString("bindip"), rs
								.getInt("productid"));
			}
		});
	}

	/**
	 * 添加需要推送报告的记录
	 * 
	 * @param recordModes
	 */
	public void addRecord(List<RecordMode> recordModes) {
		if(recordModes.size() > 0){
			StringBuffer buffer = new StringBuffer("INSERT INTO sms_reports ( rid, uid, msgid, sort, totle, is_deliv, phone, srcId ) VALUES ");
			for(int i = 0; i< recordModes.size(); i++){
				RecordMode recordMode = recordModes.get(i);
				buffer.append("(" + recordMode.getRid() + ", " + recordMode.getUid() + ", '" + recordMode.getMsgId() + "', " + recordMode.getSort() + ", " + recordMode.getTotle() + ", 0, '" + recordMode.getPhone() + "', '" + recordMode.getSrcId() + "')");
				if(i != recordModes.size() - 1){
					buffer.append(",");
				}
			}
			super.insert(buffer.toString(), new IntegerConverter());
		}
	}
	
	/**
	 * 获取可以推送的报告
	 * 
	 * @return
	 */
	public List<RecordMode> getReports(){
		String sql = "SELECT id, uid, msgid, state, time, phone, srcId FROM sms_reports WHERE is_deliv = 0 AND state IS NOT NULL LIMIT 1000";
		return super.queryForList(sql, new ResultConverter<RecordMode>() {
			@Override
			public RecordMode convert(ResultSet rs) throws SQLException {
				return new RecordMode(rs.getInt("id"), rs.getInt("uid"), rs.getString("msgid"), rs.getString("state"), rs.getString("time"), rs.getString("phone"), rs.getString("srcId"));
			}
		});
	}
	
	/**
	 * 批量删除已经推送的报告
	 * 
	 * @param ids
	 */
	public void delReports(String ids){
		if(StringUtils.isNotEmpty(ids)){
			String[] idArray = ids.split(",");
			String[] sqlArray = new String[idArray.length];
			for(int i = 0;i < sqlArray.length;i++){
				sqlArray[i] = "DELETE FROM sms_reports WHERE id = " + idArray[i] + ";";
			}
			super.batchUpdate(sqlArray);
		}
	}
	
	/**
	 * 获取上行短信
	 * 
	 * @return
	 */
	public List<DeliverMode> getDelivers(){
		String sql = "SELECT m.id, m.send_userid, m.content, m.phone, m.`port`, m.`index`, m.totle FROM message_received m WHERE m.is_deliv = 0 LIMIT 1000;";
		return super.queryForList(sql, new ResultConverter<DeliverMode>() {
			@Override
			public DeliverMode convert(ResultSet rs) throws SQLException {
				return new DeliverMode(rs.getInt("id"), rs.getInt("send_userid"), rs.getString("content"), rs.getString("phone"), rs.getString("port"), rs.getInt("index"), rs.getInt("totle"));
			}
		});
	}
	
	/**
	 * 批量修改已推送了的上行消息状态
	 * 
	 * @param ids
	 */
	public void modifyDelivState(String ids){
		if(StringUtils.isNotEmpty(ids)){
			String[] idArray = ids.split(",");
			String[] sqlArray = new String[idArray.length];
			for(int i = 0;i < sqlArray.length;i++){
				sqlArray[i] = "UPDATE message_received SET is_deliv = 1 WHERE id = " + idArray[i] + ";";
			}
			super.batchUpdate(sqlArray);
		}
	}
}