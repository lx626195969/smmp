package com.ddk.smmp.client.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * @author leeson 2014年7月28日 下午2:10:08 li_mr_ceo@163.com <br>
 * 
 */
public class HttpClient {
	public Object get(String url){
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(1000);

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

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(1000);
		
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
}
