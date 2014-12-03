package com.ddk.smmp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.DbDao;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.jdbc.BaseService;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.jdbc.database.access.DataAccess.Result;
import com.ddk.smmp.model.SmQueue;

/**
 * @author leeson 2014-6-12 上午09:50:48 li_mr_ceo@163.com <br>
 * 
 */
public class DbService extends BaseService {
	private static final Logger logger = Logger.getLogger(DbService.class);
	
	public DbService() {
		super();
	}

	public DbService(DatabaseTransaction trans) {
		super(trans);
	}

	/**
	 * 获取所有状态为运行状态的通道
	 * 以便于后面启动通道
	 * @return
	 */
	public List<Channel> getAllRunChannels(){
		DbDao dao = new DbDao(getConnection());
		return dao.getAllRunChannels();
	}
	
	/**
	 * 获取通道信息
	 * 
	 * @param cid
	 * @return
	 */
	public Channel getChannel(int cid){
		DbDao dao = new DbDao(getConnection());
		return dao.getChannel(cid);
	}
	
	/**
	 * 获取协议 入口程序
	 * 
	 * @param protocolType
	 * @return
	 */
	public String getProtocolRunClass(int protocolType){
		DbDao dao = new DbDao(getConnection());
		return dao.getProtocolRunClass(protocolType);
	}
	/**
	 * 更改通道状态
	 * 
	 * @param id
	 * @param status
	 */
	public void updateChannelStatus(Integer id, Integer status){
		DbDao dao = new DbDao(getConnection());
		dao.updateChannelStatus(id, status);
	}
	
	/**
	 * 处理MO类型的消息
	 * 
	 * @param galleryId
	 * @param destId
	 * @param spType
	 * @param phone
	 * @param content
	 */
	@Deprecated
	public void processMo(int galleryId, String destId, int spType, String phone, String content, int index, int totle){
		DbDao dao = new DbDao(getConnection());
		Object[] accessCodeInfo = dao.getAccessCodeInfo(destId, spType);
		Integer uId = null;
		if(null != accessCodeInfo){
			String accessCode = accessCodeInfo[0].toString();
			//int isExtend = Integer.parseInt(accessCodeInfo[1].toString());
			int isPlus = Integer.parseInt(accessCodeInfo[2].toString());
			String childCode = "";
			String port = "";
			
			//目前srcId有以下几种结构
			//1.通道 [是否拓展 为 是] [是否签名 为否]
			//	接入号+3/5位用户标识  接入号+3/5位用户标识+自定义拓展
			//2.通道 [是否拓展 为 是] [是否签名 为是]
			//	接入号+3/5位用户标识+签名拓展
			//3.通道 [是否拓展 为 否]
			//	接入号
			//其中用户标识 
			//	渠道用户等于3位 为[100-399] 共300个
			//	直客用户等于5位 为[40000-99999] 共6万个
			if(isPlus == 1){
				String str1 = destId.replace(accessCode, "");
				
				int num = Integer.parseInt(str1.charAt(0) + "");
				if(num <= 3){
					childCode = str1.substring(0, 3);
				}else{
					childCode = str1.substring(0, 5);
				}
				
				uId = dao.getUidByExpandCode(childCode);
				
				port = str1;
			}
			
			if(null == uId){
				uId = dao.getUidByExpandCode(phone, galleryId);
			}
			
			String uName = "";
			String blackRule = "";
			if(null != uId){
				String result = dao.getUNameAndBlackRuleByUID(uId);
				if(StringUtils.isNotEmpty(result)){
					uName = result.split("#")[0];
					blackRule = result.split("#")[1];
				}
			}
			dao.addMessageReceived((null == uId ? 0 : uId), StringUtils.isEmpty(uName) ? "" : uName, phone, accessCode + "#" + port, content, index, totle);
			
			List<String> blackList = new ArrayList<String>();
			if(null != uId){
				CollectionUtils.addAll(blackList, blackRule.toUpperCase().split(","));
				
				if(blackList.contains(content.toUpperCase())){
					dao.addUserBlack(uId, phone);
				}else if(content.equalsIgnoreCase("QX") || content.equalsIgnoreCase("TD") || content.equalsIgnoreCase("N") || content.equalsIgnoreCase("0000") || content.equalsIgnoreCase("000000")){
					dao.addUserBlack(uId, phone);
				}
			}
		}
	}
	
	/**
	 * 处理HTTP上行
	 * 
	 * @param galleryId
	 * @param phone
	 * @param content
	 * @param port
	 */
	@Deprecated
	public void process_http_Mo(int galleryId, String phone, String content, String port){
		DbDao dao = new DbDao(getConnection());
		Integer uId = dao.getUidByExpandCode(phone, galleryId);
			
		if(null != uId){
			String uName = "";
			String blackRule = "";
			
			String result = dao.getUNameAndBlackRuleByUID(uId);
			if(StringUtils.isNotEmpty(result)){
				uName = result.split("#")[0];
				blackRule = result.split("#")[1];
			}
			
			dao.addMessageReceived(uId, StringUtils.isEmpty(uName) ? "" : uName, phone, port, content, 1, 1);
			
			List<String> blackList = new ArrayList<String>();
			CollectionUtils.addAll(blackList, blackRule.toUpperCase().split(","));
			
			//是否加用户黑名单
			if(blackList.contains(content.toUpperCase())){
				dao.addUserBlack(uId, phone);
			}else if(content.equalsIgnoreCase("QX") || content.equalsIgnoreCase("TD") || content.equalsIgnoreCase("N") || content.equalsIgnoreCase("0000") || content.equalsIgnoreCase("000000")){
				dao.addUserBlack(uId, phone);
			}
		}
	}
	
	/**
	 * 从队列表中获取未锁定的待发送消息<br>
	 * 并且锁定数据
	 * 
	 * @param galleryId
	 *            通道ID
	 * @param limit
	 *            获取数据条数
	 * @return
	 */
	public List<SmQueue> getMsgFromQueueAndLockMsg(Integer galleryId, int limit) {
		long s = System.currentTimeMillis();
		DbDao dao = new DbDao(getConnection());
		Result<SmQueue> result = dao.getMsgFromQueue(galleryId, limit);
		long e = System.currentTimeMillis();
		
		logger.info("通道[" + galleryId + "]抓取数据" + result.getList().size() + "个｜耗时" + (e - s));
		
		if(StringUtils.isNotEmpty(result.getIdStr())){
			long s1 = System.currentTimeMillis();
			dao.lockMsgFromQueue(galleryId, result.getIdStr());
			long e1 = System.currentTimeMillis();
			
			logger.info("通道[" + galleryId + "]锁定数据" + result.getList().size() + "个｜耗时" + (e1 - s1));
		}
		
		return result.getList();
	}
	
	/**
	 * 从队列表中获取未锁定的待发送消息-批量<br>
	 * 并且锁定数据
	 * 
	 * @param galleryId
	 *            通道ID
	 * @param limit
	 *            获取数据条数
	 * @return
	 */
	public List<SmQueue> getMsgFromQueueAndLockMsg_batch(Integer galleryId, int limit) {
		long s = System.currentTimeMillis();
		DbDao dao = new DbDao(getConnection());
		Result<SmQueue> result = dao.getMsgFromQueue_batch(galleryId, limit);
		long e = System.currentTimeMillis();
		
		logger.info("通道[" + galleryId + "]批量抓取数据" + result.getList().size() + "个｜耗时" + (e - s));
		
		if(StringUtils.isNotEmpty(result.getIdStr())){
			long s1 = System.currentTimeMillis();
			dao.lockMsgFromQueue(galleryId, result.getIdStr());
			long e1 = System.currentTimeMillis();
			
			logger.info("通道[" + galleryId + "]批量锁定数据" + result.getList().size() + "个｜耗时" + (e1 - s1));
		}
		
		return result.getList();
	}
	
	/**
	 * 批量添加提交数据
	 * 
	 * @param list
	 */
	public void batchAddSubmit(List<SubmitVo> list){
		DbDao dao = new DbDao(getConnection());
		dao.batchAddSubmit(list);
	}
	
	/**
	 * 批量添加提交响应数据
	 * 
	 * @param list
	 */
	public void batchAddSubmitRsp(List<SubmitRspVo> list){
		DbDao dao = new DbDao(getConnection());
		dao.batchAddSubmitRsp(list);
	}
	
	/**
	 * 批量添加状态报告数据
	 * 
	 * @param list
	 */
	public void batchAddDeliv(List<DelivVo> list){
		DbDao dao = new DbDao(getConnection());
		dao.batchAddDeliv(list);
	}
	
	/**
	 * 批量添加上行短信
	 * 
	 * @param list
	 */
	public void batchAddMt(List<MtVo> list){
		for(MtVo mt : list){
			if(mt.getType() == 1){
				//直连
				DbDao dao = new DbDao(getConnection());
				Object[] accessCodeInfo = dao.getAccessCodeInfo(mt.getPort(), mt.getSpType());
				Integer uId = null;
				if(null != accessCodeInfo){
					String accessCode = accessCodeInfo[0].toString();
					//int isExtend = Integer.parseInt(accessCodeInfo[1].toString());
					int isPlus = Integer.parseInt(accessCodeInfo[2].toString());
					String childCode = "";
					String port = "";
					
					//目前srcId有以下几种结构
					//1.通道 [是否拓展 为 是] [是否签名 为否]
					//	接入号+3/5位用户标识  接入号+3/5位用户标识+自定义拓展
					//2.通道 [是否拓展 为 是] [是否签名 为是]
					//	接入号+3/5位用户标识+签名拓展
					//3.通道 [是否拓展 为 否]
					//	接入号
					//其中用户标识 
					//	渠道用户等于3位 为[100-399] 共300个
					//	直客用户等于5位 为[40000-99999] 共6万个
					if(isPlus == 1){
						String str1 = mt.getPort().replace(accessCode, "");
						
						int num = Integer.parseInt(str1.charAt(0) + "");
						if(num <= 3){
							childCode = str1.substring(0, 3);
						}else{
							childCode = str1.substring(0, 5);
						}
						
						uId = dao.getUidByExpandCode(childCode);
						
						port = str1;
					}
					
					if(null == uId){
						uId = dao.getUidByExpandCode(mt.getPhone(), mt.getChannelId());
					}
					
					String uName = "";
					String blackRule = "";
					if(null != uId){
						String result = dao.getUNameAndBlackRuleByUID(uId);
						if(StringUtils.isNotEmpty(result)){
							uName = result.split("#")[0];
							blackRule = result.split("#")[1];
						}
					}
					dao.addMessageReceived((null == uId ? 0 : uId), StringUtils.isEmpty(uName) ? "" : uName, mt.getPhone(), accessCode + "#" + port, mt.getContent(), mt.getIndex(), mt.getTotle());
					
					List<String> blackList = new ArrayList<String>();
					if(null != uId){
						CollectionUtils.addAll(blackList, blackRule.toUpperCase().split(","));
						
						if(blackList.contains(mt.getContent().toUpperCase())){
							dao.addUserBlack(uId, mt.getPhone());
						}else if(mt.getContent().equalsIgnoreCase("QX") || mt.getContent().equalsIgnoreCase("TD") || mt.getContent().equalsIgnoreCase("N") || mt.getContent().equalsIgnoreCase("0000") || mt.getContent().equalsIgnoreCase("000000")){
							dao.addUserBlack(uId, mt.getPhone());
						}
					}
				}
			}
			
			if(mt.getType() == 2){
				//http
				DbDao dao = new DbDao(getConnection());
				Integer uId = dao.getUidByExpandCode(mt.getPhone(), mt.getChannelId());
					
				if(null != uId){
					String uName = "";
					String blackRule = "";
					
					String result = dao.getUNameAndBlackRuleByUID(uId);
					if(StringUtils.isNotEmpty(result)){
						uName = result.split("#")[0];
						blackRule = result.split("#")[1];
					}
					
					dao.addMessageReceived(uId, StringUtils.isEmpty(uName) ? "" : uName, mt.getPhone(), mt.getPort(), mt.getContent(), 1, 1);
					
					List<String> blackList = new ArrayList<String>();
					CollectionUtils.addAll(blackList, blackRule.toUpperCase().split(","));
					
					//是否加用户黑名单
					if(blackList.contains(mt.getContent().toUpperCase())){
						dao.addUserBlack(uId, mt.getPhone());
					}else if(mt.getContent().equalsIgnoreCase("QX") || mt.getContent().equalsIgnoreCase("TD") || mt.getContent().equalsIgnoreCase("N") || mt.getContent().equalsIgnoreCase("0000") || mt.getContent().equalsIgnoreCase("000000")){
						dao.addUserBlack(uId, mt.getPhone());
					}
				}
			}
		}
	}
	
	/**
	 * 添加通道启动/停止/重连日志
	 * 
	 * @param channelId
	 * @param name
	 * @param log
	 */
	public void addChannelLog(int channelId, String name, String log){
		DbDao dao = new DbDao(getConnection());
		dao.addChannelLog(channelId, name, log);
	}
	
	public Integer getIdByPhoneAndContent(String phone, String content){
		DbDao dao = new DbDao(getConnection());
		return dao.getIdByPhoneAndContent(phone, content);
	}
}