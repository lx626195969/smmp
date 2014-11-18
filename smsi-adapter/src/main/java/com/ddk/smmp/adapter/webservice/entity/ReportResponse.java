package com.ddk.smmp.adapter.webservice.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.model.Report;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.webservice.entity.helper.Body;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 * 
 */
public class ReportResponse extends Body<ReportResponse> {
	private static final long serialVersionUID = -2431597592821065034L;

	private int num;
	private Report[] reports;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Report[] getReports() {
		return reports;
	}

	public void setReports(Report[] reports) {
		this.reports = reports;
	}

	public ReportResponse() {
		super();
	}

	public ReportResponse(int num, Report[] reports) {
		super();
		this.num = num;
		this.reports = reports;
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
	public ReportResponse toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}

		return JSON.parseObject(json, ReportResponse.class);
	}

	@Override
	public String toString() {
		return "ReportResponse [num=" + num + ", reports=" + Arrays.toString(reports) + "]";
	}
}