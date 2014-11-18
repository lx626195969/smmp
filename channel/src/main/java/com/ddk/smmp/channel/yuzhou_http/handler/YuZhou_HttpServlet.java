package com.ddk.smmp.channel.yuzhou_http.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;
import com.ddk.smmp.thread.SmsCache;
import com.ddk.smmp.utils.JaxbUtils;

/**
 * @author leeson 2014年10月27日 上午9:29:53 li_mr_ceo@163.com <br>
 * 
 */
public class YuZhou_HttpServlet extends HttpServlet {
	private static final long serialVersionUID = 1004028325830573310L;
	
	private static final Logger logger = Logger.getLogger(YuZhou_HttpServlet.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Channel channel = null;
	
	public YuZhou_HttpServlet(Channel channel) {
		super();
		this.channel = channel;
	}
	
	public YuZhou_HttpServlet() {
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
		if(StringUtils.isNotEmpty(uri) && uri.equals("/sms")){
			InputStream is = request.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));   
	        StringBuilder sb = new StringBuilder();   
	        String line = null;   
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        
	        String xmlStr = sb.toString();
			
	        SYNCPacket syncPacket = JaxbUtils.converyToJavaBean(xmlStr, SYNCPacket.class);
	        if(null != syncPacket){
	        	ChannelLog.log(logger, "recv msg:" + syncPacket.toString(), LevelUtils.getSucLevel(channel.getId()));
	        	
	        	if(syncPacket.getType().intValue() == 1){
	        		//上行
	        		DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						new DbService(trans).process_http_Mo(channel.getId(), syncPacket.getMobile(), syncPacket.getMsg(), channel.getAccount() + "#" + syncPacket.getPort());
						trans.commit();
					} catch (Exception ex) {
						ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()));
						trans.rollback();
					} finally {
						trans.close();
					}
	        	}
	        	
	        	if(syncPacket.getType().intValue() == 4){
	        		//状态报告
	        		DelivVo delivVo = new DelivVo(syncPacket.getCpmid(), channel.getId(), syncPacket.getMsg(), sdf.format(new Date()));
	        		SmsCache.queue3.add(delivVo);
	        	}
	        	
	        	out.println(JaxbUtils.convertToXml(new SYNCResponse(syncPacket.getMid(), syncPacket.getCpmid(), "0"), "UTF-8"));
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