package com.sioo.cmppgw.entity.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author leeson 2014年12月4日 下午5:22:24 li_mr_ceo@163.com <br>
 * 
 */
public class Client {
	public Student SendAndGet(String ip, int port, Student s) {
		EventLoopGroup group = new NioEventLoopGroup();
		Student ret = null;
		try {
			Bootstrap b = new Bootstrap();
			b.group(group);
			final ClientHandler chl = new ClientHandler();
			b.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
					pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
					pipeline.addLast("encode", new ObjectEncoder());
					pipeline.addLast("decode", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
					pipeline.addLast("handler", chl);
				}
			});
			ChannelFuture f = b.connect(ip, port).sync();
			f.channel().writeAndFlush(s);
			f.channel().closeFuture().sync();
			ret = chl.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		Student s = new Student("hello", 23);
		Student g = new Client().SendAndGet("localhost", 9988, s);
	}
}