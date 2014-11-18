package com.ddk.smmp.client.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 * @author leeson 2014年7月25日 上午9:37:34 li_mr_ceo@163.com <br>
 * 
 */
public class RestletClient {

	public static void main(String[] args) {
		RestletClient client = new RestletClient();
		String url = "http://www.webservicex.net/geoipservice.asmx/GetGeoIP?IPAddress=116.226.34.152";
		try {
			Object getResult = client.get(url);
			System.out.println(getResult);
			
			Map<String, String> param = new HashMap<String, String>();
			param.put("IPAddress", "116.226.34.152");
			Object postResult = client.post(url, param);
			System.out.println(postResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object get(String url) throws IOException {
		ClientResource client = new ClientResource(url);
		
		Representation representation = client.get();
		return representation.getText();
	}
	
	public Object post(String url, Map<String, String> param) throws IOException{
		ClientResource client = new ClientResource(url);
		
		Form form = new Form();
		for(String key : param.keySet()){
			form.add(key, param.get(key));
		}
		
		form.getWebRepresentation(CharacterSet.UTF_8);
		Representation representation = client.post(form.getWebRepresentation());
		return representation.getText();
	}
}
