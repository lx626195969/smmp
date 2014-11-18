package com.sioo.cmppgw.entity;

import io.netty.util.AttributeKey;

/**
 * 
 * @author leeson 2014年8月22日 上午9:19:23 li_mr_ceo@163.com <br>
 *
 */
public class Constants {
	public static Integer PROTOCALTYPE_VERSION_CMPP2 = 32;
	public static Integer PROTOCALTYPE_VERSION_CMPP3 = 48;
	@SuppressWarnings("rawtypes")
	public static AttributeKey PROTOCALTYPE_VERSION = new AttributeKey("protocalType");
	@SuppressWarnings("rawtypes")
	public static AttributeKey CURRENT_USER = new AttributeKey("currentUser");
}