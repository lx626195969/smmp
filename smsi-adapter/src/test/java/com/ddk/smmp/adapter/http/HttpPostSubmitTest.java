package com.ddk.smmp.adapter.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSONObject;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.client.http.HttpClient;

/**
 * @author leeson 2014年7月25日 下午5:26:02 li_mr_ceo@163.com <br>
 *
 */
public class HttpPostSubmitTest {

	public static void main(String[] args) {
		String url = "http://210.5.152.50:7001/submit";
		String userName = "106980";
		String passWord = "qwe123!@#";
		int productID = 8;
		String key = "GH1QQJsG2VJuuZXq8TsyMQ==";
		
		JSONObject json = new JSONObject();
		json.put("content", "test " + new Date());
		json.put("passWord", passWord);
		json.put("expId", "");
		json.put("phones", new String[]{ "13166099479" });
		json.put("productId", productID);
		json.put("sendTime", "");
		
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(1000);
		
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 2000);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		method.setParameter("userName", userName);
		method.setParameter("body", AesUtil.encrypt(json.toJSONString(), key));
		
		StringBuffer response = new StringBuffer(); 
		try {
			httpClient.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), Charset.forName("UTF-8")));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				
				System.out.println(response.toString());
				
				System.out.println(AesUtil.decrypt(response.toString(), key));
			}
		} catch (Exception e) {
			System.out.println("HTTP Post Error:" + e.getMessage() + " URL:" + url);
		} finally {
			method.releaseConnection();
		}
	}
}