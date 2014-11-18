package com.sioo.cmppgw.business;

import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

import com.sioo.cmppgw.util.ConfigUtil;

/**
 * 
 * @author leeson 2014年8月22日 上午9:17:53 li_mr_ceo@163.com <br>
 *
 */
public class MainStart {
	static {
		try {
			// 初始化log配置
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure(ConfigUtil.getRootDir() + "config/logback.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "resource", "unused" })
	public static void main(String[] args) {
		ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationcontex-init.xml");
	}
}