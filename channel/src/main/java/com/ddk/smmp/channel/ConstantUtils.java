package com.ddk.smmp.channel;

import org.apache.log4j.Logger;

import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;


/**
 * @author leeson 2014年8月6日 下午4:16:23 li_mr_ceo@163.com <br>
 * 
 */
public class ConstantUtils {
	private static final Logger logger = Logger.getLogger(ConstantUtils.class);
	
	public static boolean isPause = false;
	
	/**
	 * 晚上23:58分是否可以暂停操作
	 * 
	 * @return
	 */
	public static boolean isPause_23_58() {
		return isPause;
	}
	
	/**
	 * 
	 * @param channelId
	 * @param status 1启动2停止3重连
	 */
	public static void updateChannelStatus(int channelId, int status){
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService dbService = new DbService(trans);
			dbService.updateChannelStatus(channelId, status);
			
			String state_ = "";
			if(status == 1){
				state_ = "启动";
			}
			if(status == 2){
				state_ = "停止";
			}
			if(status == 3){
				state_ = "重连";
			}
			dbService.addChannelLog(channelId, "通道ID", "通道" + state_);
			
			trans.commit();
		} catch (Exception ex) {
			ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channelId), ex);
			trans.rollback();
		} finally {
			trans.close();
		}
	}
}