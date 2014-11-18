package com.ddk.smmp.adapter.socket;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.ddk.smmp.adapter.Server;

/**
 * @author leeson 2014年7月7日 下午5:32:06 li_mr_ceo@163.com <br>
 * 
 */
public class SocketServer implements Server{
	IoAcceptor acceptor = null;
	InetSocketAddress socketAddres = null;
	
	public int status = 0;
	public int port = 7002;//default
	
	public SocketServer() {
		super();
	}

	public SocketServer(int port) {
		super();
		this.port = port;
	}

	public static void main(String[] args) {
		SocketServer server = new SocketServer();
		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() throws Exception {
		acceptor = new NioSocketAcceptor();
		socketAddres = new InetSocketAddress("127.0.0.1", port);
		
		TextLineCodecFactory lineCodec=new TextLineCodecFactory(Charset.forName("UTF-8")); 
		lineCodec.setDecoderMaxLineLength(1024*1024); //1M  
		lineCodec.setEncoderMaxLineLength(1024*1024); //1M
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(lineCodec));
		
		acceptor.setHandler(new SocketServerHanlder());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(socketAddres);
		
		this.status = RUN_STATUS;
	}

	@Override
	public void stop() throws Exception {
		if (null != acceptor) {
			acceptor.unbind(socketAddres);
			acceptor.getFilterChain().clear();
			acceptor.dispose();
			acceptor = null;
		}
		this.status = STOP_STATUS;
	}

	@Override
	public int status() {
		return status;
	}

	@Override
	public int port() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPort() {
		return port;
	}
}