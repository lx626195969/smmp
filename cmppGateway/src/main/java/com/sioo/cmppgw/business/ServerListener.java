package com.sioo.cmppgw.business;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sioo.cmppgw.business.handler.CmppDecoder;
import com.sioo.cmppgw.business.handler.CmppEncoder;
import com.sioo.cmppgw.util.ConfigUtil;

/**
 * 
 * @author leeson 2014年8月22日 上午9:17:58 li_mr_ceo@163.com <br>
 *
 */
public class ServerListener {
    Logger logger = LoggerFactory.getLogger(ServerListener.class);
    
    private ConfigUtil configUtil;
    private ChannelHandler channelHandler;

    @SuppressWarnings("static-access")
    public void startListener(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(),new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 0, Integer.valueOf(configUtil.getConfig("idleSeconds"))));
                        ch.pipeline().addLast(new CmppDecoder());
                        ch.pipeline().addLast(new CmppEncoder());
                        ch.pipeline().addLast(channelHandler);
                    }
                });
        try {
            serverBootstrap.bind(Integer.parseInt(configUtil.getConfig("listenPort"))).sync();
            logger.info("Server Start Success, Listning on Port:【{}】", configUtil.getConfig("listenPort"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setConfigUtil(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}
