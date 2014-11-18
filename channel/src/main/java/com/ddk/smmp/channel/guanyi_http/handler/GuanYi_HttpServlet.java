package com.ddk.smmp.channel.guanyi_http.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;
import com.ddk.smmp.thread.SmsCache;

/**
 * @author leeson 2014年10月27日 上午9:29:53 li_mr_ceo@163.com <br>
 * 
 */
public class GuanYi_HttpServlet extends HttpServlet {
	private static final long serialVersionUID = 1004028325830573310L;
	
	private static final Logger logger = Logger.getLogger(GuanYi_HttpServlet.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private Channel channel = null;
	
	public GuanYi_HttpServlet(Channel channel) {
		super();
		this.channel = channel;
	}
	
	public GuanYi_HttpServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
        out.println("<title>Tips</title>");
        out.println("<body>");
        out.println("<h1>not support get request</h1>");
        out.println("</body>");
        out.println("</html>");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		
		String uri = request.getRequestURI();
		if(StringUtils.isNotEmpty(uri) && uri.equals("/deliverMessage")){
			String message = request.getParameter("args");
			if(StringUtils.isNotEmpty(message)){ 
				//上行消息
				if(message.startsWith("0")){
					String[] messageArray = message.split(";");
					for(String msg : messageArray){
						String[] msgArray = msg.split(",");
						
						Date date = new Date();
						try {
							date = sdf.parse(msgArray[4]);
						} catch (ParseException e1) {
							ChannelLog.log(logger, e1.getMessage(), LevelUtils.getErrLevel(channel.getId()));
						}
						String time = sdf.format(date);
						String content = "解析错误";
						try {
							content = URLDecoder.decode(msgArray[3], "GBK");
						} catch (UnsupportedEncodingException e) {
							ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()));
						}
						
						if(null != channel){
							DatabaseTransaction trans = new DatabaseTransaction(true);
							try {
								new DbService(trans).process_http_Mo(channel.getId(), msgArray[1], content, channel.getAccount() + "#" + msgArray[2]);
								trans.commit();
							} catch (Exception ex) {
								ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()));
								trans.rollback();
							} finally {
								trans.close();
							}
							
							ChannelLog.log(logger, "receive deliver:phone="
									+ msgArray[1] + ";expid=" + msgArray[2]
									+ ";content=" + content + ";time=" + time
									+ ";",
									LevelUtils.getSucLevel(channel.getId()));
						}
					}
				}else if
				//报告消息
				(message.startsWith("2")){
					List<DelivVo> delivVos = new LinkedList<DelivVo>();
					
					String[] messageArray = message.split(";");
					for(String msg : messageArray){
						String[] msgArray = msg.split(",");
						
						Long msgId = Long.parseLong(msgArray[3].substring(1));
						String state = msgArray[2].equals("0") ? "DELIVRD" : "UNDELIV";
						
						Date date = new Date();
						try {
							date = sdf.parse(msgArray[4]);
						} catch (ParseException e1) {
							ChannelLog.log(logger, e1.getMessage(), LevelUtils.getErrLevel(channel.getId()));
						}
						String time = sdf.format(date);
						
						if(null != channel){
							delivVos.add(new DelivVo(msgId, channel.getId(), state, time));
							ChannelLog.log(logger, "receive report:msgId="
									+ msgId + ";state=" + msgArray[2]
									+ ";time=" + time + ";",
									LevelUtils.getSucLevel(channel.getId()));
						}
					}
					
					if(delivVos.size() > 0){
						SmsCache.queue3.addAll(delivVos);
					}
				}
				
				out.println("0");
			}else{
				out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		        out.println("<head>");
		        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
		        out.println("<title>Tips</title>");
		        out.println("<body>");
		        out.println("<h1>parameter is error</h1>");
		        out.println("</body>");
		        out.println("</html>");
			}
		}else{
	        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
	        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	        out.println("<head>");
	        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
	        out.println("<title>Tips</title>");
	        out.println("<body>");
	        out.println("<h1>not support uri [" + uri + "]</h1>");
	        out.println("</body>");
	        out.println("</html>");
		}
	}
}