package com.ddk.smmp.channel.sgip.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.sgip.handler.SubmitResponseThread;
import com.ddk.smmp.channel.sgip.handler.SubmitThread;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014-6-10 上午11:29:51 li_mr_ceo@163.com <br>
 *         客户端
 */
public class SgipClient implements Client {
	private static final long serialVersionUID = 7973902102974483442L;

	private static final Logger logger = Logger.getLogger(SgipClient.class);
	
	private Channel channel = null;

	public SgipClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	SubmitThread submitThread = null;
	SubmitResponseThread submitResponseThread = null;
	SgipListener listener = null;
	
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
		
		//开线程启动通道
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//启动联通监听端口
					if(listener == null){
						listener = new SgipListener(channel);
						listener.start();
						ChannelCacheUtil.put("listener_" + channel.getId(), "sgipListener", listener);
						
						ChannelLog.log(logger, "启动联通端口监听处理程序......", LevelUtils.getSucLevel(channel.getId()));
					}
					
					//启动消息发送处理类
					if(submitThread == null){
						submitThread = new SubmitThread(channel);
						submitThread.start();
						ChannelCacheUtil.put("thread_" + channel.getId(), "submitThread", submitThread);
						
						ChannelLog.log(logger, "启动联通短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
					}
					
					//启动发送响应处理类
					if(submitResponseThread == null){
						submitResponseThread = new SubmitResponseThread(channel);
						submitResponseThread.start();
						ChannelCacheUtil.put("thread_" + channel.getId(), "submitResponseThread", submitResponseThread);
						ChannelLog.log(logger, "启动联通短信提交响应处理线程......", LevelUtils.getSucLevel(channel.getId()));
					}
				} catch (IOException e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
				} catch (Exception e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
				}
				
				// create tcp/ip connector
				final IoConnector connector = new NioSocketConnector();
				final InetSocketAddress defaultAddress = new InetSocketAddress(channel.getHost(), channel.getPort());
				try {
					connector.setConnectTimeoutMillis(3000);// set connect timeout
					connector.getSessionConfig().setReadBufferSize(10240);//设置缓冲区大小
					connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SgipProtocolCodecFactory(channel.getId())));// 创建接受数据的过滤器
					connector.setDefaultRemoteAddress(defaultAddress);//设置默认访问地址  
					connector.setHandler(new SgipClientIoHandler(channel, submitResponseThread));//添加处理器
					// 添加重连监听 
					connector.addListener(new IoListener(){
						@Override
						public void sessionDestroyed(IoSession session) throws Exception {
							ChannelLog.log(logger, "[sgip]监听到session关闭", LevelUtils.getSucLevel(channel.getId()));
							
							for (;;) {
								if(channel.isReConnect()){
									ChannelLog.log(logger, "[sgip]开始重连", LevelUtils.getSucLevel(channel.getId()));
									
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
										ChannelLog.log(logger, "[sgip]开始重连,强制关闭之前session" + e.getMessage(), LevelUtils.getSucLevel(channel.getId()), e.getCause());
									}
									
									DatabaseTransaction trans1 = new DatabaseTransaction(true);
									try {
										DbService dbService = new DbService(trans1);
										dbService.addChannelLog(channel.getId(), channel.getName(), "通道重连");
										trans1.commit();
									} catch (Exception ex) {
										ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
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
					                    	ChannelLog.log(logger, "[sgip]断线重连[" + defaultAddress.getHostName() + ":" + defaultAddress.getPort() + "]成功", LevelUtils.getSucLevel(channel.getId()));
					                        break;
					                    }
					                } catch (Exception ex) {
					                	ChannelLog.log(logger, "[sgip]重连服务器登录失败,60秒再连接一次:" + ex.getMessage(), LevelUtils.getSucLevel(channel.getId()), ex.getCause());
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
				} catch (Exception e) {
					ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
					connector.dispose();
					stopThread();
				}
			}
		}).start();
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
				ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
				trans.rollback();
			} finally {
				trans.close();
			}
			
			channel.setReConnect(false);
			
			if(null != channel.getSession() && channel.getSession().isConnected()){
				channel.getSession().close(true);
			}
			
			channel.setStatus(Channel.STOP_STATUS);
			
			stopThread();
		}
	}
	
	/**
	 * 停止所有子线程
	 */
	private void stopThread(){
		//关闭线程
		if(null != submitThread){
			submitThread.stop_();
			ChannelLog.log(logger, "停止联通短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		if(null != submitResponseThread){
			submitResponseThread.stop_();
			ChannelLog.log(logger, "停止联通短信提交响应处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		//清除缓存
		ChannelCacheUtil.clear("thread_" + channel.getId());
		ChannelCacheUtil.clear("message_" + channel.getId());
		
		//关闭监听
		if(null != listener){
			listener.stop();
			ChannelLog.log(logger, "停止联通端口监听处理程序......", LevelUtils.getSucLevel(channel.getId()));
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