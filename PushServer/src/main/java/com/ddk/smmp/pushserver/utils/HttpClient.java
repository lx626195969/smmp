package com.ddk.smmp.pushserver.utils;

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
import org.apache.log4j.Logger;

/**
 * @author leeson 2014年7月28日 下午2:10:08 li_mr_ceo@163.com <br>
 * 
 */
public class HttpClient {
	private static final Logger logger = Logger.getLogger(HttpClient.class);
	
	public Object get(String url, Map<String, String> param, String encode){
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		httpClient.getHttpConnectionManager().getParams().setBooleanParameter("http.tcp.nodelay", true);

		GetMethod method = new GetMethod(url + "?" + map2Url(param, encode));
		
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 2000);
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
			logger.error("HTTP Get Error:" + e.getMessage() + " URL:" + url + "?" + map2Url(param, encode), e.getCause());
		}
		return null;
	}

	public Object post(String url, Map<String, String> param, String encode){
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		httpClient.getHttpConnectionManager().getParams().setBooleanParameter("http.tcp.nodelay", true);
		
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 2000);
		
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
			logger.error("HTTP Post Error:" + e.getMessage() + " URL:" + url, e.getCause());
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