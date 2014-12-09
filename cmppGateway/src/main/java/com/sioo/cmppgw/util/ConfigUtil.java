package com.sioo.cmppgw.util;

import static com.sioo.cmppgw.business.handler.CmppServerHandler.magIdTailCount;
import static com.sioo.cmppgw.business.handler.CmppServerHandler.msgIdHeadFormat;
import static com.sioo.cmppgw.business.handler.CmppServerHandler.random;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.sioo.cmppgw.dao.DeliverMode;
import com.sioo.cmppgw.dao.RecordMode;
import com.sioo.cmppgw.dao.UserMode;
import com.sioo.cmppgw.entity.Constants;
import com.sioo.cmppgw.entity.Deliver;
import com.sioo.cmppgw.jdbc.database.DatabaseTransaction;
import com.sioo.cmppgw.jdbc.database.DruidDatabaseConnectionPool;
import com.sioo.cmppgw.service.DbService;

/**
 * 
 * @author leeson 2014年8月22日 下午3:12:23 li_mr_ceo@163.com <br>
 *
 */
public class ConfigUtil {
	private final static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
	
	public static final String USER_CACHE_KEY = "user_cache_key";
	public static final String USER_SESSION_KEY = "user_session_key";
	
	private static final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();
	private long fileLastModifyTime = 0;

	public static String getConfig(String name) {
		return cache.get(name.trim());
	}

	public void setConfig(String name, String value) {
		cache.put(name.trim(), value.trim());
	}

	public void loadConf() {
		/**=======初始化连接池=====*/
		DruidDatabaseConnectionPool.startup();
		
		/**=======初始化用户缓存=====*/
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					List<UserMode> userModes = new DbService(trans).getAllUser();
					trans.commit();
					for(UserMode um : userModes){
						CacheUtil.put(USER_CACHE_KEY, um.getUid(), um);
					}
					logger.info("User Cache Num[" + userModes.size() + "] ......");
				} catch (Exception ex) {
					trans.rollback();
				} finally {
					trans.close();
				}
				
				logger.info("Online User:");
				for(Object obj : CacheUtil._GetCache(USER_SESSION_KEY, true).values()){
					ChannelHandlerContext ctx = (ChannelHandlerContext)obj;
					@SuppressWarnings("unchecked")
					Object uObj = ctx.channel().attr(Constants.CURRENT_USER).get();
			    	if(null != uObj){
			    		logger.info(((UserMode)uObj).getUid());
			    	}
				}
			}
		}, 0, 60 * 5, TimeUnit.SECONDS);
		
		/**=======定时清理长短信缓存=====*/
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				LongSMCache.removeInvalidSM();
			}
		}, 60 * 5, 60 * 5, TimeUnit.SECONDS);
		
		/**=======定时推送报告=====*/
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					StringBuffer idBuffer = new StringBuffer();
					
					DbService dbService = new DbService(trans);
					List<RecordMode> recordModes = dbService.getReports();
					
					for(int i = 0; i < recordModes.size(); i++){
						RecordMode rm = recordModes.get(i);
						ChannelHandlerContext ctx = CacheUtil.get(ChannelHandlerContext.class, USER_SESSION_KEY, rm.getUid());
						if(null != ctx){
							Deliver deliver = new Deliver((Integer) Constants.PROTOCALTYPE_VERSION_CMPP2);
							
							deliver.setSecquenceId(Tools.generateSeq());//生成序列
							
							ByteBuffer.wrap(deliver.getMsgId()).putInt(Integer.valueOf(msgIdHeadFormat.format(Calendar.getInstance().getTime()))).putInt(magIdTailCount.incrementAndGet());
					        ByteBuffer.wrap(deliver.getDestId()).put(rm.getSrcId().getBytes());
					        ByteBuffer.wrap(deliver.getSrcTerminalId()).put(rm.getPhone().getBytes());
					        deliver.setSrcTerminalType((byte) 0);
					        deliver.setRegisteredDelivery((byte) 1);
					        ByteBuffer.wrap(deliver.getMsg_Id()).putInt(Integer.parseInt(rm.getMsgId().split("#")[0])).putInt(Integer.parseInt(rm.getMsgId().split("#")[1]));

					        String state = rm.getState();
					        int length = state.getBytes(Charsets.US_ASCII).length;
					    	if(length > 7 ){
					    		state = "UNDELIV";
					    	}
					        ByteBuffer.wrap(deliver.getStat()).put(state.getBytes(Charsets.US_ASCII));
					        
					        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
							Date receiveTime = sdf.parse(rm.getTime());
					        ByteBuffer.wrap(deliver.getSubmitTime()).put(sdf.format(receiveTime).getBytes(Charsets.US_ASCII));
					        ByteBuffer.wrap(deliver.getDoneTime()).put(deliver.getSubmitTime());
					        random.nextBytes(deliver.getSmscSequence());
					      
					        List<byte[]> contents = new ArrayList<byte[]>();
					        contents.add(deliver.getMsg_Id());
					        contents.add(deliver.getStat());
					        contents.add(deliver.getSubmitTime());
					        contents.add(deliver.getDoneTime());
					        contents.add(deliver.getDestTerminalId());
					        contents.add(deliver.getSmscSequence());
					        deliver.setMsgContent(byteMeger(contents));
					        
					        ByteBuffer.wrap(deliver.getDestTerminalId()).put(rm.getPhone().getBytes());
			                deliver.doEncode();
			                logger.debug("Send Deliver Report:{}", deliver.toString());
			                ctx.writeAndFlush(deliver);
			                
			                idBuffer.append(rm.getId()).append(",");
						}
					}
					
					if(idBuffer.length() > 0){
						dbService.delReports(idBuffer.substring(0, idBuffer.length() - 1));//删除已经推送报告的
					}
					
					trans.commit();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
		
		/**=======定时推送上行=====*/
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					
					StringBuffer idBuffer = new StringBuffer();
					
					List<DeliverMode> deliverModes = dbService.getDelivers();
					
					for(int i = 0; i < deliverModes.size(); i++){
						DeliverMode dm = deliverModes.get(i);
						
						ChannelHandlerContext ctx = CacheUtil.get(ChannelHandlerContext.class, USER_SESSION_KEY, dm.getUid());
						if(null != ctx){
							Deliver deliver = new Deliver((Integer) Constants.PROTOCALTYPE_VERSION_CMPP2);
					        
							ByteBuffer.wrap(deliver.getMsgId()).putInt(Integer.valueOf(msgIdHeadFormat.format(Calendar.getInstance().getTime()))).putInt(magIdTailCount.incrementAndGet());
							String srcId = "";
							String[] srcArray = dm.getSrcId().split("#");
							if(srcArray.length == 2){
								srcId = srcArray[1];
							}
							ByteBuffer.wrap(deliver.getDestId()).put(srcId.getBytes());
					        ByteBuffer.wrap(deliver.getSrcTerminalId()).put(dm.getPhone().getBytes());
					        deliver.setSrcTerminalType((byte) 0);
					        deliver.setRegisteredDelivery((byte) 0);
					        deliver.setMsgFmt((byte)8);
					        
					        ByteBuffer.wrap(deliver.getDestTerminalId()).put(dm.getPhone().getBytes());
					        
					        if(dm.getTotle() == 1){
					        	//普通
					        	List<byte[]> byteList = LongSMByte.getLongByte(70, 0, dm.getContent());
					        	if(byteList.size() > 0){
					        		//长短信
					        		deliver.setTpUdhi((byte)1);
					        		for(byte[] b : byteList){
					        			Deliver deliver2 = deliver.clone();
					        			deliver2.setSecquenceId(Tools.generateSeq());//生成序列
					        			deliver2.setMsgContent(b);
					        			deliver2.doEncode();
						                logger.debug("Send Deliver MSG:{}", deliver2.toString());
						                ctx.writeAndFlush(deliver2);
					        		}
					        	}else{
					        		//普通短信
					        		deliver.setSecquenceId(Tools.generateSeq());//生成序列
					        		
					        		deliver.setTpUdhi((byte)0);
						        	deliver.setMsgContent(dm.getContent().getBytes("UnicodeBigUnmarked"));
					                deliver.doEncode();
					                logger.debug("Send Deliver MSG:{}", deliver.toString());
					                ctx.writeAndFlush(deliver);
					        	}
					        }else{
					        	//长
					        	List<byte[]> contents = new ArrayList<byte[]>();
					        	 
					        	deliver.setSecquenceId(Tools.generateSeq());//生成序列
					        	deliver.setTpUdhi((byte)1);
					        	contents.add(new byte[]{ (byte)5, (byte)0, (byte)3, (byte)0, (byte)dm.getIndex(), (byte)dm.getTotle() });
					        	contents.add(dm.getContent().getBytes("UnicodeBigUnmarked"));
					        	
					        	deliver.setMsgContent(byteMeger(contents));
				                deliver.doEncode();
				                logger.debug("Send Deliver MSG:{}", deliver.toString());
				                ctx.writeAndFlush(deliver);
					        }
			                
			                idBuffer.append(dm.getId()).append(",");
						}
					}
					
					if(idBuffer.length() > 0){
						dbService.modifyDelivState(idBuffer.substring(0, idBuffer.length() - 1));//更新推送状态
					}
					
					trans.commit();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}, 0, 1, TimeUnit.MINUTES);
		
		/**=======系统初始化=====*/
		Properties properties = new Properties();
		File file = new File(getRootDir() + "config/server.properties");
		if (!file.exists()) {
			logger.error("not found config file server.properties!");
			return;
		}
		if (file.lastModified() == fileLastModifyTime) {
			return;
		}
		fileLastModifyTime = file.lastModified();

		try {
			InputStream fi = new FileInputStream(file);
			properties.load(fi);
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				setConfig((String) entry.getKey(), (String) entry.getValue());
			}
			fi.close();
			logger.info("[Config Update]");
		} catch (IOException e) {
			logger.error("load properties Error:{}", e);
		}
	}
	
	/**
	 * 获取根目录
	 * 
	 * @return
	 */
	public static String getRootDir(){
		return Class.class.getClass().getResource("/").getPath();
	}
	
    /**
     * 合并byte数组
     * 
     * @param srcArrays
     * @return
     */
    public static byte[] byteMeger(List<byte[]> byteArrays) {
		int len = 0;
		for (byte[] srcArray : byteArrays) {
			len += srcArray.length;
		}

		byte[] destArray = new byte[len];
		int destLen = 0;
		for (byte[] srcArray : byteArrays) {
			System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
			destLen += srcArray.length;
		}
		return destArray;
	}
}