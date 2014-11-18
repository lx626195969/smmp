package com.ddk.smmp.channel.cmpp._3.msg.parent;

/**
 * 
 * @author leeson 2014-6-9 下午02:42:36 li_mr_ceo@163.com
 * 
 */
public abstract class Response extends CmppMSG {

	/**
	 * 关联请求
	 */
	private Request originalRequest = null;

	public Response() {

	}

	public Response(int commandId) {
		super(commandId);
	}

	public boolean canResponse() {
		return false;
	}

	public boolean isRequest() {
		return false;
	}

	public boolean isResponse() {
		return true;
	}

	public void setOriginalRequest(Request originalRequest) {
		this.originalRequest = originalRequest;
	}

	public Request getOriginalRequest() {
		return originalRequest;
	}
}