package com.ddk.smmp.adapter.socket.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
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
	
	public static void main(String[] args){
		ReportResponse response = new ReportResponse(2, new Report[] {
				new Report("2013073011180001", "15214380001", "DELIVRD",
						"2014-07-30 11:21:00"),
				new Report("2013073011190001", "15214380002", "DELIVRD",
						"2014-07-30 11:22:00") });
		Msg msg = new Msg(Constants.SOCKET_COMMAND_REPORT_RESP, SeqUtil.generateSeq(), response.toJson(null));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		ReportResponse reportResponse = new ReportResponse();
		reportResponse = reportResponse.toObj(msg2.getBody(), null);
		
		System.out.println(reportResponse);
	}
}