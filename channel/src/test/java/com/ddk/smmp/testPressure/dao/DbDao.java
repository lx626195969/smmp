package com.ddk.smmp.testPressure.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ddk.smmp.jdbc.database.access.DataAccess;
import com.ddk.smmp.jdbc.database.convert.IntegerConverter;
import com.ddk.smmp.jdbc.database.convert.ResultConverter;

/**
 * @author leeson 2014-6-12 上午10:05:07 li_mr_ceo@163.com <br>
 * 
 */
public class DbDao extends DataAccess {
	public DbDao(Connection conn) {
		super(conn);
	}

	public List<String> getAllUser(){
		return super.queryForList("select logname from user", new ResultConverter<String>() {
			@Override
			public String convert(ResultSet rs) throws SQLException {
				return rs.getString("logname");
			}
		});
	}
	
	/**
	 * 清空表数据
	 * 
	 * @param tables
	 */
	public void removeAll(String[] tables) {
		for (String table : tables) {
			String sql = "DELETE FROM " + table;
			super.update(sql);
			System.out.println(sql);
		}
	}

	/**
	 * 添加数据到用户提交表
	 * 
	 * @param id
	 * @param phone
	 * @param batchNum
	 * @param dateStr
	 * @param userId
	 */
	public void insertDataToSubmit(int id, String phone, String batchNum,
			String dateStr, String userId) {
		String sql = "INSERT INTO message_submit VALUES (" + id + ", '" + phone
				+ "', 'TEST CUSTOM SORT MESSAGE', '" + batchNum + "', '"
				+ userId + "', 'WAIT', '1', '1', 0, '6', '1', '" + dateStr
				+ "', 'guoqr', '" + dateStr + "', 'guoqr', '1')";
		System.out.println(sql);
		super.insert(sql, new IntegerConverter());
		System.out.println(sql);
	}

	/**
	 * 添加数据到用户提交表 长短信
	 * 
	 * @param id
	 * @param phone
	 * @param batchNum
	 * @param dateStr
	 * @param userId
	 */
	public void insertDataToSubmitSuper(int id, String phone, String batchNum,
			String dateStr, String userId) {
		String sql = "INSERT INTO message_submit VALUES ("
				+ id
				+ ", '"
				+ phone
				+ "', '测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信', '"
				+ batchNum + "', '" + userId
				+ "', 'WAIT', '1', '2', 0, '6', '1', '" + dateStr
				+ "', 'guoqr', '" + dateStr + "', 'guoqr', '1')";
		super.insert(sql, new IntegerConverter());
		System.out.println(sql);
	}

	/**
	 * 添加数据到用户队列表
	 * 
	 * @param id
	 * @param phone
	 * @param batchNum
	 * @param dateStr
	 * @param userId
	 */
	public void insertDataToQueue(int id, String phone, String batchNum,
			String dateStr, String userId) {
		String sql = "INSERT INTO queue VALUES (" + id + ", '" + phone
				+ "', 'TEST CUSTOM SORT MESSAGE', '" + batchNum + "', '"
				+ userId + "', '1', '1', '6', '1', '" + dateStr
				+ "', 'guoqr', '" + dateStr + "', 'guoqr', '0')";
		super.insert(sql, new IntegerConverter());
		System.out.println(sql);
	}

	/**
	 * 添加数据到用户队列表 长短信
	 * 
	 * @param id
	 * @param phone
	 * @param batchNum
	 * @param dateStr
	 * @param userId
	 */
	public void insertDataToQueueSuper(int id, String phone, String batchNum,
			String dateStr, String userId) {
		String sql = "INSERT INTO queue VALUES ("
				+ id
				+ ", '"
				+ phone
				+ "', '测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信', '"
				+ batchNum + "', '" + userId + "', '1', '2', '6', '1', '"
				+ dateStr + "', 'guoqr', '" + dateStr + "', 'guoqr', '0')";
		super.insert(sql, new IntegerConverter());
		System.out.println(sql);
	}
	
	/**
	 * 添加2000万测试数据
	 * 
	 * @param initVal
	 * @param packageNum
	 */
	public void insertMessage(int initVal, int packageNum){
		//StringBuffer sqlBuffer1 = new StringBuffer("INSERT INTO message_submit_0813(id, phone, content, batch_num, send_userid, send_state, productid, num, deliv_num, ifreturn, is_deliv, galleryid, submit_type, createDate, createBy, updateDate,  updateBy, state) VALUES ");
		//StringBuffer sqlBuffer2 = new StringBuffer("INSERT INTO message_0813(id, phone, batch_num, send_state, submit_state, deliv_num, submit_num, num, content, send_userid, productid, galleryid, submit_type, createDate, createBy, updateDate,  updateBy, state) VALUES ");
		StringBuffer sqlBuffer3 = new StringBuffer("INSERT INTO queue_1(id, num, galleryid, phone, content, sendCode, state) VALUES ");
		
		for(int i = 0; i < packageNum; i++){
			//sqlBuffer1.append("(" + initVal + ",'1521" + StringUtils.leftPad(initVal + "", 7, "0") + "', '海量发送测试1427【希奥123】',  '2014081314271288', 19, 'WAIT', 9, 1, 0, 0, 0, 1, 1, '2014-08-01 14:27:13', 'user7170', '2014-08-01 14:27:13', 'user7170', '1')");
			//sqlBuffer2.append("(" + initVal + ",'1521" + StringUtils.leftPad(initVal + "", 7, "0") + "', '2014081314271288', 'WAIT', null, 0, 1, 1, '海量发送测试1427【希奥123】', 19, 9, 1, 1, '2014-08-01 14:27:13', 'user7170', '2014-08-01 14:27:13', 'user7170', '1')");
			sqlBuffer3.append("(" + initVal + ", 1, 1, '1521" + StringUtils.leftPad(initVal + "", 7, "0") + "', '海量发送测试1427【希奥123】', '07892200031', 0)");
			
			initVal++;
			if(i != packageNum - 1){
				//sqlBuffer1.append(",");
				//sqlBuffer2.append(",");
				sqlBuffer3.append(",");
			}
		}
		//super.insert(sqlBuffer1.toString(), null);
		//super.insert(sqlBuffer2.toString(), null);
		super.insert(sqlBuffer3.toString(), null);
		
	}
	
	public void updateMessage(int id, String submitState){
		String sql = "UPDATE message_lixin_test SET submit_state = '" + submitState + "' WHERE id = " + id;
		super.update(sql);
	}
	
	public void updateMessage(String phone, String batchNum, String submitState){
		String sql = "UPDATE message_lixin_test SET submit_state = '" + submitState + "' WHERE phone = '" + phone + "' AND batch_num = '" + batchNum + "'";
		super.update(sql);
	}
	
	public void addUserBlack(int uId, String phone){
		String sql = "INSERT INTO user_black (userid, phone) VALUES (" + uId + ", '" + phone + "')";
		super.update(sql);
	}
}