package com.ddk.smmp.channel.yuzhou_http.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnection {
	public static String doURL(String urlStr, String xml) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");

			connection.setDoOutput(true);
			connection.setDoInput(true);

			OutputStream outputStream = connection.getOutputStream();
			BufferedOutputStream bufferoutput = new BufferedOutputStream(outputStream);
			bufferoutput.write(xml.getBytes("utf-8"));
			bufferoutput.flush();
			bufferoutput.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String sCurrentLine = "";
			String str = null;
			while ((sCurrentLine = reader.readLine()) != null) {
				str = sCurrentLine;
			}
			reader.close();
			return str;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}