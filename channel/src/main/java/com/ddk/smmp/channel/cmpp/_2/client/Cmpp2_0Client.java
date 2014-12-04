package com.ddk.smmp.channel.cmpp._2.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.cmpp._2.handler.ActiveTestThread;
import com.ddk.smmp.channel.cmpp._2.handler.DeliverThread;
import com.ddk.smmp.channel.cmpp._2.handler.SubmitResponseThread;
import com.ddk.smmp.channel.cmpp._2.handler.SubmitThread;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014-6-10 上午11:29:51 li_mr_ceo@163.com <br>
 *         客户端
 */
public class Cmpp2_0Client extends Client {
	private static final long serialVersionUID = 7973902102974483442L;
	private static final Logger logger = Logger.getLogger(Cmpp2_0Client.class);
	
	public SubmitThread submitThread = null;
	public SubmitResponseThread submitResponseThread = null;
	public ActiveTestThread heartbeatThread = null;
	public DeliverThread deliverThread = null;
	
	public Cmpp2_0Client(Channel channel) {
		super();
		this.channel = channel;
		channel.setClient(this);
	}
	
	@Override
	public void start() {
		//通道启动
		ConstantUtils.updateChannelStatus(channel.getId(), 1);
		
		// create tcp/ip connector
		connector = new NioSocketConnector();
		final InetSocketAddress defaultAddress = new InetSocketAddress(channel.getHost(), channel.getPort());
		
		try {
			connector.setConnectTimeoutMillis(3000);// set connect timeout
			connector.getSessionConfig().setReadBufferSize(10240);//设置缓冲区大小
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CmppProtocolCodecFactory(channel.getId())));// 创建接受数据的过滤器
			connector.setDefaultRemoteAddress(defaultAddress);//设置默认访问地址  
			connector.setHandler(new CmppClientIoHandler(channel));//添加处理器
			ConnectFuture cf = connector.connect();// 连接到服务器
			cf.awaitUninterruptibly();// 等待连接创建成功
			cf.getSession().getCloseFuture().awaitUninterruptibly();
		}catch (Exception e) {
			ChannelLog.log(logger, "监听到session关闭, 通道[" + channel.getName() + "-" + channel.getId() + "]", LevelUtils.getSucLevel(channel.getId()));
			//更改状态为重连中
			ConstantUtils.updateChannelStatus(channel.getId(), 3);
			connector.dispose();
		}
	}
	
	@Override
	public void stop(){
		connector.dispose();
	}
}