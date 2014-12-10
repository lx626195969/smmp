package com.ddk.smmp.adapter.http;

import java.io.IOException;
import java.net.URLEncoder;

import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.ddk.smmp.adapter.http.entity.BalanceRequest;
import com.ddk.smmp.adapter.http.entity.BalanceResponse;
import com.ddk.smmp.adapter.http.entity.SubmitRequest;
import com.ddk.smmp.adapter.http.entity.SubmitResponse;

/**
 * @author leeson 2014年7月10日 上午11:32:17 li_mr_ceo@163.com <br>
 * 
 */
public class HttpClientTest {
	@SuppressWarnings("deprecation")
	@Ignore
	public void GET_Submit() throws IOException {
		String[] phones = new String[]{ "15214388400" };
		String password = "qwe123!@#";
		String content = "大家下午好，今天下午6点在小会议室开会商讨上市方案，请提前安排好工作，准时参会。";
		Integer productId = 4;
		String sendTime = "";
		String expId = "";
		
		SubmitRequest request = new SubmitRequest(password, phones, content, expId, productId, sendTime);
		
		String url = "http://210.5.152.50:7001/submit";
		url += "?";
		url += "userName=106989";
		url += "&";
		url += "body=" + URLEncoder.encode(request.toJson("29Sy052g7M8N/+gR9SQNiQ=="));

		ClientResource client = new ClientResource(url);
		Representation result = client.get(); // 调用get方法
		
		String back = result.getText();
		
		System.out.println(back);
		
		SubmitResponse response = new SubmitResponse();
		response = response.toObj(back, "29Sy052g7M8N/+gR9SQNiQ==");
		System.out.println(response);
	}
	
	@Test
    public void POST_Submit() throws IOException{
		String[] phones = new String[]{ "13166099479"};
		
		String password = "qwe123!@#";
		String content = "大家下午好，今天下午6点在小会议室开会商讨上市方案，请提前安排好工作，准时参会。【希奥股份】";
		Integer productId = 8;
		String sendTime = "";
		String expId = "";
		
		SubmitRequest request = new SubmitRequest(password, phones, content, expId, productId, sendTime);
		
        ClientResource client = new ClientResource("http://210.5.152.50:7001/submit");
        Form form = new Form();
        form.add("userName", "106980");
        form.add("body", request.toJson("GH1QQJsG2VJuuZXq8TsyMQ=="));
        Representation result =  client.post(form); // 调用post方法
        
        String back = result.getText();
		
		System.out.println(back);
		
		SubmitResponse response = new SubmitResponse();
		response = response.toObj(back, "GH1QQJsG2VJuuZXq8TsyMQ==");
		System.out.println(response);
    }
	
	@SuppressWarnings("deprecation")
	@Ignore
	public void GET_Balance() throws IOException {
		BalanceRequest request = new BalanceRequest("qwe123!@#");
		
		String url = "http://192.168.0.135:7001/balance";
		url += "?";
		url += "userName=leeson";
		url += "&";
		url += "body=" + URLEncoder.encode(request.toJson("OfwcAPYjoH0DIsSdFP+DRw=="));

		ClientResource client = new ClientResource(url);
		Representation result = client.get(); // 调用get方法
		
		String back = result.getText();
		
		System.out.println(back);
		
		BalanceResponse response = new BalanceResponse();
		response = response.toObj(back, "OfwcAPYjoH0DIsSdFP+DRw==");
		System.out.println(response);
	}
	
	@Ignore
    public void POST_Balance() throws IOException{
		BalanceRequest request = new BalanceRequest("qwe123!@#");
		
        ClientResource client = new ClientResource("http://192.168.0.135:7001/balance");
        Form form = new Form();
        form.add("userName", "leeson");
        form.add("body", request.toJson("OfwcAPYjoH0DIsSdFP+DRw=="));
        Representation result =  client.post(form); // 调用post方法
        
        String back = result.getText();
		
		System.out.println(back);
		
		BalanceResponse response = new BalanceResponse();
		response = response.toObj(back, "OfwcAPYjoH0DIsSdFP+DRw==");
		System.out.println(response);
    }
}
