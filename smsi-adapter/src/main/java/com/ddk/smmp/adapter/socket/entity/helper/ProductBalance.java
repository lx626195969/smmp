package com.ddk.smmp.adapter.socket.entity.helper;

import java.io.Serializable;

/**
 * @author leeson 2014年7月9日 下午12:03:44 li_mr_ceo@163.com <br>
 * 
 */
public class ProductBalance implements Serializable {
	private static final long serialVersionUID = 4313351861039483252L;

	private Integer productId;
	private Integer balance;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public ProductBalance(Integer productId, Integer balance) {
		super();
		this.productId = productId;
		this.balance = balance;
	}

	public ProductBalance() {
		super();
	}

	@Override
	public String toString() {
		return "ProductBalance [productId=" + productId + ", balance=" + balance + "]";
	}
}