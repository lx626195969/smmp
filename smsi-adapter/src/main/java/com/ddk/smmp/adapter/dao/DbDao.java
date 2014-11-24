package com.ddk.smmp.adapter.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ddk.smmp.adapter.jdbc.database.access.DataAccess;
import com.ddk.smmp.adapter.jdbc.database.convert.IntegerConverter;
import com.ddk.smmp.adapter.jdbc.database.convert.ResultConverter;
import com.ddk.smmp.adapter.model.Deliver;
import com.ddk.smmp.adapter.model.Report;

/**
 * @author leeson 2014-6-12 上午10:05:07 li_mr_ceo@163.com <br>
 * 
 */
public class DbDao extends DataAccess {
	public DbDao(Connection conn) {
		super(conn);
	}

	/**
	 * 获取所有用户
	 * 
	 * @return
	 */
	public List<UserMode> getAllUser() {
		String sql = "SELECT id, logname, password, decryptKey, bindip, filter_date FROM user WHERE state = 1";
		return super.queryForList(sql, new ResultConverter<UserMode>() {
			@Override
			public UserMode convert(ResultSet rs) throws SQLException {
				return new UserMode(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6));
			}
		});
	}
	
	/**
	 * 查询用户可用余额
	 * 
	 * @param uId
	 * @return
	 */
	public Integer getAvailableBalance(String uId){
		String sql = "SELECT u.availableNum FROM user u WHERE u.logname = '" + uId + "'";
		return super.queryForObject(sql, new IntegerConverter());
	}
	
	/**
	 * 查询用户的产品余额
	 * 
	 * @param uId
	 * @return
	 */
	public List<String> getProductBalance(String uId){
		String sql = "SELECT pu.productid, pu.surplus FROM product_user pu LEFT JOIN user u ON u.id = pu.userid WHERE u.logname = '" + uId + "'";
	
		return super.queryForList(sql, new ResultConverter<String>() {
			@Override
			public String convert(ResultSet rs) throws SQLException {
				return rs.getInt(1) + "#" + rs.getInt(2);
			}
		});
	}
	
	/**
	 * 查询用户的上行短信
	 * 
	 * @param uId
	 * @return
	 */
	public List<Deliver> getDeliverList(String uId){
		String sql = "SELECT mr.id, mr.phone, mr.content, mr.createDate FROM message_received mr LEFT JOIN user u ON u.id = mr.send_userid WHERE u.logname = '" + uId + "' AND mr.is_deliv = 0";
		Result<Deliver> result = super.queryForListEx(sql, new ResultConverter<Deliver>() {
			@Override
			public Deliver convert(ResultSet rs) throws SQLException {
				return new Deliver(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}
		});
		
		if(result.getList().size() > 0){
			String uSql = "UPDATE message_received SET is_deliv = 1 WHERE id IN(" + result.getIdStr() + ")";
			super.update(uSql);
		}
		
		return result.getList();
	}
	
	/**
	 * 查询用户的短信报告
	 * 
	 * @param uId
	 * @param limit
	 * @return
	 */
	public List<Report> getReportList(String uId, int limit){
		List<Report> finalList = new ArrayList<Report>();
		final String today = getDayBefore(0);
 		final String bd = getDayBefore(1);
		final String bbd = getDayBefore(2);
		final String bbbd = getDayBefore(3);
		
		String sql1 = "SELECT mr.id, mr.batch_num, mr.phone, mr.send_state, mr.updateDate FROM message_submit_" + today + " mr LEFT JOIN `user` u ON u.id = mr.send_userid WHERE u.logname = '" + uId + "' AND NOT FIND_IN_SET('WAIT', mr.send_state) AND mr.is_deliv = 0 AND mr.submit_type = 2 LIMIT " + limit;
		
		Result<Report> reports1 = super.queryForListEx(sql1, new ResultConverter<Report>() {
			@Override
			public Report convert(ResultSet rs) throws SQLException {
				return new Report(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
			}
		});
		
		finalList.addAll(reports1.getList());
		
		if(reports1.getList().size() > 0){
			String uSQl1 = "UPDATE message_submit_" + today + " SET is_deliv = 1 WHERE id IN(" + reports1.getIdStr() + ")";
			super.update(uSQl1);
		}
		
		if(reports1.getList().size() < limit){
			String sql2 = "SELECT mr.id, mr.batch_num, mr.phone, mr.send_state, mr.updateDate FROM message_submit_" + bd + " mr LEFT JOIN `user` u ON u.id = mr.send_userid WHERE u.logname = '" + uId + "' AND NOT FIND_IN_SET('WAIT', mr.send_state) AND mr.is_deliv = 0 AND mr.submit_type = 2 LIMIT " + (limit - reports1.getList().size());
			Result<Report> reports2 = super.queryForListEx(sql2, new ResultConverter<Report>() {
				@Override
				public Report convert(ResultSet rs) throws SQLException {
					return new Report(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
				}
			});
			
			finalList.addAll(reports2.getList());
			
			if(reports2.getList().size() > 0){
				String uSQl2 = "UPDATE message_submit_" + bd + " SET is_deliv = 1 WHERE id IN(" + reports2.getIdStr() + ")";
				super.update(uSQl2);
			}
			
			if(reports2.getList().size() < (limit - reports1.getList().size())){
				String sql3 = "SELECT mr.id, mr.batch_num, mr.phone, mr.send_state, mr.updateDate FROM message_submit_" + bbd + " mr LEFT JOIN `user` u ON u.id = mr.send_userid WHERE u.logname = '" + uId + "' AND NOT FIND_IN_SET('WAIT', mr.send_state) AND mr.is_deliv = 0 AND mr.submit_type = 2 LIMIT " + (limit - reports1.getList().size() - reports2.getList().size());
				Result<Report> reports3 = super.queryForListEx(sql3, new ResultConverter<Report>() {
					@Override
					public Report convert(ResultSet rs) throws SQLException {
						return new Report(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
					}
				});
				
				finalList.addAll(reports3.getList());
				
				if(reports3.getList().size() > 0){
					String uSQl3 = "UPDATE message_submit_" + bbd + " SET is_deliv = 1 WHERE id IN(" + reports3.getIdStr() + ")";
					super.update(uSQl3);
				}
				
				if(reports3.getList().size() < (limit - reports1.getList().size() - reports2.getList().size())){
					String sql4 = "SELECT mr.id, mr.batch_num, mr.phone, mr.send_state, mr.updateDate FROM message_submit_" + bbbd + " mr LEFT JOIN `user` u ON u.id = mr.send_userid WHERE u.logname = '" + uId + "' AND NOT FIND_IN_SET('WAIT', mr.send_state) AND mr.is_deliv = 0 AND mr.submit_type = 2 LIMIT " + (limit - reports1.getList().size() - reports2.getList().size() - reports3.getList().size());
					Result<Report> reports4 = super.queryForListEx(sql4, new ResultConverter<Report>() {
						@Override
						public Report convert(ResultSet rs) throws SQLException {
							return new Report(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
						}
					});
					
					finalList.addAll(reports4.getList());
					
					if(reports4.getList().size() > 0){
						String uSQl4 = "UPDATE message_submit_" + bbbd + " SET is_deliv = 1 WHERE id IN(" + reports4.getIdStr() + ")";
						super.update(uSQl4);
					}
				}
			}
		}
		
		return finalList;
	}
	
	/**
	 * 号码批量插入【重号过滤使用】
	 * 
	 * @param uId
	 * @param phones
	 */
	@Deprecated
	public void insertPhoneRecords(int uId, String[] phones){
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO phone_records (uid, phone, time) VALUES ");
		for(int i = 0; i < phones.length; i++){
			sql.append("(" + uId + ", '" + phones[i] + "', SYSDATE())");
			if(i != phones.length - 1){
				sql.append(",");
			}
		}
		super.insert(sql.toString(), new IntegerConverter());
	}
	
	/**
	 * 定时删除重号过滤记录表
	 * 
	 * @param time
	 */
	@Deprecated
	public int deletePhoneRecords(int minute){
		String sql = "DELETE FROM phone_records WHERE time < DATE_SUB(NOW(), INTERVAL " + minute + " MINUTE)";
		return super.update(sql);
	}
	
	/**
	 * 获取day天前的日期
	 * 
	 * @param day
	 * @return
	 */
	private String getDayBefore(int day){ 
		Calendar c = Calendar.getInstance(); 
		Date date = new Date(System.currentTimeMillis()); 
		c.setTime(date); 
		
		int day_ = c.get(Calendar.DATE); 
		c.set(Calendar.DATE, day_ - day); 
		return new SimpleDateFormat("MMdd").format(c.getTime()); 
	}
}