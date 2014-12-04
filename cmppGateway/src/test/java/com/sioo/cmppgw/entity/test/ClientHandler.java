package com.sioo.cmppgw.entity.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author leeson 2014年12月4日 下午5:25:52 li_mr_ceo@163.com <br>
 * 
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
	private Student student;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("client接收到服务器返回的消息");
		student = (Student) msg;
	}

	public Student getMessage() {
		return this.student;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("client exception is general");
	}
}