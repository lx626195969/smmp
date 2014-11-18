package com.ddk.smmp.channel.sgip.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.log4j.Logger;



import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.sgip.handler.DeliverThread;
import com.ddk.smmp.channel.sgip.handler.ReportThread;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014年6月27日 上午9:17:05 li_mr_ceo@163.com <br>
 *
 */
public class SgipListener extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(SgipListener.class);
	
	private static final int BUFFER_SIZE = 8192;
	
	private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
		public Thread newThread(final Runnable r) {
			return new Thread(null, r, "SgipListenerThread", 64 * 1024);
		}
	};
	
	public SocketAcceptor acceptor;
	public SocketConnector connector;
	private OrderedThreadPoolExecutor executor;
	private Channel channel;
	
	public SgipListener(Channel channel) throws IOException {
		this.channel = channel;
		
		executor = new OrderedThreadPoolExecutor(0, 1000, 60, TimeUnit.SECONDS, THREAD_FACTORY);
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setReceiveBufferSize(BUFFER_SIZE);
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(executor));
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SgipProtocolCodecFactory(channel.getId())));
	}

	public void start() throws Exception {
		final InetSocketAddress socketAddress = new InetSocketAddress("0.0.0.0", channel.getLocalPort());
		acceptor.setHandler(new SgipListenerIoHandler(channel));
		acceptor.bind(socketAddress);
	}

	public void stop(){
		DeliverThread dThread = ChannelCacheUtil.get(DeliverThread.class, "child_thread_" + channel.getId(), "deliverThread");
		if(null != dThread){
			dThread.stop_();
		}
		
		ReportThread rThread = ChannelCacheUtil.get(ReportThread.class, "child_thread_" + channel.getId(), "reportThread");
		if(null != rThread){
			rThread.stop_();
		}
		
		acceptor.dispose(true);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		if (!(cause instanceof IOException)) {
			ChannelLog.log(logger, "Exception: " + cause.getMessage(), LevelUtils.getErrLevel(channel.getId()), cause);
		} else {
			ChannelLog.log(logger, "I/O error: " + cause.getMessage(), LevelUtils.getErrLevel(channel.getId()), cause);
		}
		session.close(true);
	}
}
