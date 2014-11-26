package com.ddk.smmp.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

/**
 * @author leeson 2014年7月28日 下午2:10:08 li_mr_ceo@163.com <br>
 * 
 */
public class HttpClient {
	public Object get(String url, Map<String, String> param, String encode){
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
		httpClient.getHttpConnectionManager().getParams().setBooleanParameter("http.tcp.nodelay", true);

		System.out.println(url + "?" + map2Url(param, encode));
		GetMethod method = new GetMethod(url + "?" + map2Url(param, encode));
		
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		if(StringUtils.isNotEmpty(encode)){
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encode);
		}

		try {
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());
				return new String(responseBody, encode);
			}
		} catch (Exception e) {
			System.out.println("HTTP Get Error:" + e.getMessage() + " URL:" + url + "?" + map2Url(param, encode));
		}
		return null;
	}

	public Object post(String url, Map<String, String> param, String encode){
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
		httpClient.getHttpConnectionManager().getParams().setBooleanParameter("http.tcp.nodelay", true);
		
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		
		if(StringUtils.isNotEmpty(encode)){
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encode);
		}
		
		if (null != param) {
			for (String key : param.keySet()) {
				method.setParameter(key, param.get(key));
			}
		}
		
		StringBuffer response = new StringBuffer(); 
		try {
			httpClient.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), Charset.forName(encode)));
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
	
	/**
	 * MAP转URL参数部分
	 * 
	 * @param param
	 * @return
	 */
	private String map2Url(Map<String, String> param, String encode){
		StringBuffer buffer = new StringBuffer();
		
		int length = param.keySet().size();
		int initIndex = 0;
		try {
			for(String key : param.keySet()){
				buffer.append(key).append("=").append(URLEncoder.encode(param.get(key), encode));
				initIndex++;
				if(initIndex != length){
					buffer.append("&");
				}
			}
			
			return buffer.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}