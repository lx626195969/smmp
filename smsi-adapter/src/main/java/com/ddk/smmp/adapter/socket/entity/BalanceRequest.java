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
public class BalanceRequest extends Body<BalanceRequest> {
	private static final long serialVersionUID = 1014612843400398767L;
	
	public BalanceRequest() {
		super();
	}

	@Override
	public String toJson(String key) {
		return "{}";
	}
	
	@Override
	public BalanceRequest toObj(String json, String key) {
		return JSON.parseObject(json, BalanceRequest.class);
	}
	
	public static void main(String[] args){
		String key = "bJFCDwVeVvP93Nhga2bgXA==";
		
		BalanceRequest request = new BalanceRequest();
		
		Msg msg = new Msg(Constants.SOCKET_COMMAND_BALANCE, SeqUtil.generateSeq(), request.toJson(key));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		BalanceRequest balanceRequest = new BalanceRequest();
		balanceRequest = balanceRequest.toObj(msg2.getBody(), key);
		
		System.out.println(balanceRequest);
	}
}