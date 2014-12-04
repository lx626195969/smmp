package com.sioo.cmppgw.entity.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author leeson 2014年12月4日 下午5:28:55 li_mr_ceo@163.com <br>
 * 
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        Student s=(Student)msg;
        System.out.println("SERVER接收到消息");
        ctx.channel().writeAndFlush(new Student("world",23));
        ctx.close();
    }
 
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        System.out.println(">>>>>>>>");
    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        System.out.println("exception is general");
    }
}
