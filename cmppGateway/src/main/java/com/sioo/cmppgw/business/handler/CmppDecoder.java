package com.sioo.cmppgw.business.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sioo.cmppgw.entity.ActiveTest;
import com.sioo.cmppgw.entity.CMPPConstant;
import com.sioo.cmppgw.entity.CmppHead;
import com.sioo.cmppgw.entity.Connect;
import com.sioo.cmppgw.entity.Constants;
import com.sioo.cmppgw.entity.DeliverResp;
import com.sioo.cmppgw.entity.Submit;

/**
 * 
 * @author leeson 2014年8月22日 上午9:17:38 li_mr_ceo@163.com <br>
 *
 */
@SuppressWarnings("rawtypes")
public class CmppDecoder extends ReplayingDecoder {
    Logger logger = LoggerFactory.getLogger((CmppDecoder.class).getSimpleName());

    @SuppressWarnings("unchecked")
	@Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int totalLength = in.readInt();
        int commandId = in.readInt();
        if (!validateClient(ctx, commandId)) {
            logger.info("Clinet:【{}】 not Login,Closed!", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }
        logger.debug("totalLength:{}",totalLength);
        byte[] bytes = new byte[totalLength];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.putInt(totalLength);
        bb.putInt(commandId);
        bb.putInt(in.readInt());
        in.readBytes(bytes, 12, totalLength - 12);
        CmppHead head = null;
        switch (commandId) {
            case CMPPConstant.APP_SUBMIT:
                head = new Submit((Integer) ctx.channel().attr(Constants.PROTOCALTYPE_VERSION).get());
                break;
            case CMPPConstant.APP_ACTIVE_TEST:
                head = new ActiveTest();
                break;
            case CMPPConstant.CMPP_CONNECT:
                head = new Connect();
                break;
            case CMPPConstant.APP_DELIVER_RESP:
                head = new DeliverResp((Integer) ctx.channel().attr(Constants.PROTOCALTYPE_VERSION).get());
                break;
            default:
                logger.warn("Received unknown data，commandId：{},Connection Closed!", "0x" + Integer.toHexString(commandId));
                ctx.close();
        }
        if (null != head) {
            logger.debug("【Bytes Read complete:{}】",in.readerIndex()==in.writerIndex());
            head.setMsgBytes(bytes);
            out.add(head);
        }
    }

    /**
     * check client is login
     *
     * @param ctx
     * @param commandId
     * @return
     */
    @SuppressWarnings("unchecked")
	private boolean validateClient(ChannelHandlerContext ctx, int commandId) {
        return null != ctx.channel().attr(Constants.PROTOCALTYPE_VERSION).get() || commandId == CMPPConstant.CMPP_CONNECT;
    }
}