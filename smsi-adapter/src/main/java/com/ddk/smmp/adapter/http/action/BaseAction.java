package com.ddk.smmp.adapter.http.action;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.http.entity.BalanceRequest;
import com.ddk.smmp.adapter.http.entity.DeliverRequest;
import com.ddk.smmp.adapter.http.entity.ReportRequest;
import com.ddk.smmp.adapter.http.entity.SubmitRequest;
import com.ddk.smmp.adapter.http.entity.helper.Body;
import com.ddk.smmp.adapter.http.entity.helper.EMethod;
import com.ddk.smmp.adapter.http.entity.helper.EMsgType;
import com.ddk.smmp.adapter.http.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.CacheUtil;
import com.ddk.smmp.adapter.utils.Tuple2;
import com.ddk.smmp.adapter.web.SmsiServer;

/**
 * @author leeson 2014年7月10日 上午10:00:32 li_mr_ceo@163.com <br>
 * 
 */
public class BaseAction extends ServerResource {
	
	
	
	/**
	 * 获取GET请求参数
	 * 
	 * @param name
	 * @return
	 */
	protected String get(String name) {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		return form.getFirstValue(name);
	}

	/**
	 * 获取请求消息
	 * 
	 * @param method GET/POST
	 * @param representation GET时为null/POST时必填
	 * @return
	 */
	protected Msg getMsg(EMethod method, Representation representation) {
		if (method.equals(EMethod.GET)) {
			return new Msg(get("userName"), get("body"));
		}
		
		if (method.equals(EMethod.POST)) {
			Form form = new Form(representation);
			return new Msg(form.getFirstValue("userName"), form.getFirstValue("body"));
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	protected Tuple2<UserMode, Body> getBody(EMsgType type, EMethod method, Representation representation) {
		Msg msg = getMsg(method, representation);
		if (StringUtils.isNotEmpty(msg.getuId()) && StringUtils.isNotEmpty(msg.getBody())) {
			UserMode userMode = CacheUtil.get(UserMode.class, SmsiServer.USER_CACHE_KEY, msg.getuId());
			if(null != userMode){
				String ip = getRequest().getClientInfo().getAddress();
				if(StringUtils.isEmpty(userMode.getHost()) || (StringUtils.isNotEmpty(userMode.getHost()) && userMode.getHost().equals(ip))){
					if (type.equals(EMsgType.SUBMIT)) {
						SubmitRequest submitRequest = new SubmitRequest();
						return new Tuple2<UserMode, Body>(userMode, submitRequest.toObj(msg.getBody(), userMode.getKey()));
					}
					
					if (type.equals(EMsgType.REPORT)) {
						ReportRequest reportRequest = new ReportRequest();
						return new Tuple2<UserMode, Body>(userMode, reportRequest.toObj(msg.getBody(), userMode.getKey()));
					}

					if (type.equals(EMsgType.BALANCE)) {
						BalanceRequest balanceRequest = new BalanceRequest();
						return new Tuple2<UserMode, Body>(userMode, balanceRequest.toObj(msg.getBody(), userMode.getKey()));
					}
					
					if (type.equals(EMsgType.DELIVER)) {
						DeliverRequest deliverRequest = new DeliverRequest();
						return new Tuple2<UserMode, Body>(userMode, deliverRequest.toObj(msg.getBody(), userMode.getKey()));
					}
				}
			}
		}

		return null;
	}
}
