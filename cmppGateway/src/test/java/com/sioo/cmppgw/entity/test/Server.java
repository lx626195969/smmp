package com.sioo.cmppgw.entity.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Server {
	private static final String IP = "localhost";
	private static final int PORT = 9988;
	protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
	protected static final int BIZTHREADSIZE = 4;
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

	protected void run() throws Exception {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_BACKLOG, 128);
            b.option(ChannelOption.TCP_NODELAY,true);
            b.childOption(ChannelOption.TCP_NODELAY,true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					ch.pipeline().addLast(new IdleStateHandler(0, 0, 120, TimeUnit.SECONDS));
					pipeline.addLast("encode", new ObjectEncoder());
					pipeline.addLast("decode", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                    ch.pipeline().addLast(new ServerHandler());
				}
			});
			ChannelFuture f = b.bind(IP, PORT).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("开始启动服务器...");
		new Server().run();
		// HelloServer.shutdown();
	}
}