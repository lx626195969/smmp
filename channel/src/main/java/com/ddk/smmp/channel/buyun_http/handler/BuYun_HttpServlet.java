package com.ddk.smmp.channel.buyun_http.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;

/**
 * @author leeson 2014年10月27日 上午9:29:53 li_mr_ceo@163.com <br>
 * 
 */
public class BuYun_HttpServlet extends HttpServlet {
	private static final long serialVersionUID = 1004028325830573310L;
	
	private static final Logger logger = Logger.getLogger(BuYun_HttpServlet.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
	private Channel channel = null;
	
	public BuYun_HttpServlet(Channel channel) {
		super();
		this.channel = channel;
	}
	
	public BuYun_HttpServlet() {
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
		if(StringUtils.isNotEmpty(uri) && uri.equals("/smsMsg")){
			String status = request.getParameter("status");
			if(StringUtils.isNotEmpty(status)){
				//报告消息
				String msgid = request.getParameter("msgid");
				String reportTime = request.getParameter("reportTime");
				String mobile = request.getParameter("mobile");
				if(StringUtils.isNotEmpty(msgid) && StringUtils.isNotEmpty(reportTime) && StringUtils.isNotEmpty(mobile)){
					Date date = new Date();
					try {
						date = sdf.parse(reportTime);
					} catch (ParseException e1) {
						ChannelLog.log(logger, e1.getMessage(), LevelUtils.getErrLevel(channel.getId()));
					}
					String time = sdf.format(date);
					DelivVo delivVo = new DelivVo(Long.parseLong(msgid), channel.getId(), status, time);
					ChannelLog.log(logger, "receive report:msgId=" + msgid + ";state=" + status + ";time=" + time + ";", LevelUtils.getSucLevel(channel.getId()));
					SmsCache.queue3.add(delivVo);
					
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
				//上行消息
				String msg = request.getParameter("msg");
				String destcode = request.getParameter("destcode");
				String moTime = request.getParameter("moTime");
				String mobile = request.getParameter("mobile");
				
				if(StringUtils.isNotEmpty(msg) && StringUtils.isNotEmpty(destcode) && StringUtils.isNotEmpty(moTime) && StringUtils.isNotEmpty(mobile)){
					Date date = new Date();
					try {
						date = sdf.parse(moTime);
					} catch (ParseException e1) {
						ChannelLog.log(logger, e1.getMessage(), LevelUtils.getErrLevel(channel.getId()));
					}
					String time = sdf.format(date);
					MtVo mtVo = new MtVo(2, channel.getId(), mobile, msg, channel.getAccount() + "#" + destcode);
					ChannelLog.log(logger, "receive deliver:phone=" + mobile + ";expid=" + destcode + ";content=" + msg + ";time=" + time + ";", LevelUtils.getSucLevel(channel.getId()));
					SmsCache.queue4.add(mtVo);
					
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