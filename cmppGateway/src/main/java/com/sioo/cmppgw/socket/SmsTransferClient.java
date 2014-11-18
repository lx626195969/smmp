package com.sioo.cmppgw.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.sioo.cmppgw.util.PostKeyUtil;

/**
 * @author leeson 2014年9月10日 下午4:12:50 li_mr_ceo@163.com <br>
 * 
 */
public class SmsTransferClient {
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	private String hostname = "127.0.0.1";
	private int port = 10002;
	private IoConnector connector = null;
	
	public SmsTransferClient(String hostname, int port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}

	private IoConnector getIoConnector(){
		if(null == this.connector){
			IoConnector connector = new NioSocketConnector();
			connector.getSessionConfig().setReadBufferSize(4096);
			connector.getSessionConfig().setWriteTimeout(2000);
			connector.getSessionConfig().setWriterIdleTime(10000);
			connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
	        
	        TextLineCodecFactory lineFactory = new TextLineCodecFactory(Charset.forName("UTF-8"));  
	        lineFactory.setDecoderMaxLineLength(Integer.MAX_VALUE);  
	        lineFactory.setEncoderMaxLineLength(Integer.MAX_VALUE);
	        
	        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(lineFactory));
	        connector.setHandler(new SmsTransferClientHandler());
	        this.connector = connector;
		}
        return this.connector;
	}
	
	private ConnectFuture getConnectFuture(){
		return getIoConnector().connect(new InetSocketAddress(hostname, port));
	}
	
	public Object submit(String message){
		IoSession session;
		try {
			ConnectFuture future = getConnectFuture();
			future.awaitUninterruptibly();
			session = future.getSession();
			
			logger.info("S -> " + message);
			
			session.write(message);
			long sTime = System.currentTimeMillis();
			session.getCloseFuture().awaitUninterruptibly();
			long eTime = System.currentTimeMillis();
			
			logger.debug("time consuming:" + (eTime - sTime));
			
			return session.getAttribute("result");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
        
		return null;
	}
	
	public void close(){
		if(null != connector){
			connector.dispose(true);
		}
	}

	public static void main(String[] args) throws IOException,InterruptedException {
		for(int i = 0; i < 1; i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i = 2000; i < 2500; i++){
						final int x = i;
						
						String phone = "1521638" + x;
						SmsTransferClient client = new SmsTransferClient("127.0.0.1", 10002);
						
						JSONObject json = new JSONObject();
						json.put("phones", phone);
						json.put("contents", "你好世界" + x);
						json.put("productid", 15);
						json.put("userid", 30);
						json.put("expid", "");
						json.put("sign", "希奥");
						json.put("timing_date", "");
						
						long seed = System.currentTimeMillis();
						json.put("seed", seed);
						json.put("key", PostKeyUtil.generateKey(seed));
						
				        System.out.println("result=" + client.submit(json.toJSONString()));
						client.close();
					}
				}
			}).start();
		}
    }
}