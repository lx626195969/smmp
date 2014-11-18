package com.sioo.cmppgw.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSONObject;

/**
 * @author leeson 2014年7月28日 下午2:10:08 li_mr_ceo@163.com <br>
 * 
 */
public class HttpClient {
	public Object get(String url){
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);

		GetMethod method = new GetMethod(url);

		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 2000);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

		try {
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());
				return new String(responseBody, "utf-8");
			}
		} catch (Exception e) {
			System.out.println("HTTP Get Error:" + e.getMessage() + " URL:" + url);
		}
		return null;
	}

	public Object post(String url, Map<String, String> param){
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
		
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 2000);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		if (null != param) {
			for (String key : param.keySet()) {
				method.setParameter(key, param.get(key));
			}
		}
		
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
				
				return response.toString();
			}
		} catch (Exception e) {
			System.out.println("HTTP Post Error:" + e.getMessage() + " URL:" + url);
		} finally {
			method.releaseConnection();
		}
		
		return null;
	}
	
	public static void main(String[] args) throws IOException,InterruptedException {
		for(int i = 2001; i < 2500; i++){
			final int x = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					String phone = "1521437" + x;
					
					JSONObject json = new JSONObject();
					json.put("phones", phone);
					json.put("contents", "你好，世界" + x);
					json.put("productid", 15);
					json.put("userid", 30);
					json.put("expid", "");
					json.put("sign", "希奥");
					json.put("timing_date", "");
					
					long seed = System.currentTimeMillis();
					json.put("seed", seed);
					json.put("key", PostKeyUtil.generateKey(seed));
					
					HttpClient client = new HttpClient();
					Map<String, String> param = new HashMap<String, String>();
					param.put("param", json.toJSONString());
					long sTime = System.currentTimeMillis();
			        System.out.print("result=" + client.post("http://192.168.0.114/manager.web/message/doISend", param));
			        long eTime = System.currentTimeMillis();
			        System.out.println(" " + (eTime - sTime));
				}
			}).start();
			
			Thread.sleep(30);
		}
		
		Thread.sleep(10000);
    }
}
