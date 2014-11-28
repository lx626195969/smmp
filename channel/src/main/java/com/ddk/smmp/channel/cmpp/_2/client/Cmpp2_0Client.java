package com.ddk.smmp.channel.cmpp._2.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014-6-10 上午11:29:51 li_mr_ceo@163.com <br>
 *         客户端
 */
public class Cmpp2_0Client implements Client {
	private static final long serialVersionUID = 7973902102974483442L;

	private static final Logger logger = Logger.getLogger(Cmpp2_0Client.class);
	
	private Channel channel = null;

	public Cmpp2_0Client(Channel channel) {
		super();
		this.channel = channel;
		channel.setClient(this);
	}
	
	@Override
	public void start() {
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			new DbService(trans).addChannelLog(channel.getId(), channel.getName(), "通道启动");
			trans.commit();
		} catch (Exception ex) {
			ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
			trans.rollback();
		} finally {
			trans.close();
		}
		
		// create tcp/ip connector
		final IoConnector connector = new NioSocketConnector();
		final InetSocketAddress defaultAddress = new InetSocketAddress(channel.getHost(), channel.getPort());
			
		try {
			connector.setConnectTimeoutMillis(3000);// set connect timeout
			connector.getSessionConfig().setReadBufferSize(10240);//设置缓冲区大小
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CmppProtocolCodecFactory(channel.getId())));// 创建接受数据的过滤器
			connector.setDefaultRemoteAddress(defaultAddress);//设置默认访问地址  
			connector.setHandler(new CmppClientIoHandler(channel));//添加处理器
			// 添加重连监听 
			connector.addListener(new IoListener(){
				@Override
				public void sessionDestroyed(IoSession session) throws Exception {
					ChannelLog.log(logger, "监听到session关闭, 通道[" + channel.getName() + "-" + channel.getId() + "]", LevelUtils.getSucLevel(channel.getId()));
					
					//更改状态为重连中
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						new DbService(trans).updateChannelStatus(channel.getId(), 3);
						trans.commit();
					} catch (Exception ex) {
						ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
						trans.rollback();
					} finally {
						trans.close();
					}
					
					try {
						channel.getSession().close(true);
					} catch (Exception e) {
						ChannelLog.log(logger, "强制关闭之前session" + e.getMessage(), LevelUtils.getSucLevel(channel.getId()), e.getCause());
					}
					
					if(null != channel.getSession() && channel.getSession().isConnected()){
						channel.getSession().close(true);
					}
					
					connector.dispose();
				}
			});
			ConnectFuture cf = connector.connect();// 连接到服务器
			cf.awaitUninterruptibly();// 等待连接创建成功
			cf.getSession().getCloseFuture().awaitUninterruptibly();
		}catch (Exception e) {
			ChannelLog.log(logger, "监听到session关闭, 通道[" + channel.getName() + "-" + channel.getId() + "]", LevelUtils.getSucLevel(channel.getId()));
			
			//更改状态为重连中
			DatabaseTransaction trans1 = new DatabaseTransaction(true);
			try {
				new DbService(trans1).updateChannelStatus(channel.getId(), 3);
				trans1.commit();
			} catch (Exception ex) {
				ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
				trans1.rollback();
			} finally {
				trans1.close();
			}
			
			try {
				channel.getSession().close(true);
			} catch (Exception e1) {
				ChannelLog.log(logger, "强制关闭之前session" + e1.getMessage(), LevelUtils.getSucLevel(channel.getId()), e1.getCause());
			}
			
			if(null != channel.getSession() && channel.getSession().isConnected()){
				channel.getSession().close(true);
			}
			
			connector.dispose();
		}
	}
}