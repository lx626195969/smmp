package com.sioo.cmppgw.business.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.sioo.cmppgw.entity.CmppHead;

/**
 * 
 * @author leeson 2014年8月22日 上午9:17:43 li_mr_ceo@163.com <br>
 *
 */
public class CmppEncoder extends MessageToByteEncoder<CmppHead> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CmppHead msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getMsgBytes());
    }
}
