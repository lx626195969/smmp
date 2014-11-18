package com.ddk.smmp.client.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

/**
 * @author leeson 2014年7月25日 上午10:26:48 li_mr_ceo@163.com <br>
 * 
 */
public class SOAPClient {

	public static void main(String[] args) {
		SOAPClient client = new SOAPClient();
		LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
		paramMap.put("IPAddress", "116.226.34.152");
		
		try {
			Object soap11Result = client.soap11("http://www.webservicex.net/geoipservice.asmx", "http://www.webservicex.net/GetGeoIP", "GetGeoIP", "http://www.webservicex.net/", paramMap);
			System.out.println(soap11Result);
			
			Object soap12Result = client.soap12("http://www.webservicex.net/geoipservice.asmx", "http://www.webservicex.net/GetGeoIP", "GetGeoIP", "http://www.webservicex.net/", paramMap);
			System.out.println(soap12Result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object soap11(String address, String soapAction, String operation, String nameSpace, LinkedHashMap<String, String> paramMap) throws Exception {
		String soapMsg = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				       + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					       + "<soap:Body>"
						       + "<${operation} xmlns=\"${namespace}\">"
						   		    + "${params}"
						       + "</${operation}>"
					       + "</soap:Body>"
				       + "</soap:Envelope>";

		soapMsg = soapMsg.replaceAll("\\$\\{operation\\}", operation);
		soapMsg = soapMsg.replaceAll("\\$\\{namespace\\}", nameSpace);

		StringBuffer buffer = new StringBuffer();
		for (String key : paramMap.keySet()) {
			buffer.append("<" + key + ">").append(paramMap.get(key)).append("</" + key + ">");
		}
		soapMsg = soapMsg.replaceAll("\\$\\{params\\}", buffer.toString());

		URL url = new URL(address);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Content-Length", String.valueOf(soapMsg.getBytes().length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", soapAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		out.write(soapMsg.getBytes());
		out.close();

		byte[] datas = readInputStream(httpConn.getInputStream());
		return new String(datas);
	}
	
	public Object soap12(String address, String soapAction, String operation, String nameSpace, LinkedHashMap<String, String> paramMap) throws Exception {
		String soapMsg = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				       + "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
					       + "<soap12:Body>"
						       + "<${operation} xmlns=\"${namespace}\">"
						   		    + "${params}"
						       + "</${operation}>"
					       + "</soap12:Body>"
				       + "</soap12:Envelope>";

		soapMsg = soapMsg.replaceAll("\\$\\{operation\\}", operation);
		soapMsg = soapMsg.replaceAll("\\$\\{namespace\\}", nameSpace);

		StringBuffer buffer = new StringBuffer();
		for (String key : paramMap.keySet()) {
			buffer.append("<" + key + ">").append(paramMap.get(key)).append("</" + key + ">");
		}
		soapMsg = soapMsg.replaceAll("\\$\\{params\\}", buffer.toString());

		URL url = new URL(address);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Content-Length", String.valueOf(soapMsg.getBytes().length));
		httpConn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
		//httpConn.setRequestProperty("SOAPAction", soapAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		out.write(soapMsg.getBytes());
		out.close();

		byte[] datas = readInputStream(httpConn.getInputStream());
		return new String(datas);
	}


	/**
	 * 解析响应消息
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}
}
