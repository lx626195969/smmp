package com.ddk.smmp.adapter.socket.entity;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.socket.entity.helper.Body;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * 
 * @author leeson 2014年7月9日 上午11:07:44 li_mr_ceo@163.com <br>
 *
 */
public class ConnectResponse extends Body<ConnectResponse> {
	private static final long serialVersionUID = 1014612843400398767L;
	
	private Integer code;
	private String result;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public ConnectResponse() {
		super();
	}

	public ConnectResponse(Integer code, String result) {
		super();
		this.code = code;
		this.result = result;
	}

	@Override
	public String toJson(String key) {
		return JSON.toJSONString(this);
	}
	
	@Override
	public ConnectResponse toObj(String json, String key) {
		return JSON.parseObject(json, ConnectResponse.class);
	}
	
	@Override
	public String toString() {
		return "ConnectResponse [code=" + code + ", result=" + result + "]";
	}

	public static void main(String[] args){
		//String key = DigestUtils.md5Hex("siookey");
		
		ConnectResponse response = new ConnectResponse(0, "预留字段");
		
		Msg msg = new Msg(Constants.SOCKET_COMMAND_CONNECT_RESP, SeqUtil.generateSeq(), response.toJson(null));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		ConnectResponse connectResponse = new ConnectResponse();
		connectResponse = connectResponse.toObj(msg2.getBody(), null);
		
		System.out.println(connectResponse);
	}
}