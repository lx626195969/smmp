package com.ddk.smmp.web;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

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
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		int bindPort = Integer.parseInt(bundle.getString("channel.server.bindport"));
		
		
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("bindPort", bindPort + "");
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
