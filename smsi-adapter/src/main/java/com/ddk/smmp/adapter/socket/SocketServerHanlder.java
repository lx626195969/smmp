package com.ddk.smmp.adapter.socket;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.adapter.model.Deliver;
import com.ddk.smmp.adapter.model.Report;
import com.ddk.smmp.adapter.service.DbService;
import com.ddk.smmp.adapter.socket.entity.BalanceRequest;
import com.ddk.smmp.adapter.socket.entity.BalanceResponse;
import com.ddk.smmp.adapter.socket.entity.ConnectRequest;
import com.ddk.smmp.adapter.socket.entity.ConnectResponse;
import com.ddk.smmp.adapter.socket.entity.DeliverRequest;
import com.ddk.smmp.adapter.socket.entity.DeliverResponse;
import com.ddk.smmp.adapter.socket.entity.ReportRequest;
import com.ddk.smmp.adapter.socket.entity.ReportResponse;
import com.ddk.smmp.adapter.socket.entity.SubmitRequest;
import com.ddk.smmp.adapter.socket.entity.SubmitResponse;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.socket.entity.helper.ProductBalance;
import com.ddk.smmp.adapter.submit_socket_client.SmsTransferClient;
import com.ddk.smmp.adapter.utils.CacheUtil;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.web.SmsiServer;
import com.ddk.smmp.util.Base64;
import com.ddk.smmp.util.PostKeyUtil;

/**
 * @author leeson 2014年7月7日 下午5:46:27 li_mr_ceo@163.com <br>
 * 
 */
public class SocketServerHanlder extends IoHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(SocketServerHanlder.class);
	public static final String CURRENT_USER = "currentUser";
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String clientIP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
		Msg msg = Msg.toObj(message.toString());
		if(null == msg){
			logger.info("###ILLEGAL REQUEST###" + message.toString() + "\r\n");
			session.close(true);
		}
		
		switch (msg.getCommandId()) {
		case Constants.SOCKET_COMMAND_CONNECT:
			ConnectRequest connectRequest = new ConnectRequest();
			connectRequest = connectRequest.toObj(msg.getBody(), null);
			
			logger.info("###RECEIVE###" + msg.toString() + "\r\n" + connectRequest.toString() + "\r\n");
			
			UserMode userMode = CacheUtil.get(UserMode.class, SmsiServer.USER_CACHE_KEY, connectRequest.getUid());
			if(StringUtils.isEmpty(userMode.getHost()) || (StringUtils.isNotEmpty(userMode.getHost()) && userMode.getHost().equals(clientIP))){
				if(null != userMode && userMode.getPwd().equalsIgnoreCase(Base64.encrypt(connectRequest.getPassWord(), Base64.KEY))){
					ConnectResponse connectResponse = new ConnectResponse(Constants.CONNECT_OK, "");
					Msg back = new Msg(Constants.SOCKET_COMMAND_CONNECT_RESP, msg.getSeq(), connectResponse.toJson(null));
					
					session.setAttribute(CURRENT_USER, userMode);//将用户信息加入到session
					
					session.write(back.toJson());
				}
			}else{
				ConnectResponse connectResponse = new ConnectResponse(Constants.CONNECT_VALI_FAIL, "auth fail");
				Msg back = new Msg(Constants.SOCKET_COMMAND_CONNECT_RESP, msg.getSeq(), connectResponse.toJson(null));
				session.write(back.toJson());
			}

			break;
		case Constants.SOCKET_COMMAND_SUBMIT:
			UserMode userMode2 = (UserMode)session.getAttribute(CURRENT_USER);
			if(null != userMode2){
				SubmitRequest submitRequest = new SubmitRequest();
				submitRequest = submitRequest.toObj(msg.getBody(), userMode2.getKey());
				
				logger.info("###RECEIVE###" + msg.toString() + "\r\n" + submitRequest.toString() + "\r\n");
				
				//号码过多
				if(submitRequest.getPhones().length > 2000){
					SubmitResponse submitResponse = new SubmitResponse(11, "", "too many numbers");
					Msg back = new Msg(Constants.SOCKET_COMMAND_SUBMIT_RESP, msg.getSeq(), submitResponse.toJson(userMode2.getKey()));
					session.write(back.toJson());
				}else{
					Boolean state = CacheUtil.get(Boolean.class, "SOCKET_USER_STATE", userMode2.getId());
					if(null != state && state == true){
						//提交太快
						SubmitResponse submitResponse = new SubmitResponse(12, "", "submit too fast");
						Msg back = new Msg(Constants.SOCKET_COMMAND_SUBMIT_RESP, msg.getSeq(), submitResponse.toJson(userMode2.getKey()));
						session.write(back.toJson());
					}else{
						CacheUtil.put("SOCKET_USER_STATE", userMode2.getId(), true);
						JSONObject result = null;
						try {
							result = submit(submitRequest, userMode2.getId());
						} catch (Exception e) {
							
						}finally{
							CacheUtil.put("SOCKET_USER_STATE", userMode2.getId(), false);
						}
						
						if(null != result){
							JSONObject res = JSONObject.parseObject(result.toString());
							SubmitResponse submitResponse = new SubmitResponse((null == res.getInteger("code") ? Constants.CODE_AUTH_ERROR : res.getInteger("code")), (StringUtils.isEmpty(res.getString("batch_num")) ? "" : res.getString("batch_num")), (StringUtils.isEmpty(res.getString("result")) ? "" : res.getString("result")));
							
							if(submitResponse.getCode() == 0){
								//将号码加入到数据库【重号过滤使用】
								DatabaseTransaction trans = new DatabaseTransaction(true);
								try {
									DbService dbService = new DbService(trans);
									dbService.insertPhoneRecords(userMode2.getId(), submitRequest.getPhones());
									trans.commit();
								} catch (Exception ex) {
									trans.rollback();
								} finally {
									trans.close();
								}
							}
							
							Msg back1 = new Msg(Constants.SOCKET_COMMAND_SUBMIT_RESP, msg.getSeq(), submitResponse.toJson(userMode2.getKey()));
							session.write(back1.toJson());
						}else{
							SubmitResponse submitResponse = new SubmitResponse(Constants.CODE_AUTH_ERROR, "", "server error");
							Msg back1 = new Msg(Constants.SOCKET_COMMAND_SUBMIT_RESP, msg.getSeq(), submitResponse.toJson(userMode2.getKey()));
							session.write(back1.toJson());
						}
					}
				}
			}
			
			break;
//以下 已替换成Push方式  详见 PushServer模块
//		case Constants.SOCKET_COMMAND_DELIVER:
//			UserMode userMode3 = (UserMode)session.getAttribute(CURRENT_USER);
//			if(null != userMode3){
//				DeliverRequest deliverRequest = new DeliverRequest();
//				deliverRequest = deliverRequest.toObj(msg.getBody(), userMode3.getKey());
//				
//				logger.info("###RECEIVE###" + msg.toString() + "\r\n" + deliverRequest.toString() + "\r\n");
//				
//				DatabaseTransaction trans = new DatabaseTransaction(true);
//				try {
//					DbService dbService = new DbService(trans);
//					List<Deliver> delivers = dbService.getDeliverList(userMode3.getUserName());
//					trans.commit();
//					
//					Deliver[] deliverArray = new Deliver[delivers.size()];
//					for(int i = 0; i < deliverArray.length; i++){
//						deliverArray[i] = delivers.get(i);
//					}
//					DeliverResponse deliverResponse = new DeliverResponse(deliverArray.length, deliverArray);
//					
//					Msg back2 = new Msg(Constants.SOCKET_COMMAND_DELIVER_RESP, msg.getSeq(), deliverResponse.toJson(userMode3.getKey()));
//					session.write(back2.toJson());
//				} catch (Exception ex) {
//					trans.rollback();
//				} finally {
//					trans.close();
//				}
//			}
//			
//			break;
//		case Constants.SOCKET_COMMAND_REPORT:
//			UserMode userMode4 = (UserMode)session.getAttribute(CURRENT_USER);
//			if(null != userMode4){
//				ReportRequest reportRequest = new ReportRequest();
//				reportRequest = reportRequest.toObj(msg.getBody(), userMode4.getKey());
//				
//				logger.info("###RECEIVE###" + msg.toString() + "\r\n" + reportRequest.toString() + "\r\n");
//				
//				DatabaseTransaction trans = new DatabaseTransaction(true);
//				try {
//					DbService dbService = new DbService(trans);
//					List<Report> reports = dbService.getReportList(userMode4.getUserName());
//					trans.commit();
//					
//					Report[] reportArray = new Report[reports.size()];
//					for(int i = 0; i < reportArray.length; i++){
//						reportArray[i] = reports.get(i);
//					}
//					ReportResponse reportResponse = new ReportResponse(reportArray.length, reportArray);
//					Msg back3 = new Msg(Constants.SOCKET_COMMAND_REPORT_RESP, msg.getSeq(), reportResponse.toJson(userMode4.getKey()));
//					session.write(back3.toJson());
//				} catch (Exception ex) {
//					trans.rollback();
//				} finally {
//					trans.close();
//				}
//			}
//			
//			break;
		case Constants.SOCKET_COMMAND_BALANCE:
			UserMode userMode5 = (UserMode)session.getAttribute(CURRENT_USER);
			if(null != userMode5){
				BalanceRequest balanceRequest = new BalanceRequest();
				balanceRequest = balanceRequest.toObj(msg.getBody(), userMode5.getKey());
				
				logger.info("###RECEIVE###" + msg.toString() + "\r\n" + balanceRequest.toString() + "\r\n");
				
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					int availableBalance = dbService.getAvailableBalance(userMode5.getUserName());
					List<String> productBalanceList = dbService.getProductBalance(userMode5.getUserName());
					trans.commit();
					
					ProductBalance[] productBalanceArray = new ProductBalance[productBalanceList.size()];
					for(int i = 0; i < productBalanceArray.length;i++){
						String[] strArray = productBalanceList.get(i).split("#");
						productBalanceArray[i] = new ProductBalance(Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1]));
					}
					
					BalanceResponse balanceResponse = new BalanceResponse(availableBalance, productBalanceArray);
					Msg back4 = new Msg(Constants.SOCKET_COMMAND_BALANCE_RESP, msg.getSeq(), balanceResponse.toJson(userMode5.getKey()));
					session.write(back4.toJson());
				} catch (Exception ex) {
					trans.rollback();
				} finally {
					trans.close();
				}
			}
			
			break;
		default:
			
			break;
		}
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
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}
}