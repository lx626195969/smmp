package com.ddk.smmp.adapter.http.entity;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.http.entity.helper.Body;
import com.ddk.smmp.adapter.utils.AesUtil;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 * 
 */
public class ReportRequest extends Body<ReportRequest> {
	private static final long serialVersionUID = 1014612843400398767L;

	private String passWord;

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public ReportRequest() {
		super();
	}

	public ReportRequest(String passWord) {
		super();
		this.passWord = passWord;
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
	public ReportRequest toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}

		return JSON.parseObject(json, ReportRequest.class);
	}

	@Override
	public String toString() {
		return "ReportRequest [passWord=" + passWord + "]";
	}
	
	public static void main(String[] args) {
		ReportRequest request = new ReportRequest("test");
		System.out.println(URLEncoder.encode(request.toJson("iNlNXXIBopo7QR+12vWWow==")));
		System.out.println(JSON.toJSONString(request, true));
	}
}