package com.ddk.smmp.channel.gdydADC.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
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
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.MemCachedUtil;

/**
 * @author leeson 2014年10月27日 上午9:29:53 li_mr_ceo@163.com <br>
 * 
 */
public class DingYuan_HttpServlet extends HttpServlet {
	private static final long serialVersionUID = 1004028325830573310L;
	
	private static final Logger logger = Logger.getLogger(DingYuan_HttpServlet.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private Channel channel = null;
	
	public DingYuan_HttpServlet(Channel channel) {
		super();
		this.channel = channel;
	}
	
	public DingYuan_HttpServlet() {
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
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=GBK");
		response.setCharacterEncoding("GBK");
		
		PrintWriter out = response.getWriter();
		
		String uri = request.getRequestURI();
		if(StringUtils.isNotEmpty(uri) && uri.equals("/smsMsg")){
			String id = request.getParameter("id");
			if(StringUtils.isNotEmpty(id)){
				List<DelivVo> delivVos = new LinkedList<DelivVo>();
				
				//报告消息
				String st = request.getParameter("st");
				String mb = request.getParameter("mb");
				if(StringUtils.isNotEmpty(st) && StringUtils.isNotEmpty(mb)){
					String time = sdf.format(new Date());
					Long msgId = Long.parseLong(id);
					
					ChannelLog.log(logger, "receive report:msgId=" + msgId + ";state=" + st + ";time=" + time + ";", LevelUtils.getSucLevel(channel.getId()));
					
					Integer delivNum = MemCachedUtil.get(Integer.class, "deliv_cache", channel.getId() + "_" + msgId);
					if(delivNum == null){
						delivNum = 1;
					}
					
					for(int x = 0; x < delivNum; x++){
						delivVos.add(new DelivVo(msgId, channel.getId(), st, time));
					}
					
					SmsCache.queue3.addAll(delivVos);
					
					out.println("0");
				}else{
					out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
			        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			        out.println("<head>");
			        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=GBK\" />");
			        out.println("<title>Tips</title>");
			        out.println("<body>");
			        out.println("<h1>parameter is error</h1>");
			        out.println("</body>");
			        out.println("</html>");
				}
			}else{
				//上行消息
				String ms = request.getParameter("ms");
				String sc = request.getParameter("sc");
				String mb = request.getParameter("mb");
				
				if(StringUtils.isNotEmpty(ms) && StringUtils.isNotEmpty(mb) && StringUtils.isNotEmpty(sc)){
					String srcMsg = "";
					for(String one : request.getQueryString().split("&")){
						String[] twos = one.split("=");
						if(twos[0].equalsIgnoreCase("ms")){
							srcMsg = twos[1];
							break;
						}
					}
					
					String msg = URLDecoder.decode(srcMsg, "GBK");
					MtVo mtVo = new MtVo(2, channel.getId(), mb, msg, channel.getAccount() + "#" + sc);
					ChannelLog.log(logger, "receive deliver:phone=" + mb + ";expid=" + sc + ";content=" + msg + ";", LevelUtils.getSucLevel(channel.getId()));
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
	        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=GBK\" />");
	        out.println("<title>Tips</title>");
	        out.println("<body>");
	        out.println("<h1>not support uri [" + uri + "]</h1>");
	        out.println("</body>");
	        out.println("</html>");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}
}