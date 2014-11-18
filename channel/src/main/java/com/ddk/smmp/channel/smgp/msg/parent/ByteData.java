package com.ddk.smmp.channel.smgp.msg.parent;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.ValueNotSetException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;

/**
 * 
 * @author leeson 2014-6-9 下午02:18:50 li_mr_ceo@163.com
 *
 */
public abstract class ByteData extends SmsObject {

	public abstract void setData(ByteBuffer buffer) throws MSGException;

	public abstract ByteBuffer getData() throws ValueNotSetException;
}
