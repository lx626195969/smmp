package com.ddk.smmp.adapter.web.action;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.ddk.smmp.adapter.Server;
import com.ddk.smmp.adapter.utils.CacheUtil;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

/**
 * @author leeson 2014年7月10日 上午9:25:20 li_mr_ceo@163.com <br>
 * 
 */
public class IndexAction extends ServerResource {
	@Get
	public Representation index() {
		Server http_server = CacheUtil.get(Server.class, BackStageAciton.SERVER_CACHE_GROUP_KEY, BackStageAciton.SERVER_CACHE_HTTP_KEY);
		Server socket_server = CacheUtil.get(Server.class, BackStageAciton.SERVER_CACHE_GROUP_KEY, BackStageAciton.SERVER_CACHE_SOCKET_KEY);
		Server webservice_server = CacheUtil.get(Server.class, BackStageAciton.SERVER_CACHE_GROUP_KEY, BackStageAciton.SERVER_CACHE_WEBSERVICE_KEY);
		
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("httpServer", http_server);
		dataModel.put("socketServer", socket_server);
		dataModel.put("webserviceServer", webservice_server);
		return new TemplateRepresentation("index.ftl", getConfig(), dataModel, MediaType.TEXT_HTML);
	}

	/**
	 * 获取freemaker模版位置
	 * 
	 * @return
	 */
	private Configuration getConfig() {
		Configuration config = new Configuration();
		ClassTemplateLoader classLoader = new ClassTemplateLoader(getClass(), "/template");
		TemplateLoader[] loaders = new TemplateLoader[] { classLoader };
		MultiTemplateLoader multiLoader = new MultiTemplateLoader(loaders);
		config.setTemplateLoader(multiLoader);
		return config;
	}
}
