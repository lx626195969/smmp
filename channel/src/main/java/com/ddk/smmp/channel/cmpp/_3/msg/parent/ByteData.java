package com.ddk.smmp.channel.cmpp._3.msg.parent;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.ValueNotSetException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;

/**
 * 
 * @author leeson 2014-6-9 下午02:18:50 li_mr_ceo@163.com
 *
 */
public abstract class ByteData extends SmsObject {

	public abstract void setData(ByteBuffer buffer) throws MSGException;

	public abstract ByteBuffer getData() throws ValueNotSetException;
}
