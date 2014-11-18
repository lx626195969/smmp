package com.ddk.smmp.adapter.webservice.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.webservice.entity.helper.Body;
import com.ddk.smmp.adapter.webservice.entity.helper.ProductBalance;

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

	public BalanceResponse(Integer account, ProductBalance[] products) {
		super();
		this.account = account;
		this.products = products;
	}

	@Override
	public String toJson(String key) {
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
}