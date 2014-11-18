package com.ddk.smmp.adapter.test;

import java.net.URLEncoder;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.ddk.smmp.adapter.http.entity.SubmitRequest;
import com.ddk.smmp.adapter.http.entity.SubmitResponse;

/**
 * @author leeson 2014年8月15日 上午9:18:31 li_mr_ceo@163.com <br>
 * 
 */
public class Main {
	public static void main(String[] args) {
		try {
			test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void test() throws Exception {
		String[] phones1 = new String[200];
		for(int i = 10001000; i <= 10001199; i ++){
			phones1[i-10001000] = "152" + i;
		}
		
		String[] phones2 = new String[2000];
		for(int i = 10001000; i <= 10002999; i ++){
			phones2[i-10001000] = "152" + i;
		}
		String key = "OfwcAPYjoH0DIsSdFP+DRw==";
		String userName = "leeson";
		String password = "qwe123!@#";
		String content = "大家下午好，今天下午6点在小会议室开会商讨上市方案，请提前安排好工作，准时参会。";
		Integer productId = 9;
		String sendTime = "";
		String expId = "";
		
		/*==========GET==========*/
		SubmitRequest request_get = new SubmitRequest(password, phones1, content, expId, productId, sendTime);
		String url = "http://192.168.0.114:7001/submit";
		url += "?";
		url += "userName=" + userName;
		url += "&";
		url += "body=" + URLEncoder.encode(request_get.toJson(key));
		ClientResource client_get = new ClientResource(url);
		Representation result_get = client_get.get(); // 调用get方法
		SubmitResponse response_get = new SubmitResponse();
		response_get = response_get.toObj(result_get.getText(), key);
		System.out.println(response_get);
		/*==========GET==========*/
		
		/*==========POST==========*/
		SubmitRequest request_post = new SubmitRequest(password, phones2, content, expId, productId, sendTime);
        ClientResource client_post = new ClientResource("http://192.168.0.114:7001/submit");
        Form form = new Form();
        form.add("userName", userName);
        form.add("body", request_post.toJson(key));
        Representation result_post =  client_post.post(form); // 调用post方法
		SubmitResponse response_post = new SubmitResponse();
		response_post = response_post.toObj(result_post.getText(), key);
		System.out.println(response_post);
		/*==========POST==========*/
	}
}