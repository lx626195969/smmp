package com.ddk.smmp.adapter.socket.entity;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.socket.entity.helper.Body;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * @author leeson 2014年7月9日 上午9:15:48 li_mr_ceo@163.com <br>
 * 
 */
public class ConnectRequest extends Body<ConnectRequest> {
	private static final long serialVersionUID = 1014612843400398767L;
	
	private String uid;
	private String passWord;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public ConnectRequest() {
		super();
	}

	public ConnectRequest(String uid, String passWord) {
		super();
		this.uid = uid;
		this.passWord = passWord;
	}

	@Override
	public String toJson(String key) {
		return JSON.toJSONString(this);
	}
	
	@Override
	public ConnectRequest toObj(String json, String key) {
		return JSON.parseObject(json, ConnectRequest.class);
	}
	
	@Override
	public String toString() {
		return "ConnectRequest [uid=" + uid + ", passWord=" + passWord + "]";
	}

	public static void main(String[] args){
		//String key = DigestUtils.md5Hex("siookey");
		
		ConnectRequest request = new ConnectRequest("lixin890828", "admin123456");
		
		Msg msg = new Msg(Constants.SOCKET_COMMAND_CONNECT, SeqUtil.generateSeq(), request.toJson(null));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		ConnectRequest connectRequest = new ConnectRequest();
		connectRequest = connectRequest.toObj(msg2.getBody(), null);
		
		System.out.println(connectRequest);
	}
}