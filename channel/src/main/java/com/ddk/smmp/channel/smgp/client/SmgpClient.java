package com.ddk.smmp.channel.smgp.client;

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
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014-6-10 上午11:29:51 li_mr_ceo@163.com <br>
 *         客户端
 */
public class SmgpClient implements Client {
	private static final long serialVersionUID = 7973902102974483442L;

	private static final Logger logger = Logger.getLogger(SmgpClient.class);
	
	private Channel channel = null;

	public SmgpClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	@Override
	public void start() {
		synchronized (channel) {
			DatabaseTransaction trans = new DatabaseTransaction(true);
			try {
				new DbService(trans).addChannelLog(channel.getId(), channel.getName(), "通道启动");
				trans.commit();
			} catch (Exception ex) {
				trans.rollback();
			} finally {
				trans.close();
			}
			
			//开线程启动通道
			new Thread(new Runnable() {
				@Override
				public void run() {
					// create tcp/ip connector
					final IoConnector connector = new NioSocketConnector();
					final InetSocketAddress defaultAddress = new InetSocketAddress(channel.getHost(), channel.getPort());
					try {
						connector.setConnectTimeoutMillis(3000);// set connect timeout
						connector.getSessionConfig().setReadBufferSize(10240);//设置缓冲区大小
						connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SmgpProtocolCodecFactory()));// 创建接受数据的过滤器
						connector.setDefaultRemoteAddress(defaultAddress);//设置默认访问地址  
						connector.setHandler(new SmgpClientIoHandler(channel));
						// 添加重连监听 
						connector.addListener(new IoListener(){
							@Override
							public void sessionDestroyed(IoSession session) throws Exception {
								logger.info("[smgp]监听到session关闭"); 
								
								for (;;) {
									if(channel.isReConnect()){
										logger.info("[smgp]开始重连");
										
										//更改状态为重连中
										DatabaseTransaction trans = new DatabaseTransaction(true);
										try {
											new DbService(trans).updateChannelStatus(channel.getId(), 3);
											trans.commit();
										} catch (Exception ex) {
											trans.rollback();
										} finally {
											trans.close();
										}
										
										try {
											channel.getSession().close(true);
										} catch (Exception e) {
											logger.info("[smgp]开始重连,强制关闭之前session");
										}
										
										DatabaseTransaction trans1 = new DatabaseTransaction(true);
										try {
											DbService dbService = new DbService(trans1);
											dbService.addChannelLog(channel.getId(), channel.getName(), "通道重连");
											trans1.commit();
										} catch (Exception ex) {
											trans1.rollback();
										} finally {
											trans1.close();
										}
										
						                try {
						                	Thread.sleep(55 * 1000);
						                    ConnectFuture future = connector.connect();  
						                    future.awaitUninterruptibly();// 等待连接创建成功
						                    session = future.getSession();// 获取会话 
						                    Thread.sleep(5000);
						                    if (session.isConnected() && null != session.getAttribute("isSend")) {
						                    	logger.info("[smgp]断线重连[" + defaultAddress.getHostName() + ":" + defaultAddress.getPort() + "]成功");  
						                        break;
						                    }
						                } catch (Exception ex) {
						                    logger.info("[smgp]重连服务器登录失败,60秒再连接一次:" + ex.getMessage());  
						                }
									}else{
										break;
									}
						        }
							}
						});
						ConnectFuture cf = connector.connect();// 连接到服务器
						cf.awaitUninterruptibly();// 等待连接创建成功
						cf.getSession().getCloseFuture().awaitUninterruptibly();
						connector.dispose();
					} catch (Exception e1) {
						connector.dispose();
					}
				}
			}).start();
		}
	}
	
	@Override
	public void stop() {
		synchronized (channel) {
			DatabaseTransaction trans = new DatabaseTransaction(true);
			try {
				DbService dbService = new DbService(trans);
				dbService.addChannelLog(channel.getId(), channel.getName(), "通道停止");
				dbService.updateChannelStatus(channel.getId(), 2);
				trans.commit();
			} catch (Exception ex) {
				trans.rollback();
			} finally {
				trans.close();
			}
			
			channel.setReConnect(false);
			
			if(null != channel.getSession() && channel.getSession().isConnected()){
				channel.getSession().close(true);
			}
			
			channel.setStatus(Channel.STOP_STATUS);
		}
	}
	
	@Override
	public Integer status(){
		synchronized (channel) {
			return channel.getStatus();
		}
	}

	@Override
	public Channel getChannel() {
		synchronized (channel) {
			return this.channel;
		}
	}
}