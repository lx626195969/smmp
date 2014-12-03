package com.ddk.smmp.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.jdbc.database.access.DataAccess;
import com.ddk.smmp.jdbc.database.convert.IntegerConverter;
import com.ddk.smmp.jdbc.database.convert.ResultConverter;
import com.ddk.smmp.model.SmQueue;

/**
 * @author leeson 2014-6-12 上午10:05:07 li_mr_ceo@163.com <br>
 * 
 */
public class DbDao extends DataAccess {
	Logger logger = Logger.getLogger(getClass());
	
	public DbDao(Connection conn) {
		super(conn);
	}
	
	/**
	 * 获取所有状态为运行状态的通道
	 * 以便于后面启动通道
	 * @return
	 */
	public List<Channel> getAllRunChannels(){
		String sql = "SELECT g.id, g.name, g.type, g.ip, g.`port`, g.localPort, g.protocolType, g.accessCode, g.companyCode, g.account, g.`password`, g.submitRate, g.nodeId, g.num, g.sign_num, g.encodedType, g.`status`, g.submitUrl, g.isBatch FROM gallery g WHERE g.status = 1";
		return super.queryForList(sql, new ResultConverter<Channel>() {
			@Override
			public Channel convert(ResultSet rs) throws SQLException {
				return new Channel(rs.getInt("id"), rs.getString("name"), rs
						.getInt("type"), rs.getString("ip"), rs.getInt("port"),
						rs.getInt("localPort"), rs.getInt("protocolType"), rs
								.getString("accessCode"), rs
								.getString("companyCode"), rs
								.getString("account"),
						rs.getString("password"), rs.getInt("submitRate"), rs
								.getInt("nodeId"), rs.getInt("num"), rs
								.getInt("sign_num"), rs.getInt("encodedType"),
						rs.getInt("status"), rs.getString("submitUrl"), rs
								.getInt("isBatch"));
			}
		});
	}
	
	/**
	 * 通过用户ID获取用户名和退订规则
	 * 
	 * @param uid
	 * @return
	 */
	public String getUNameAndBlackRuleByUID(int uid){
		String sql = "SELECT u.logname, u.back_rule FROM `user` u WHERE u.id = " + uid;
		return super.queryForObject(sql, new ResultConverter<String>() {
			@Override
			public String convert(ResultSet rs) throws SQLException {
				return rs.getString("logname") + "#" + rs.getString("back_rule");
			}
		});
	}
	
	public Channel getChannel(int cid){
		String sql = "SELECT g.id, g.name, g.type, g.ip, g.`port`, g.localPort, g.protocolType, g.accessCode, g.companyCode, g.account, g.`password`, g.submitRate, g.nodeId, g.num, g.sign_num, g.encodedType, g.`status`, g.submitUrl, g.isBatch FROM gallery g WHERE g.id = " + cid;
		return super.queryForObject(sql, new ResultConverter<Channel>() {
			@Override
			public Channel convert(ResultSet rs) throws SQLException {
				return new Channel(rs.getInt("id"), rs.getString("name"), rs
						.getInt("type"), rs.getString("ip"), rs.getInt("port"),
						rs.getInt("localPort"), rs.getInt("protocolType"), rs
								.getString("accessCode"), rs
								.getString("companyCode"), rs
								.getString("account"),
						rs.getString("password"), rs.getInt("submitRate"), rs
								.getInt("nodeId"), rs.getInt("num"), rs
								.getInt("sign_num"), rs.getInt("encodedType"),
						rs.getInt("status"), rs.getString("submitUrl"), rs
								.getInt("isBatch"));
			}
		});
	}
	
	/**
	 * 获取协议 入口程序
	 * 
	 * @param protocolType
	 * @return
	 */
	public String getProtocolRunClass(int protocolType){
		String sql = "SELECT p.run_class FROM protocol_type p WHERE p.id = " + protocolType;
		return super.queryForObject(sql, new ResultConverter<String>() {
			@Override
			public String convert(ResultSet rs) throws SQLException {
				return rs.getString("run_class");
			}
		});
	}

	/**
	 * 更改通道状态
	 * 
	 * @param id
	 * @param status
	 */
	public void updateChannelStatus(Integer id, Integer status){
		String sql = "UPDATE gallery g SET g.status = " + status + " WHERE g.id = " + id + ";";
		super.update(sql);
	}
	
	/**
	 * 
	 * @param expandCode
	 * @return
	 */
	public Integer getUidByExpandCode(String expandCode){
		String sql = "SELECT id FROM user WHERE expandCode = '" + expandCode + "'";
		return super.queryForObject(sql, new IntegerConverter());
	}
	
	/**
	 * 通过号码和通道模糊查询最近的发送人
	 * 
	 * @param phone
	 * @param galleryId
	 * @return
	 */
	public Integer getUidByExpandCode(String phone, int galleryId){
		String sql1 = "SELECT mm.id, mm.send_userid FROM message_submit_" + getDayBefore(0) + " mm WHERE mm.id = (SELECT MAX(m.id) FROM message_submit_" + getDayBefore(0) + " m WHERE m.phone = '" + phone + "' AND m.galleryid = " + galleryId + ")";
		String sql2 = "SELECT mm.id, mm.send_userid FROM message_submit_" + getDayBefore(1) + " mm WHERE mm.id = (SELECT MAX(m.id) FROM message_submit_" + getDayBefore(1) + " m WHERE m.phone = '" + phone + "' AND m.galleryid = " + galleryId + ")";
		
		Integer uId = super.queryForObject(sql1, new ResultConverter<Integer>() {
			@Override
			public Integer convert(ResultSet rs) throws SQLException {
				return rs.getInt(2);
			}
		});
		
		if(null == uId){
			uId = super.queryForObject(sql2, new ResultConverter<Integer>() {
				@Override
				public Integer convert(ResultSet rs) throws SQLException {
					return rs.getInt(2);
				}
			});
		}
		return uId;
	}
	
	/**
	 * 通过MO消息中的destId和运营商类型获取接入号
	 * 
	 * @param destId
	 * @param spType
	 * @return
	 */
	public Object[] getAccessCodeInfo(String destId, int spType){
		String sql = "SELECT g.accessCode,g.isExtend,g.isPlus FROM gallery g where POSITION(g.accessCode IN '" + destId + "') = 1 AND g.type = " + spType + ";";
		return super.queryForObject(sql, new ResultConverter<Object[]>() {
			@Override
			public Object[] convert(ResultSet rs) throws SQLException {
				return new Object[]{ rs.getString("accessCode"), rs.getInt("isExtend"), rs.getInt("isPlus") };
			}
		});
	}
	
	/**
	 * 添加MO消息
	 * 
	 * @param userId
	 * @param uName
	 * @param phone
	 * @param port
	 * @param content
	 * @param index
	 * @param totle
	 */
	public void addMessageReceived(int userId, String uName, String phone, String port, String content, int index, int totle){
		String sql = "INSERT INTO message_received ( send_userid, send_user, phone, `port`, content, createDate, createBy, updateDate, updateBy, state, `index`, totle ) VALUES ( " + userId + ", '" + uName + "', '" + phone + "', '" + port + "', '" + content + "', CURRENT_TIMESTAMP(), '系统', CURRENT_TIMESTAMP(), '系统', 1, " + index + ", " + totle + " );";
		super.insert(sql, new IntegerConverter());
	}
	
	/**
	 * 添加用户黑名单
	 * 
	 * @param uId
	 * @param phone
	 */
	public void addUserBlack(int uId, String phone){
		String sql = "INSERT INTO user_black (userid, phone) VALUES (" + uId + ", '" + phone + "')";
		super.update(sql);
	}
	
	/**
	 * 从队列表中获取未锁定的待发送消息
	 * 
	 * @param galleryId
	 * @param limit
	 */
	public Result<SmQueue> getMsgFromQueue(Integer galleryId, int limit) {
		String sql = "SELECT q.id, q.phone, q.content, q.sendCode, q.num from queue_" + galleryId + " q WHERE q.state = 0 LIMIT ?;";
		Result<SmQueue> tempResult = super.queryForListEx(sql, new ResultConverter<SmQueue>() {
			@Override
			public SmQueue convert(ResultSet rs) throws SQLException {
				return new SmQueue(rs.getInt("id"), rs.getString("phone"), rs.getString("content"), rs.getString("sendCode"), rs.getInt("num"));
			}
		}, new Object[] { limit });
		
		List<SmQueue> list = new ArrayList<SmQueue>();
		String idStr = "";
		int count = 0;
		
		for(SmQueue smQueue : tempResult.getList()){
			count += smQueue.getNum();
			if(count > limit){
				tempResult.setList(list);
				tempResult.setIdStr(idStr);
				break;
			}
			
			list.add(smQueue);
			if(StringUtils.isEmpty(idStr)){
				idStr = smQueue.getId() + "";
			}else{
				idStr += "," + smQueue.getId();
			}
		}
		
		return tempResult;
	}
	
	/**
	 * 从队列表中获取未锁定的待发送消息-批量
	 * 
	 * @param galleryId
	 * @param limit
	 */
	public Result<SmQueue> getMsgFromQueue_batch(Integer galleryId, int limit) {
		String sql = "SELECT q.id, q.phone, q.content, q.sendCode, q.num FROM queue_" + galleryId + " q WHERE q.content = ( SELECT m.content FROM queue_" + galleryId + " m WHERE m.state = 0 GROUP BY m.content ORDER BY m.id ASC LIMIT 1) LIMIT " + limit;
		Result<SmQueue> tempResult = super.queryForListEx(sql, new ResultConverter<SmQueue>() {
			@Override
			public SmQueue convert(ResultSet rs) throws SQLException {
				return new SmQueue(rs.getInt("id"), rs.getString("phone"), rs.getString("content"), rs.getString("sendCode"), rs.getInt("num"));
			}
		}, new Object[] { limit });
		
		List<SmQueue> list = new ArrayList<SmQueue>();
		String idStr = "";
		int count = 0;
		
		for(SmQueue smQueue : tempResult.getList()){
			count += smQueue.getNum();
			if(count > limit){
				tempResult.setList(list);
				tempResult.setIdStr(idStr);
				break;
			}
			
			list.add(smQueue);
			if(StringUtils.isEmpty(idStr)){
				idStr = smQueue.getId() + "";
			}else{
				idStr += "," + smQueue.getId();
			}
		}
		
		return tempResult;
	}

	/**
	 * 锁定准备取出的数据，防止重复取出，重复发送
	 * @param galleryId 通道id
	 * @param idStr  队列表中的id 多个以逗号连接
	 * @return
	 */
	public int lockMsgFromQueue(Integer galleryId, String idStr) {
		String sql = "UPDATE queue_" + galleryId + " q set q.state = 1 where q.id in( " + idStr + ");";
		return super.update(sql);
	}
	
	/**
	 * 批量添加提交数据
	 * 
	 * @param list
	 */
	public void batchAddSubmit(List<SubmitVo> list){
		if(list.size() > 0){
			StringBuffer bufferSql = new StringBuffer("INSERT INTO sms_submit (rid, seq, channel_id, createDate) VALUES ");
			for(int i = 0; i < list.size(); i++){
				SubmitVo sv = list.get(i);
				bufferSql.append("(" + sv.getRid() + ", " + sv.getSeq() + ", " + sv.getChannelId() + ", NOW())");
				if(i != list.size() - 1){
					bufferSql.append(",");
				}
			}
			
			super.insert(bufferSql.toString(), new IntegerConverter());
		}
	}
	
	/**
	 * 批量添加提交响应数据
	 * 
	 * @param list
	 */
	public void batchAddSubmitRsp(List<SubmitRspVo> list){
		if(list.size() > 0){
			StringBuffer bufferSql = new StringBuffer("INSERT INTO sms_submit_rsp ( seq, rid, msg_id, channel_id, state, createDate ) VALUES ");
			for(int i = 0; i < list.size(); i++){
				SubmitRspVo sv = list.get(i);
				bufferSql.append("(" + sv.getSeq() + ", " + sv.getRid() + ", " + sv.getMsgId() + ", " + sv.getChannelId() + ", '" + sv.getState() + "', NOW())");
				if(i != list.size() - 1){
					bufferSql.append(",");
				}
			}
			//long s = System.currentTimeMillis();
			super.insert(bufferSql.toString(), new IntegerConverter());
			//long e = System.currentTimeMillis();
			
			//logger.info("批量插入提交响应" + list.size() + "条|耗时[" + (e - s) + "]");
		}
	}
	
	/**
	 * 批量添加状态报告数据
	 * 
	 * @param list
	 */
	public void batchAddDeliv(List<DelivVo> list){
		if(list.size() > 0){
			StringBuffer bufferSql = new StringBuffer("INSERT INTO sms_deliv (msg_id, channel_id, state, time, createDate) VALUES ");
			for(int i = 0; i < list.size(); i++){
				DelivVo sv = list.get(i);
				bufferSql.append("(" + sv.getMsgId() + ", " + sv.getChannelId() + ", '" + sv.getState() + "', '" + sv.getTime() + "', NOW())");
				if(i != list.size() - 1){
					bufferSql.append(",");
				}
			}
			
			//long s = System.currentTimeMillis();
			super.insert(bufferSql.toString(), new IntegerConverter());
			//long e = System.currentTimeMillis();
			
			//logger.info("批量插入提交报告" + list.size() + "条|耗时[" + (e - s) + "]");
		}
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
	 * 添加通道启动/停止/重连日志
	 * 
	 * @param channelId
	 * @param name
	 * @param log
	 */
	public void addChannelLog(int channelId, String name, String log){
		String sql = "INSERT INTO gallery_log (cid, cname, log) VALUES ( " + channelId + ", '" + name + "', '" + log + "' )";
		super.insert(sql, new IntegerConverter());
	}
	
	public static void main(String[] args) {
		Calendar cal3 = Calendar.getInstance();
		cal3.set(Calendar.HOUR_OF_DAY, 3);
		cal3.set(Calendar.MINUTE, 0);
		cal3.set(Calendar.SECOND, 0);
		
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println(new Date(System.currentTimeMillis()));
				
			}
		}, cal3.getTime(), 1000);
		
		while (true) {
		}
	}
	
	public Integer getIdByPhoneAndContent(String phone, String content){
		String sql = "SELECT id FROM message_1112 WHERE phone = '" + phone + "' AND content='" + content + "'";
		return super.queryForObject(sql, new ResultConverter<Integer>() {
			@Override
			public Integer convert(ResultSet rs) throws SQLException {
				return rs.getInt("id");
			}
		});
	}
}