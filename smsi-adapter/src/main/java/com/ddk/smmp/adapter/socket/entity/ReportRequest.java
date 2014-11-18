package com.ddk.smmp.adapter.socket.entity;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.socket.entity.helper.Body;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 * 
 */
public class ReportRequest extends Body<ReportRequest> {
	private static final long serialVersionUID = 1014612843400398767L;

	public ReportRequest() {
		super();
	}

	@Override
	public String toJson(String key) {
		return "{}";
	}

	@Override
	public ReportRequest toObj(String json, String key) {
		return JSON.parseObject(json, ReportRequest.class);
	}
	
	public static void main(String[] args){
		ReportRequest request = new ReportRequest();
		
		Msg msg = new Msg(Constants.SOCKET_COMMAND_REPORT, SeqUtil.generateSeq(), request.toJson(null));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		ReportRequest reportRequest = new ReportRequest();
		reportRequest = reportRequest.toObj(msg2.getBody(), null);
		
		System.out.println(reportRequest);
	}
}