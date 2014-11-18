package com.ddk.smmp.adapter.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leeson 2014年7月8日 上午9:18:20 li_mr_ceo@163.com <br>
 * 
 */
public class SocketClient {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
	
	int PORT = 7002;
	IoConnector connector = null;
	InetSocketAddress socketAddres = null;

	public static void main(String[] args) {
		SocketClient client = new SocketClient();
		try {
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException {
		connector = new NioSocketConnector();
		socketAddres = new InetSocketAddress("192.168.0.135", PORT);
		//connector.getFilterChain().addLast("logger", new LoggingFilter());
		
		TextLineCodecFactory lineCodec=new TextLineCodecFactory(Charset.forName("UTF-8")); 
		lineCodec.setDecoderMaxLineLength(1024*1024); //1M  
		lineCodec.setEncoderMaxLineLength(1024*1024); //1M
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(lineCodec));
		
		connector.setHandler(new SocketClientHanlder());
		try {
			ConnectFuture cf = connector.connect(socketAddres);
			cf.awaitUninterruptibly();
			cf.getSession().getCloseFuture().awaitUninterruptibly();
		} catch (Exception e) {
			connector.dispose();
		}
	}

	public void destroy() {
		if (null != connector) {
			connector.dispose();
			connector = null;
		}
	}
}
