package com.ddk.smmp.channel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.ddk.smmp.channel.cmpp._2.helper.LongSMByte;
import com.ddk.smmp.channel.guanyi_http.utils.Tools;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.jdbc.database.DruidDatabaseConnectionPool;
import com.ddk.smmp.service.DbService;
import com.ddk.smmp.utils.DateUtils;

/**
 * @author leeson 2014年11月12日 上午10:05:10 li_mr_ceo@163.com <br>
 * 
 */
public class RepairMain {
	static List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
	static List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
	static List<DelivVo> delivVos = new LinkedList<DelivVo>();
	
	public static void main(String[] args) {
		DruidDatabaseConnectionPool.startup();
		
		String filePath = "C:\\Users\\leeson\\Desktop\\log\\guanyi.log";
        readTxtFile(filePath);
        
        DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService dbService = new DbService(trans);
			dbService.batchAddSubmit(submitVos);
			dbService.batchAddSubmitRsp(submitRspVos);
			dbService.batchAddDeliv(delivVos);
			trans.commit();
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
	}
	
	public static void readTxtFile(String filePath) {
		int seq = Tools.generateSeq();
		String content = "";
		Integer rid = null;
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					lineTxt = lineTxt.substring(15);
					
					if(lineTxt.startsWith("send")){
						seq = Tools.generateSeq();
						
						//send msg:OperID=00585;OperPass=linla;DesMobile=18818278929;Content=【肯薇居家日用专营店】亲，感谢您给了小店的好.评，小店已经将您加入VIP会员，下次购买更多优.惠等着您;
						String s1 = lineTxt.substring(lineTxt.indexOf("DesMobile="));
						String phone = s1.substring(s1.indexOf("DesMobile=") + 10, 21);
						content = s1.substring(s1.indexOf("Content=") + 8, s1.length() - 1);
						//System.out.println(phone + "#" + content);
						
						DatabaseTransaction trans = new DatabaseTransaction(true);
						try {
							rid = new DbService(trans).getIdByPhoneAndContent(phone, content);
							trans.commit();
						} catch (Exception ex) {
							trans.rollback();
						} finally {
							trans.close();
						}
						
						if(null != rid){
							submitVos.add(new SubmitVo(rid, seq, 7));
							System.out.println(rid + "#" + seq);
						}else{
							System.out.println("rid is null");
						}
						
						Thread.sleep(200);
					}else if(lineTxt.startsWith("recv")){
						//recv msg:03,E261140141112083727
						String msg = lineTxt.substring(lineTxt.indexOf("msg") + 4, lineTxt.indexOf(","));
						String msgId = lineTxt.substring(lineTxt.indexOf(",") + 2);
						//System.out.println(msg + "#" + msgId);
						String state = "MT:1:408";
						if(msg.equals("03")){
							state = "MT:0";
						}else{
							state = "MT:1:" + msg;
						}
						
						int n = LongSMByte.getLongByte(70, 0, content).size();
						if(n == 0){
							n = 1;
						}
						for(int i = 0;i < n;i++){
							submitRspVos.add(new SubmitRspVo(seq, rid, Long.parseLong(msgId), 7, state));
							System.out.println(rid + "#" + seq + "#" + Long.parseLong(msgId) + "#" + state);
						}
					}else if(lineTxt.startsWith("receive")){
						//receive report:msgId=261140141112083727;state=0;time=20141112083733;
						String msgId = lineTxt.substring(lineTxt.indexOf("msgId=") + 6, lineTxt.indexOf(";"));
						String state = lineTxt.substring(lineTxt.indexOf("state=") + 6, lineTxt.indexOf(";", lineTxt.indexOf("state=") + 6));
						String time = lineTxt.substring(lineTxt.indexOf("time=") + 5, lineTxt.lastIndexOf(";"));
						//System.out.println(msgId + "#" + state+ "#" + time);
						delivVos.add(new DelivVo(Long.parseLong(msgId), 7, state.equals("0") ? "DELIVRD" : "UNDELIV", DateUtils.dateToString(DateUtils.stringToDate(time, "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss")));
						
						System.out.println(Long.parseLong(msgId) + "#" + (state.equals("0") ? "DELIVRD" : "UNDELIV") + "#" + DateUtils.dateToString(DateUtils.stringToDate(time, "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
					}
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}
}
