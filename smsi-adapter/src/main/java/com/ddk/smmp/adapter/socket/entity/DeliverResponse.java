package com.ddk.smmp.adapter.socket.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.model.Deliver;
import com.ddk.smmp.adapter.model.Report;
import com.ddk.smmp.adapter.socket.entity.helper.Body;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 *
 */
public class DeliverResponse extends Body<DeliverResponse> {
	private static final long serialVersionUID = 1014612843400398767L;
	
	private int num;
	private Deliver[] delivers;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Deliver[] getDelivers() {
		return delivers;
	}

	public void setDelivers(Deliver[] delivers) {
		this.delivers = delivers;
	}

	public DeliverResponse() {
		super();
	}

	public DeliverResponse(int num, Deliver[] delivers) {
		super();
		this.num = num;
		this.delivers = delivers;
	}

	@Override
	public String toJson(String key) {
		if(null == key){
			return JSON.toJSONString(this);
		}
		String result = null;
		try {
			result = AesUtil.encrypt(JSON.toJSONString(this), key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public DeliverResponse toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}
		
		return JSON.parseObject(json, DeliverResponse.class);
	}

	@Override
	public String toString() {
		return "DeliverResponse [num=" + num + ", delivers=" + Arrays.toString(delivers) + "]";
	}
	
	public static void main(String[] args){
		DeliverResponse response = new DeliverResponse(2, new Deliver[] {
				new Deliver("15214380001", "测试上行内容1", "2014-07-30 11:37:00"),
				new Deliver("15214380002", "测试上行内容2", "2014-07-30 11:37:00") });
		Msg msg = new Msg(Constants.SOCKET_COMMAND_DELIVER_RESP, SeqUtil.generateSeq(), response.toJson(null));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		DeliverResponse deliverResponse = new DeliverResponse();
		deliverResponse = deliverResponse.toObj(msg2.getBody(), null);
		
		System.out.println(deliverResponse);
	}
}