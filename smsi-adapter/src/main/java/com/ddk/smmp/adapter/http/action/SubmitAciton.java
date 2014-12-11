package com.ddk.smmp.adapter.http.action;

import org.apache.commons.lang3.StringUtils;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.http.entity.SubmitRequest;
import com.ddk.smmp.adapter.http.entity.SubmitResponse;
import com.ddk.smmp.adapter.http.entity.helper.Body;
import com.ddk.smmp.adapter.http.entity.helper.EMethod;
import com.ddk.smmp.adapter.http.entity.helper.EMsgType;
import com.ddk.smmp.adapter.submit_socket_client.SmsTransferClient;
import com.ddk.smmp.adapter.utils.CacheUtil;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.Tuple2;
import com.ddk.smmp.util.Base64;
import com.ddk.smmp.util.MemCachedUtil;
import com.ddk.smmp.util.PostKeyUtil;

/**
 * @author leeson 2014年7月9日 下午6:11:49 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitAciton extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger((SubmitAciton.class).getSimpleName());
	
	@SuppressWarnings("rawtypes")
	@Get
	public String GET_Submit() throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.SUBMIT, EMethod.GET, null);
		if(null != tuple2 && null != tuple2.e2){
			SubmitRequest request = (SubmitRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				//号码过多
				if(request.getPhones().length > 200){
					SubmitResponse submitResponse = new SubmitResponse(11, "", "too many numbers");
					return submitResponse.toJson(tuple2.e1.getKey());
				}
				
				Boolean state = CacheUtil.get(Boolean.class, "HTTP_USER_STATE", tuple2.e1.getId());
				if(null != state && state == true){
					//提交太快
					SubmitResponse submitResponse = new SubmitResponse(12, "", "submit too fast");
					return submitResponse.toJson(tuple2.e1.getKey());
				}else{
					CacheUtil.put("HTTP_USER_STATE", tuple2.e1.getId(), true);
					JSONObject json = null;
					try {
						json = submit(request, tuple2.e1);
					} catch (Exception e) {
						
					}finally{
						CacheUtil.put("HTTP_USER_STATE", tuple2.e1.getId(), false);
					}
					
					if(null != json){
						if(json.getInteger("code") == 0){
							//将号码加入到memcached【重号过滤使用】
							int uid = tuple2.e1.getId();
							int filter_time = tuple2.e1.getFilterTime();
							if(filter_time != 0){
								for(String phone : request.getPhones()){
									Integer record = MemCachedUtil.get(Integer.class, "phone_records", uid + "_" + phone);
									if(null == record){
										MemCachedUtil.set("phone_records", uid + "_" + phone, 0, filter_time * 60);
									}
								}
							}
						}
						
						SubmitResponse submitResponse = new SubmitResponse(json.getInteger("code"), json.getString("batch_num"), json.getString("result"));
						return submitResponse.toJson(tuple2.e1.getKey());
					}else{
						SubmitResponse submitResponse = new SubmitResponse(Constants.CODE_SERVER_ERROR, "", "server error");
						return submitResponse.toJson(tuple2.e1.getKey());
					}
				}
			}else{
				SubmitResponse submitResponse = new SubmitResponse(Constants.CODE_AUTH_ERROR, "", "auth error");
				return submitResponse.toJson(tuple2.e1.getKey());
			}
		}
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Post
	public Representation POST_Submit(Representation representation) throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.SUBMIT, EMethod.POST, representation);
		if(null != tuple2 && null != tuple2.e2){
			SubmitRequest request = (SubmitRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				//号码过多
				if(request.getPhones().length > 2000){
					SubmitResponse submitResponse = new SubmitResponse(11, "", "too many numbers");
					return new StringRepresentation(submitResponse.toJson(tuple2.e1.getKey()));
				}
				
				Boolean state = CacheUtil.get(Boolean.class, "HTTP_USER_STATE", tuple2.e1.getId());
				if(null != state && state == true){
					//提交太快
					SubmitResponse submitResponse = new SubmitResponse(12, "", "submit too fast");
					return new StringRepresentation(submitResponse.toJson(tuple2.e1.getKey()));
				}else{
					CacheUtil.put("HTTP_USER_STATE", tuple2.e1.getId(), true);
					JSONObject json = null;
					try {
						json = submit(request, tuple2.e1);
					} catch (Exception e) {
						
					}finally{
						CacheUtil.put("HTTP_USER_STATE", tuple2.e1.getId(), false);
					}
					
					if(null != json){
						if(json.getInteger("code") == 0){
							//将号码加入到memcached【重号过滤使用】
							int uid = tuple2.e1.getId();
							int filter_time = tuple2.e1.getFilterTime();
							if(filter_time != 0){
								for(String phone : request.getPhones()){
									Integer record = MemCachedUtil.get(Integer.class, "phone_records", uid + "_" + phone);
									if(null == record){
										MemCachedUtil.set("phone_records", uid + "_" + phone, 0, filter_time * 60);
									}
								}
							}
						}
						
						SubmitResponse submitResponse = new SubmitResponse(json.getInteger("code"), json.getString("batch_num"), json.getString("result"));
						return new StringRepresentation(submitResponse.toJson(tuple2.e1.getKey()));
					}else{
						SubmitResponse submitResponse = new SubmitResponse(Constants.CODE_SERVER_ERROR, "", "server error");
						return new StringRepresentation(submitResponse.toJson(tuple2.e1.getKey()));
					}
				}
			}else{
				SubmitResponse submitResponse = new SubmitResponse(Constants.CODE_AUTH_ERROR, "", "auth error");
				return new StringRepresentation(submitResponse.toJson(tuple2.e1.getKey()));
			}
		}
		return null;
	}
	
	private JSONObject submit(SubmitRequest request, UserMode user){
		JSONObject json = new JSONObject();
		
		String[] phoneArray = request.getPhones();
		StringBuffer phones = new StringBuffer();
		for(int i = 0; i < phoneArray.length; i++){
			phones.append(phoneArray[i]);
			if(i != (phoneArray.length - 1)){
				phones.append("\r\n");
			}
		}
		json.put("phones", phones.toString());
		json.put("contents", request.getContent());
		json.put("productid", request.getProductId());
		json.put("userid", user.getId());
		json.put("expid", request.getExpId());
		json.put("timing_date", request.getSendTime());
		
		long seed = System.currentTimeMillis();
		json.put("seed", seed);
		json.put("key", PostKeyUtil.generateKey(seed));
		
		SmsTransferClient client = new SmsTransferClient(CacheUtil.get(String.class, "SUBMIT_INFO", "submit.hostname"), CacheUtil.get(Integer.class, "SUBMIT_INFO", "submit.port"));
		Tuple2<String, Long> result = client.submit(json.toJSONString(), user.getUserName());
		client.close();
		
		logger.info("R <- " + result.e1 + "[SID:" + result.e2 + "]");
		
		if(null != result.e1){
			JSONObject res = JSONObject.parseObject(result.e1);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("result", StringUtils.isEmpty(res.getString("resultEN")) ? "" : res.getString("resultEN"));
			jsonObject.put("code", null == res.getInteger("code") ? Constants.CODE_AUTH_ERROR : res.getInteger("code"));
			jsonObject.put("batch_num", StringUtils.isEmpty(res.getString("batch_num")) ? "" : res.getString("batch_num"));
			
			return jsonObject;
		}
		return null;
	}
}