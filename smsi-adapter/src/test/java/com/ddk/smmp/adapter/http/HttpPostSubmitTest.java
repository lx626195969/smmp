package com.ddk.smmp.adapter.http;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ddk.smmp.client.http.HttpClient;
import com.ddk.smmp.util.PostKeyUtil;

/**
 * @author leeson 2014年7月25日 下午5:26:02 li_mr_ceo@163.com <br>
 *
 */
public class HttpPostSubmitTest {

	public static void main(String[] args) {
		String url = "http://192.168.0.135:8080/backstage.web/queue/doSend";
		
		JSONObject json = new JSONObject();
		json.put("phones", "15214388466\n15214388467\n15214388468\n15214388469\n15214388470");
		json.put("contents", "大家下午好，今天下午6点在小会议室开会商讨上市方案，请提前安排好工作，准时参会。");
		json.put("productid", 9);
		json.put("userid", 28);
		json.put("sign", "晨曦科技");
		
		long seed = System.currentTimeMillis();
		json.put("seed", seed);
		json.put("key", PostKeyUtil.generateKey(seed));
		
		Map<String, String> param = new LinkedHashMap<String, String>();
		param.put("param", json.toJSONString());
		
		HttpClient client = new HttpClient();
		
		Object result = client.post(url, param);
		System.out.println(result);
	}
}