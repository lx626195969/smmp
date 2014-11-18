package com.ddk.smmp.web;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelAdapter;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;
import com.ddk.smmp.utils.PostKeyUtil;

/**
 * @author leeson 2014年7月9日 下午6:11:49 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelAciton extends ServerResource {
	private static final Logger logger = Logger.getLogger(ChannelAciton.class);
	
	private final String PARAM_ERROR = "Param Error";
	private final String SUCCESS = "success";


	/**
	 * 获取请求参数信息
	 * 
	 * @param representation
	 *            POST请求参数封装
	 * @return
	 */
	private Form getForm(Representation representation) {
		if (null == representation) {
			return getRequest().getResourceRef().getQueryAsForm();
		}
		return new Form(representation);
	}

	@Get
	public String get_() {
		try {
			Form form = getForm(null);
			Integer cid = Integer.parseInt(form.getFirstValue("cid"));
			Integer status = Integer.parseInt(form.getFirstValue("status"));
			
			Long seed = Long.parseLong(form.getFirstValue("seed"));
			String key = form.getFirstValue("key");
			
			if(null == cid || null == status || null == seed || StringUtils.isEmpty(key) || (null != status && status != 0 && status != 1)){
				return PARAM_ERROR;
			}
			
			if(PostKeyUtil.isEquals(seed, key)){
				Channel channel = null;
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService service = new DbService(trans);
					channel = service.getChannel(cid);
					trans.commit();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex.getCause());
					trans.rollback();
				} finally {
					trans.close();
				}
				
				if(null == channel){
					return PARAM_ERROR;
				}
				
				//启动
				if(status == 1){
					ChannelAdapter.getInstance().start(channel);
				}
				
				//停止
				if(status == 0){
					ChannelAdapter.getInstance().stop(cid);
				}
				
				return SUCCESS;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
		}
		return PARAM_ERROR;
	}
}