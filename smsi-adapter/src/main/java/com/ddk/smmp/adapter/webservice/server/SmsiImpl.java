package com.ddk.smmp.adapter.webservice.server;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.alibaba.fastjson.JSONObject;
import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.adapter.service.DbService;
import com.ddk.smmp.adapter.submit_socket_client.SmsTransferClient;
import com.ddk.smmp.adapter.utils.CacheUtil;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.web.SmsiServer;
import com.ddk.smmp.adapter.webservice.entity.BalanceRequest;
import com.ddk.smmp.adapter.webservice.entity.BalanceResponse;
import com.ddk.smmp.adapter.webservice.entity.SubmitRequest;
import com.ddk.smmp.adapter.webservice.entity.SubmitResponse;
import com.ddk.smmp.adapter.webservice.entity.helper.Msg;
import com.ddk.smmp.adapter.webservice.entity.helper.ProductBalance;
import com.ddk.smmp.util.Base64;
import com.ddk.smmp.util.MemCachedUtil;
import com.ddk.smmp.util.PostKeyUtil;

@javax.jws.WebService(serviceName = "smsi", portName = "smsiSOAP", targetNamespace = "http://www.sioo.cn/smsi/", wsdlLocation = "wsdl/smsi.wsdl", endpointInterface = "com.ddk.smmp.adapter.webservice.server.Smsi")
public class SmsiImpl implements Smsi {
	private static final Logger LOG = Logger.getLogger(SmsiImpl.class.getName());

	public java.lang.String invoke(int commandId, java.lang.String uId, java.lang.String body) throws Exception {
		String clientIP = getIp();//获取客户端IP
		Msg msg = new Msg();//初始化响应消息
		
		switch (commandId) {
		case Constants.WEBSERVICE_COMMAND_SUBMIT:
			UserMode userMode = CacheUtil.get(UserMode.class, SmsiServer.USER_CACHE_KEY, uId);
			if(null != userMode){
				SubmitRequest submitRequest = new SubmitRequest();
				submitRequest = submitRequest.toObj(body, userMode.getKey());
				
				LOG.info(submitRequest.toString());
				
				msg.setCommandId(Constants.WEBSERVICE_COMMAND_SUBMIT_RESP);
				
				if(StringUtils.isEmpty(userMode.getHost()) || (StringUtils.isNotEmpty(userMode.getHost()) && userMode.getHost().equals(clientIP))){
					if(userMode.getPwd().equalsIgnoreCase(Base64.encrypt(submitRequest.getPassWord(), Base64.KEY))){
						//号码过多
						if(submitRequest.getPhones().length > 2000){
							SubmitResponse submitResponse = new SubmitResponse(11, "", "too many numbers");
							msg.setBody(submitResponse.toJson(userMode.getKey()));
						}else{
							Boolean state = CacheUtil.get(Boolean.class, "WEBSERVICE_USER_STATE", userMode.getId());
							if(null != state && state == true){
								//提交太快
								SubmitResponse submitResponse = new SubmitResponse(12, "", "submit too fast");
								msg.setBody(submitResponse.toJson(userMode.getKey()));
							}else{
								CacheUtil.put("WEBSERVICE_USER_STATE", userMode.getId(), true);
								JSONObject result = null;
								try {
									result = submit(submitRequest, userMode.getId());
								} catch (Exception e) {
									
								}finally{
									CacheUtil.put("WEBSERVICE_USER_STATE", userMode.getId(), false);
								}
								
								if(null != result){
									JSONObject res = JSONObject.parseObject(result.toString());
									SubmitResponse submitResponse = new SubmitResponse((null == res.getInteger("code") ? Constants.CODE_AUTH_ERROR : res.getInteger("code")), (StringUtils.isEmpty(res.getString("batch_num")) ? "" : res.getString("batch_num")), (StringUtils.isEmpty(res.getString("result")) ? "" : res.getString("result")));
									
									if(submitResponse.getCode() == 0){
										//将号码加入到memcached【重号过滤使用】
										int uid = userMode.getId();
										int filter_time = userMode.getFilterTime();
										if(filter_time != 0){
											for(String phone : submitRequest.getPhones()){
												Integer record = MemCachedUtil.get(Integer.class, "phone_records", uid + "_" + phone);
												if(null == record){
													MemCachedUtil.set("phone_records", uid + "_" + phone, 0, filter_time * 60);
												}
											}
										}
									}
									
									msg.setBody(submitResponse.toJson(userMode.getKey()));
								}else{
									SubmitResponse submitResponse = new SubmitResponse(Constants.CODE_AUTH_ERROR, "", "server error");
									msg.setBody(submitResponse.toJson(userMode.getKey()));
								}
							}
						}
					}
				}else{
					SubmitResponse submitResponse = new SubmitResponse(-1, "", "auth fail");
					msg.setBody(submitResponse.toJson(userMode.getKey()));
				}
			}
			
			break;
			
//以下 已替换成Push方式  详见 PushServer模块
			
//		case Constants.WEBSERVICE_COMMAND_DELIVER:
//			UserMode userMode1 = CacheUtil.get(UserMode.class, SmsiServer.USER_CACHE_KEY, uId);
//			if(null != userMode1){
//				DeliverRequest deliverRequest = new DeliverRequest();
//				deliverRequest = deliverRequest.toObj(body, userMode1.getKey());
//				
//				LOG.info(deliverRequest.toString());
//				
//				msg.setCommandId(Constants.WEBSERVICE_COMMAND_DELIVER_RESP);
//				
//				if(StringUtils.isEmpty(userMode1.getHost()) || (StringUtils.isNotEmpty(userMode1.getHost()) && userMode1.getHost().equals(clientIP))){
//					if(userMode1.getPwd().equalsIgnoreCase(Base64.encrypt(deliverRequest.getPassWord(), Base64.KEY))){
//						DatabaseTransaction trans = new DatabaseTransaction(true);
//						try {
//							DbService dbService = new DbService(trans);
//							List<Deliver> delivers = dbService.getDeliverList(userMode1.getUserName());
//							trans.commit();
//							
//							Deliver[] deliverArray = new Deliver[delivers.size()];
//							for(int i = 0; i < deliverArray.length; i++){
//								deliverArray[i] = delivers.get(i);
//							}
//							DeliverResponse deliverResponse = new DeliverResponse(deliverArray.length, deliverArray);
//							msg.setBody(deliverResponse.toJson(userMode1.getKey()));
//						} catch (Exception ex) {
//							trans.rollback();
//						} finally {
//							trans.close();
//						}
//					}
//				}
//			}
//			
//			break;
//		case Constants.WEBSERVICE_COMMAND_REPORT:
//			UserMode userMode2 = CacheUtil.get(UserMode.class, SmsiServer.USER_CACHE_KEY, uId);
//			if(null != userMode2){
//				ReportRequest reportRequest = new ReportRequest();
//				reportRequest = reportRequest.toObj(body, userMode2.getKey());
//				
//				LOG.info(reportRequest.toString());
//				
//				msg.setCommandId(Constants.WEBSERVICE_COMMAND_REPORT_RESP);
//				
//				if(StringUtils.isEmpty(userMode2.getHost()) || (StringUtils.isNotEmpty(userMode2.getHost()) && userMode2.getHost().equals(clientIP))){
//					if(userMode2.getPwd().equalsIgnoreCase(Base64.encrypt(reportRequest.getPassWord(), Base64.KEY))){
//						DatabaseTransaction trans = new DatabaseTransaction(true);
//						try {
//							DbService dbService = new DbService(trans);
//							List<Report> reports = dbService.getReportList(userMode2.getUserName());
//							trans.commit();
//							
//							Report[] reportArray = new Report[reports.size()];
//							for(int i = 0; i < reportArray.length; i++){
//								reportArray[i] = reports.get(i);
//							}
//							ReportResponse reportResponse = new ReportResponse(reportArray.length, reportArray);
//							msg.setBody(reportResponse.toJson(userMode2.getKey()));
//						} catch (Exception ex) {
//							trans.rollback();
//						} finally {
//							trans.close();
//						}
//					}
//				}
//			}
//			
//			break;
		case Constants.WEBSERVICE_COMMAND_BALANCE:
			UserMode userMode3 = CacheUtil.get(UserMode.class, SmsiServer.USER_CACHE_KEY, uId);
			if(null != userMode3){
				BalanceRequest balanceRequest = new BalanceRequest();
				balanceRequest = balanceRequest.toObj(body, userMode3.getKey());
				
				LOG.info(balanceRequest.toString());
				
				msg.setCommandId(Constants.WEBSERVICE_COMMAND_BALANCE_RESP);
				
				if(StringUtils.isEmpty(userMode3.getHost()) || (StringUtils.isNotEmpty(userMode3.getHost()) && userMode3.getHost().equals(clientIP))){
					if(userMode3.getPwd().equalsIgnoreCase(Base64.encrypt(balanceRequest.getPassWord(), Base64.KEY))){
						DatabaseTransaction trans = new DatabaseTransaction(true);
						try {
							DbService dbService = new DbService(trans);
							int availableBalance = dbService.getAvailableBalance(userMode3.getUserName());
							List<String> productBalanceList = dbService.getProductBalance(userMode3.getUserName());
							trans.commit();
							
							ProductBalance[] productBalanceArray = new ProductBalance[productBalanceList.size()];
							for(int i = 0; i < productBalanceArray.length;i++){
								String[] strArray = productBalanceList.get(i).split("#");
								productBalanceArray[i] = new ProductBalance(Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1]));
							}
							BalanceResponse balanceResponse = new BalanceResponse(availableBalance, productBalanceArray);
							msg.setBody(balanceResponse.toJson(userMode3.getKey()));
						} catch (Exception ex) {
							trans.rollback();
						} finally {
							trans.close();
						}
					}
				}
			}
			
			break;
		default:
			
			break;
		}
		
		return msg.toJson();
	}
	
	/**
	 * 调用接口提交短信
	 * 
	 * @param request
	 * @param uId
	 * @return
	 */
	private JSONObject submit(SubmitRequest request, int uId){
		JSONObject json = new JSONObject();
		
		String[] phoneArray = request.getPhones();
		String phones = "";
		for(int i = 0; i < phoneArray.length; i++){
			phones += phoneArray[i];
			if(i != (phoneArray.length - 1)){
				phones += "\r\n";
			}
		}
		json.put("phones", phones);
		json.put("contents", request.getContent());
		json.put("productid", request.getProductId());
		json.put("userid", uId);
		json.put("expid", request.getExpId());
		json.put("timing_date", request.getSendTime());
		
		long seed = System.currentTimeMillis();
		json.put("seed", seed);
		json.put("key", PostKeyUtil.generateKey(seed));
		
		SmsTransferClient client = new SmsTransferClient(CacheUtil.get(String.class, "SUBMIT_INFO", "submit.hostname"), CacheUtil.get(Integer.class, "SUBMIT_INFO", "submit.port"));
		Object result = client.submit(json.toJSONString());
		client.close();
		
		if(null != result){
			JSONObject res = JSONObject.parseObject(result.toString());
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("result", StringUtils.isEmpty(res.getString("resultEN")) ? "" : res.getString("resultEN"));
			jsonObject.put("code", null == res.getInteger("code") ? Constants.CODE_AUTH_ERROR : res.getInteger("code"));
			jsonObject.put("batch_num", StringUtils.isEmpty(res.getString("batch_num")) ? "" : res.getString("batch_num"));
			
			return jsonObject;
		}
		return null;
	}
	
	/**
	 * 获取IP地址
	 * 
	 * @return
	 */
	private static String getIp(){
		WebServiceContext context = new WebServiceContextImpl();
		MessageContext ctx = context.getMessageContext();
		HttpServletRequest request = (HttpServletRequest)ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
		return request.getRemoteAddr();
	}
}