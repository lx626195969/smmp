package com.ddk.smmp.pushserver.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ddk.smmp.pushserver.jdbc.database.access.DataAccess;
import com.ddk.smmp.pushserver.jdbc.database.convert.ResultConverter;

/**
 * @author leeson 2014-6-12 上午10:05:07 li_mr_ceo@163.com <br>
 * 
 */
public class DbDao extends DataAccess {
	public DbDao(Connection conn) {
		super(conn);
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
	
	/**
	 * 获取待推送报告
	 * 
	 * @param idStr 需要推送的用户ID
	 * @return
	 */
	public List<Report> getReports(String idStr){
		if(StringUtils.isEmpty(idStr)){
			return new ArrayList<Report>();
		}
		
		final String today = getDayBefore(0);
 		final String bd = getDayBefore(1);
		final String bbd = getDayBefore(2);
		final String bbbd = getDayBefore(3);
		
		StringBuffer sqlBuffer = new StringBuffer("SELECT m.id, m.send_userid, m.batch_num, m.phone, m.send_state, m.updateDate, 'message_submit_" + bbbd + "' as tbname FROM message_submit_" + bbbd + " m WHERE m.deliv_num = m.num AND m.is_deliv = 0 AND m.submit_type = 2 AND m.send_userid IN(" + idStr + ")");
		sqlBuffer.append("UNION ");
		sqlBuffer.append("SELECT m1.id, m1.send_userid, m1.batch_num, m1.phone, m1.send_state, m1.updateDate, 'message_submit_" + bbd + "' as tbname FROM message_submit_" + bbd + " m1 WHERE m1.deliv_num = m1.num AND m1.is_deliv = 0 AND m1.submit_type = 2 AND m1.send_userid IN(" + idStr + ")");
		sqlBuffer.append("UNION ");
		sqlBuffer.append("SELECT m2.id, m2.send_userid, m2.batch_num, m2.phone, m2.send_state, m2.updateDate, 'message_submit_" + bd + "' as tbname FROM message_submit_" + bd + " m2 WHERE m2.deliv_num = m2.num AND m2.is_deliv = 0 AND m2.submit_type = 2 AND m2.send_userid IN(" + idStr + ")");
		sqlBuffer.append("UNION ");
		sqlBuffer.append("SELECT m3.id, m3.send_userid, m3.batch_num, m3.phone, m3.send_state, m3.updateDate, 'message_submit_" + today + "' as tbname FROM message_submit_" + today + " m3 WHERE m3.deliv_num = m3.num AND m3.is_deliv = 0 AND m3.submit_type = 2 AND m3.send_userid IN(" + idStr + ")");
		sqlBuffer.append("LIMIT 1000");
		
		return super.queryForList(sqlBuffer.toString(), new ResultConverter<Report>() {
			@Override
			public com.ddk.smmp.pushserver.dao.Report convert(ResultSet rs) throws SQLException {
				return new Report(rs.getInt("id"), rs.getInt("send_userid"), rs.getString("batch_num"), rs.getString("phone"), rs.getString("send_state"), rs.getString("updateDate"), rs.getString("tbname"));
			}
		});
	}
	
	/**
	 * 批量更新已推送的报告
	 * 
	 * @param sqlArray
	 */
	public void batchUpdateReportStatus(List<Tuple2<Integer, String>> reportIdList){
		String[] sqlArray = new String[reportIdList.size()];
		for(int i = 0;i < reportIdList.size();i++){
			Tuple2<Integer, String> tuple2 = reportIdList.get(i);
			sqlArray[i] = "UPDATE " + tuple2.e2 + " SET is_deliv = 1 WHERE id = " + tuple2.e1;
		}
		super.batchUpdate(sqlArray);
	}
	
	/**
	 * 更新用户是否推送  状态
	 * @param isPush
	 * @param id
	 */
	public void updatePushStatus(boolean isPush, int id){
		String sql = "UPDATE user_push_cfg SET `status` = " + (isPush ? "1" : "0") + " WHERE id = " + id;
		super.update(sql);
	}
	
	/**
	 * 获取需要推送的用户信息
	 * 
	 * @return
	 */
	public List<UserPushCfg> getUserPushCfgs(){
		String sql = "SELECT u.id, u.uId, u.`status`, u.`dlv_url`, u.`rpt_url` FROM user_push_cfg u WHERE u.`status` = 1";
		return super.queryForList(sql, new ResultConverter<UserPushCfg>() {
			@Override
			public UserPushCfg convert(ResultSet rs) throws SQLException {
				return new UserPushCfg(rs.getInt("id"), rs.getInt("uId"), rs.getString("dlv_url"), rs.getString("rpt_url"), rs.getInt("status"));
			}
		});
	}
	
	/**
	 * 获取需要推送的上行
	 * 
	 * @param idStr 需要推送的用户ID
	 * @return
	 */
	public List<Deliver> getDelivers(String idStr){
		if(StringUtils.isEmpty(idStr)){
			return new ArrayList<Deliver>();
		}
		
		String sql = "SELECT mr.id, mr.send_userid, mr.phone, mr.content, mr.createDate FROM message_received mr WHERE mr.is_deliv = 0 AND mr.send_userid IN (" + idStr + ") LIMIT 1000";
	
		return super.queryForList(sql, new ResultConverter<Deliver>() {
			@Override
			public Deliver convert(ResultSet rs) throws SQLException {
				return new Deliver(rs.getInt("id"), rs.getInt("send_userid"), rs.getString("phone"), rs.getString("content"), rs.getString("createDate"));
			}
		});
	}
	
	/**
	 * 批量更新已推送的上行
	 * 
	 * @param sqlArray
	 */
	public void batchUpdateDelivStatus(List<Integer> delivIdList){
		String[] sqlArray = new String[delivIdList.size()];
		for(int i = 0;i < delivIdList.size();i++){
			Integer delivId = delivIdList.get(i);
			sqlArray[i] = "UPDATE message_received SET is_deliv = 1 WHERE id = " + delivId;
		}
		super.batchUpdate(sqlArray);
	}
}