package com.ddk.smmp.channel.smgp.msg.parent;


public abstract class Request extends SmgpMSG {
	public Request() {

	}

	public Request(int commandId) {
		super(commandId);
	}

	/**
	 * 创建响应消息体
	 * 
	 * @return
	 */
	protected abstract Response createResponse();

	public Response getResponse() {
		Response response = createResponse();
		response.setSequenceNumber(getSequenceNumber());
		response.setOriginalRequest(this);
		return response;
	}

	/**
	 * 创建响应消息体 并且获取消息类型 commandId
	 * 
	 * @return
	 */
	public int getResponseCommandId() {
		Response response = createResponse();
		return response.getCommandId();
	}

	/**
	 * Returns true. If the derived class cannot respond, then it must overwrite
	 * this function to return false.
	 * 
	 * @see PDU#canResponse()
	 */
	public boolean canResponse() {
		return true;
	}

	public boolean isRequest() {
		return true;
	}

	public boolean isResponse() {
		return false;
	}
}