package com.ddk.smmp.adapter.socket.entity;

import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.socket.entity.helper.Body;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.socket.entity.helper.ProductBalance;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 *
 */
public class BalanceResponse extends Body<BalanceResponse> {
	private static final long serialVersionUID = 1014612843400398767L;
	
	private Integer account;
	private ProductBalance[] products ;
	
	public Integer getAccount() {
		return account;
	}

	public void setAccount(Integer account) {
		this.account = account;
	}

	public ProductBalance[] getProducts() {
		return products;
	}

	public void setProducts(ProductBalance[] products) {
		this.products = products;
	}

	public BalanceResponse() {
		super();
	}

	public BalanceResponse(Integer account,
			ProductBalance[] products) {
		super();
		this.account = account;
		this.products = products;
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
	public BalanceResponse toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}
		
		return JSON.parseObject(json, BalanceResponse.class);
	}
	
	@Override
	public String toString() {
		return "BalanceResponse [account=" + account + ", products=" + Arrays.toString(products) + "]";
	}

	public static void main(String[] args){
		String key = DigestUtils.md5Hex("siookey");
		
		BalanceResponse response = new BalanceResponse(2000,
				new ProductBalance[] { new ProductBalance(1, 5000),
						new ProductBalance(2, 10000) });
		
		Msg msg = new Msg(Constants.SOCKET_COMMAND_BALANCE_RESP, SeqUtil.generateSeq(), response.toJson(null));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		BalanceResponse balanceResponse = new BalanceResponse();
		balanceResponse = balanceResponse.toObj(msg2.getBody(), key);
		
		System.out.println(balanceResponse);
	}
}