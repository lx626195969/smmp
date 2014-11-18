package com.ddk.smmp.adapter.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author leeson 2014年7月8日 上午10:23:10 li_mr_ceo@163.com <br>
 * 
 */
public final class Constants {
	/** TODO 临时使用 */
	public static final String KEY = DigestUtils.md5Hex("siookey");
	
	/*================SOCKET CONSTANT==================*/
	public static final int SOCKET_COMMAND_CONNECT = 1;
	public static final int SOCKET_COMMAND_SUBMIT = 2;
	public static final int SOCKET_COMMAND_DELIVER = 3;
	public static final int SOCKET_COMMAND_REPORT = 4;
	public static final int SOCKET_COMMAND_BALANCE = 5;
	
	public static final int SOCKET_COMMAND_CONNECT_RESP = 8001;
	public static final int SOCKET_COMMAND_SUBMIT_RESP = 8002;
	public static final int SOCKET_COMMAND_DELIVER_RESP = 8003;
	public static final int SOCKET_COMMAND_REPORT_RESP = 8004;
	public static final int SOCKET_COMMAND_BALANCE_RESP = 8005;
	
	public static final int INTERNAL_ERROR = -1;//内部错误
	
	public static final int CONNECT_OK = 0;//连接成功
	public static final int CONNECT_VALI_FAIL = 1;//验证失败
	
	/*================HTTP CONSTANT==================*/
	public static final int CODE_OK = 0;//成功
	public static final int CODE_AUTH_ERROR = -1;//认证错误
	public static final int CODE_PARAM_ERROR = -1;//参数错误
	public static final int CODE_SERVER_ERROR = -1;//服务器错误
	
	/*================WEBSERVICE CONSTANT==================*/
	
	public static final int WEBSERVICE_COMMAND_SUBMIT = 1;
	public static final int WEBSERVICE_COMMAND_DELIVER = 2;
	public static final int WEBSERVICE_COMMAND_REPORT = 3;
	public static final int WEBSERVICE_COMMAND_BALANCE = 4;
	
	public static final int WEBSERVICE_COMMAND_SUBMIT_RESP = 8001;
	public static final int WEBSERVICE_COMMAND_DELIVER_RESP = 8002;
	public static final int WEBSERVICE_COMMAND_REPORT_RESP = 8003;
	public static final int WEBSERVICE_COMMAND_BALANCE_RESP = 8004;
	
	public static final int WEBSERVICE_COMMAND_ERROR = 8000;
}