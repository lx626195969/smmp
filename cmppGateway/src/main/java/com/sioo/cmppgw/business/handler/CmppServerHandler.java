package com.sioo.cmppgw.business.handler;

import static com.sioo.cmppgw.util.ConfigUtil.USER_CACHE_KEY;
import static com.sioo.cmppgw.util.ConfigUtil.USER_SESSION_KEY;
import static java.lang.System.arraycopy;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.primitives.Bytes;
import com.sioo.cmppgw.dao.RecordMode;
import com.sioo.cmppgw.dao.UserMode;
import com.sioo.cmppgw.entity.ActiveTest;
import com.sioo.cmppgw.entity.ActiveTestResp;
import com.sioo.cmppgw.entity.CMPPConstant;
import com.sioo.cmppgw.entity.CmppHead;
import com.sioo.cmppgw.entity.Connect;
import com.sioo.cmppgw.entity.ConnectResp;
import com.sioo.cmppgw.entity.Constants;
import com.sioo.cmppgw.entity.DeliverResp;
import com.sioo.cmppgw.entity.Submit;
import com.sioo.cmppgw.entity.SubmitResp;
import com.sioo.cmppgw.jdbc.database.DatabaseTransaction;
import com.sioo.cmppgw.service.DbService;
import com.sioo.cmppgw.socket.SmsTransferClient;
import com.sioo.cmppgw.util.Base64;
import com.sioo.cmppgw.util.ByteUtil;
import com.sioo.cmppgw.util.CacheUtil;
import com.sioo.cmppgw.util.ConfigUtil;
import com.sioo.cmppgw.util.FlowControl;
import com.sioo.cmppgw.util.LongSM;
import com.sioo.cmppgw.util.LongSMCache;
import com.sioo.cmppgw.util.PostKeyUtil;

/**
 * 
 * @author leeson 2014年8月22日 上午9:17:48 li_mr_ceo@163.com <br>
 *
 */
@ChannelHandler.Sharable
public class CmppServerHandler extends ChannelDuplexHandler {
    Logger logger = LoggerFactory.getLogger(CmppServerHandler.class);
    
    public static Random random = new Random();
    public static DateFormat msgIdHeadFormat = new SimpleDateFormat("yyyyMMdd");
    public static AtomicInteger magIdTailCount = new AtomicInteger(0);
    
    @SuppressWarnings("unused")
	private FlowControl flowControl;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    
    public void setFlowControl(FlowControl flowControl) {
        this.flowControl = flowControl;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("" + "Cinet:【{}】 closed Connection!", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Handler 异常!,异常信息:{},连接关闭!", cause.getMessage());
        ctx.close();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Received Connection From:【{}】", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                CmppHead cmppMsg = (CmppHead) msg;
                cmppMsg.doDecode();
                switch (cmppMsg.getCommandId()) {
                    case CMPPConstant.APP_SUBMIT:
                        processSubmit(ctx, (Submit) cmppMsg);
                        break;
                    case CMPPConstant.APP_ACTIVE_TEST:
                        processActiveTest(ctx, (ActiveTest) cmppMsg);
                        break;
                    case CMPPConstant.CMPP_CONNECT:
                        processConnect(ctx, (Connect) cmppMsg);
                        break;
                    case CMPPConstant.APP_DELIVER_RESP:
                        processDeliverResp((DeliverResp) cmppMsg);
                        break;
                }
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("Client idle time too long, close clinet:【{}】", ctx.channel().remoteAddress());
        ctx.close();
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
		UserMode user = (UserMode)ctx.channel().attr(Constants.CURRENT_USER).get();//获取当前用户
		if(null != user){
			CacheUtil.remove(USER_SESSION_KEY, user.getId());
		}
		super.close(ctx, future);
	}

    private void processDeliverResp(DeliverResp deliverResp) {
        logger.debug("Received Deliver Resp:{}", deliverResp.toString());
    }

    @SuppressWarnings("unchecked")
	private void processConnect(ChannelHandlerContext ctx, Connect connect) {
        logger.debug("Received Connect:{}", connect.toString());
        if (connect.getVersion() != Constants.PROTOCALTYPE_VERSION_CMPP2 && connect.getVersion() != Constants.PROTOCALTYPE_VERSION_CMPP3) {
            logger.info("Unknown ProtocalVersion Close Clinet:【{}】", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }
        
        ConnectResp connectResp = new ConnectResp((int) connect.getVersion());
        connectResp.setSecquenceId(connect.getSecquenceId());
        connectResp.setVersion(connect.getVersion());
        //开始校验用户名、密码
        if(authClient(connect.getSourceAddr(), connect.getTimeStamp(), connect.getAuthenticatorSource(), ctx)){
        	ctx.channel().attr(Constants.PROTOCALTYPE_VERSION).set((int) connect.getVersion());
        	
        	connectResp.setStatus(0);
        	arraycopy(connect.getAuthenticatorSource(), 0, connectResp.getAuthenticatorIsmg(), 0, 16);
        	connectResp.doEncode();
        	logger.debug("Send ConnectRsp:{}", connectResp.toString());
            ctx.writeAndFlush(connectResp);
        }else{
        	connectResp.setStatus(3);
        	connectResp.doEncode();
        	logger.debug("Send ConnectRsp:{}", connectResp.toString());
            ctx.writeAndFlush(connectResp);
            
            ctx.close();
        }
    }

    private void processActiveTest(ChannelHandlerContext ctx, ActiveTest activeTest) {
        logger.debug("Received heartbeats From:【{}】", ctx.channel().remoteAddress());
        ActiveTestResp resp = new ActiveTestResp();
        resp.setReserved((byte) 0);
        resp.setSecquenceId(activeTest.getSecquenceId());
        resp.doEncode();
        logger.debug("Send ActiveTestRsp:{}", resp.toString());
        ctx.writeAndFlush(resp);
    }

    @SuppressWarnings("unchecked")
	private void processSubmit(ChannelHandlerContext ctx, Submit submit) {
    	logger.debug("Received Submit:{}", submit.toString());
        
        UserMode user = (UserMode)ctx.channel().attr(Constants.CURRENT_USER).get();//获取当前用户
        
        SubmitResp resp = new SubmitResp((Integer) ctx.channel().attr(Constants.PROTOCALTYPE_VERSION).get());
        resp.setSecquenceId(submit.getSecquenceId());
        resp.setResult(9);//默认为9 其他错误
        
        int timeStr = Integer.valueOf(msgIdHeadFormat.format(Calendar.getInstance().getTime()));
        int tailCount = magIdTailCount.incrementAndGet();
        ByteBuffer.wrap(resp.getMsgId()).putInt(timeStr).putInt(tailCount);
        
        if(submit.getDestTerminalIds().length == submit.getFeeTerminalId().length){
        	String phone = new String(submit.getDestTerminalIds()).trim();
        	String srcId = new String(submit.getSrcId()).trim();
        	String content = "";
        	String expId = "";
        	//验证Src_Id是否非法
        	if(StringUtils.isNotEmpty(srcId) && !srcId.startsWith(user.getExpandCode())){
        		resp.setResult(23);//代表非法的src_id
        		resp.doEncode();
		        logger.debug("Send SubmitRsp:{}", resp.toString());
		        ctx.writeAndFlush(resp);
		        return;
        	}else{
        		expId = srcId.replace(user.getExpandCode(), "");
        	}
        	
        	int tpuUdhi = submit.getTpUdhi();
        	
        	if(tpuUdhi == 1){
        		//长短信
        		byte[] header = new byte[6];
        		byte[] msg = new byte[submit.getMsgContent().length - 6];
        		arraycopy(submit.getMsgContent(), 0, header, 0, 6);
        		arraycopy(submit.getMsgContent(), 6, msg, 0, submit.getMsgContent().length - 6);
        		
        		try {
					content = new String(msg, "UnicodeBigUnmarked");
					
					LongSM sm = LongSMCache.add(resp.getSecquenceId(), timeStr + "#" + tailCount, user.getId(), header[3], content, header[4], header[5] - 1);
					
					if(null != sm.getCompleteSM()){
						JSONObject jsonObject = submit(user, phone, sm.getCompleteSM(), expId);
						Integer code = jsonObject.getInteger("code");
						Integer rid = jsonObject.getInteger("rid");
						
						if(code == 0){
							resp.setResult(0);
						}else{
							resp.setResult(code + 10);//将错误响应码+10 防止和标准冲突
						}
						
						List<RecordMode> recordModes = new ArrayList<RecordMode>();
						
						String[] msgIds = sm.getMsgIds();
						int[] seqs = sm.getSecquences();
						
						for(int i = 0; i < msgIds.length; i++){
							SubmitResp submitResp = resp.clone();
							submitResp.setSecquenceId(seqs[i]);
							int timeStr_ = Integer.parseInt(msgIds[i].split("#")[0]);
							int tailCount_ = Integer.parseInt(msgIds[i].split("#")[1]);
							ByteBuffer.wrap(submitResp.getMsgId()).putInt(timeStr_).putInt(tailCount_);
							
							submitResp.doEncode();
							logger.debug("Send SubmitRsp:{}", submitResp.toString());
						    ctx.writeAndFlush(submitResp);
						    
						    if(code == 0){
						    	recordModes.add(new RecordMode(user.getId(), rid, msgIds[i], i + 1, msgIds.length, phone, srcId));
						    }
						}
						
						if(recordModes.size() > 0 && submit.getRegisteredDelivery() == 1){
							DatabaseTransaction trans = new DatabaseTransaction(true);
							try {
								//添加需要报告的短信到sms_reports表
								new DbService(trans).addRecord(recordModes);
								trans.commit();
							} catch (Exception ex) {
								trans.rollback();
							} finally {
								trans.close();
							}
					    }
					}
					
					return;//长短信组合完成后一起给响应
				} catch (Exception e) {
					
				}
        	}else{
        		//普通短信
        		try {
					content = new String(submit.getMsgContent(), "UnicodeBigUnmarked");
					
					JSONObject jsonObject = submit(user, phone, content, expId);
					Integer code = jsonObject.getInteger("code");
					Integer rid = jsonObject.getInteger("rid");
					
					List<RecordMode> recordModes = new ArrayList<RecordMode>();
					
					if(code == 0){
						resp.setResult(0);
						recordModes.add(new RecordMode(user.getId(), rid, timeStr + "#" + tailCount, 1, 1, phone, srcId));
						
						if(submit.getRegisteredDelivery() == 1){
							DatabaseTransaction trans = new DatabaseTransaction(true);
							try {
								//添加需要报告的短信到sms_reports表
								new DbService(trans).addRecord(recordModes);
								trans.commit();
							} catch (Exception ex) {
								trans.rollback();
							} finally {
								trans.close();
							}
						}
					}else{
						resp.setResult(code + 10);//将错误响应码+10 防止和标准冲突
					}
					
					resp.doEncode();
			        logger.debug("Send SubmitRsp:{}", resp.toString());
			        ctx.writeAndFlush(resp);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        }
    }

    /**
     * 合并byte数组
     * 
     * @param srcArrays
     * @return
     */
    public static byte[] byteMeger(List<byte[]> byteArrays) {
		int len = 0;
		for (byte[] srcArray : byteArrays) {
			len += srcArray.length;
		}

		byte[] destArray = new byte[len];
		int destLen = 0;
		for (byte[] srcArray : byteArrays) {
			System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
			destLen += srcArray.length;
		}
		return destArray;
	}
    
    /**
     * 对客户端进行认证
     * 
     * @param srcAddr 企业代码
     * @param timestamp 时间戳
     * @param clientAuthSrc 客户端生成的认证字节串 用于 和本地生成的做比较
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
	private static boolean authClient(byte[] srcAddr, byte[] timestamp, byte[] clientAuthSrc, ChannelHandlerContext ctx){
    	boolean state = false;
    	if(null != srcAddr && null != timestamp && null != clientAuthSrc){
    		try {
    	        UserMode userMode = CacheUtil.get(UserMode.class, USER_CACHE_KEY, new String(srcAddr));//从缓存中拿出用户信息
    	        if(null != userMode){
    	        	String timeTemp = "" + ByteUtil.getInt(timestamp, 0);
    				for (int i = 10 - timeTemp.length(); i > 0; i--)
    					timeTemp = "0" + timeTemp;
    				
    	        	byte[] finalBytes = Bytes.concat(srcAddr, new byte[9], Base64.decrypt(userMode.getPwd(), Base64.KEY).getBytes(), timeTemp.getBytes());
    	        	MessageDigest md5 = MessageDigest.getInstance("MD5");
    	        	byte[] result = md5.digest(finalBytes);
    	        	
					// 验证密码和绑定IP
					if (Arrays.equals(result, clientAuthSrc)) {
						if (StringUtils.isEmpty(userMode.getBindIp())
								|| (StringUtils
										.isNotEmpty(userMode.getBindIp()) && ((InetSocketAddress) ctx
										.channel().remoteAddress())
										.getHostName().equals(
												userMode.getBindIp()))) {
							ctx.channel().attr(Constants.CURRENT_USER).set(userMode);
							state = true;

							// 将用户Session加入缓存
							CacheUtil.put(USER_SESSION_KEY, userMode.getId(), ctx);
						}
					}
    	        }
    		} catch (Exception ex) {
    			
    		}
    	}
		
		return state;
    }
    
    /**
     * 调用接口获取提交结果
     * 
     * @param user
     * @param phone
     * @param content
     * @return
     */
	private JSONObject submit(UserMode user, String phone, String content, String expId){
		JSONObject json = new JSONObject();
		
		String sign = getSign(content);// 获取签名
		
		json.put("phones", phone);
		json.put("contents", content.replace("【" + sign + "】", ""));
		json.put("productid", user.getProductId());
		json.put("userid", user.getId());
		json.put("expid", expId);
		json.put("sign", sign);
		json.put("timing_date", "");
		
		long seed = System.currentTimeMillis();
		json.put("seed", seed);
		json.put("key", PostKeyUtil.generateKey(seed));
		
		SmsTransferClient client = new SmsTransferClient(ConfigUtil.getConfig("submit.hostname"), Integer.parseInt(ConfigUtil.getConfig("submit.port")));
		Object result = client.submit(json.toJSONString());
		client.close();
		
		logger.debug("submit result:" + result);
		
		JSONObject jsonObject = new JSONObject();
		if(null != result){
			JSONObject res = JSONObject.parseObject(result.toString());
			
			jsonObject.put("code", null == res.getInteger("code") ? -1 : res.getInteger("code"));
			jsonObject.put("rid", res.getInteger("rid"));
			return jsonObject;
		}else{
			jsonObject.put("code",  -1);
			return jsonObject;
		}
	}
	
    /**
     * 获取短信中的签名
     * 
     * @param content
     * @return
     */
    public static String getSign(String content) {
    	String sign = "";
	    Pattern pattern = Pattern.compile("(?<=\\【)[^\\】]+");
	    Matcher matcher = pattern.matcher(content);
	    while(matcher.find())
	    {
	    	sign = matcher.group();
	    }
	    return sign;
	}
}