package com.ddk.smmp.channel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
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
public class Repair2Main {
	static List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
	static List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
	static List<DelivVo> delivVos = new LinkedList<DelivVo>();
	
	public static void main(String[] args) {
		DruidDatabaseConnectionPool.startup();
		
		String filePath = "C:\\Users\\leeson\\Desktop\\log\\yuzhou.log";
        readTxtFile(filePath);
        
      DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService dbService = new DbService(trans);
			dbService.batchAddSubmit(submitVos);
			dbService.batchAddSubmitRsp(submitRspVos);
			dbService.batchAddDeliv(delivVos);
			System.out.println(">>>>>>>>>>>最后保存一次");
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
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					lineTxt = lineTxt.substring(15);
					
					if(lineTxt.startsWith("MtPacket")){
						seq = Tools.generateSeq();
						
						String rid = lineTxt.substring(lineTxt.indexOf("cpmid=") + 6, lineTxt.indexOf(",", lineTxt.indexOf("cpmid=")));
						content = lineTxt.substring(lineTxt.indexOf("msg=") + 4, lineTxt.indexOf(", signature="));
						
						if(null != rid){
							submitVos.add(new SubmitVo(Integer.parseInt(rid), seq, 11));
							System.out.println(rid + "#" + seq);
						}
					}else if(lineTxt.startsWith("MtResponse")){
						Integer rid = Integer.parseInt(lineTxt.substring(lineTxt.indexOf("cpmid=") + 6, lineTxt.indexOf(",", lineTxt.indexOf("cpmid="))));
						String result = lineTxt.substring(lineTxt.indexOf("result=") + 7, lineTxt.length() - 1);
						String state = "MT:1:408";
						if(result.equals("0")){
							state = "MT:0";
						}else{
							state = "MT:1:" + result;
						}
						
						int n = LongSMByte.getLongByte(70, 0, content).size();
						if(n == 0){
							n = 1;
						}
						for(int i = 0;i < n;i++){
							submitRspVos.add(new SubmitRspVo(seq, rid, Long.parseLong(rid + ""), 11, state));
							System.out.println(rid + "#" + seq + "#" + Long.parseLong(rid + "") + "#" + state);
						}
					}else if(lineTxt.startsWith("SYNCPacket")){
						Long msgId = Long.parseLong(lineTxt.substring(lineTxt.indexOf("cpmid=") + 6, lineTxt.indexOf(",", lineTxt.indexOf("cpmid="))));
						String type = lineTxt.substring(lineTxt.indexOf("type=") + 5, lineTxt.indexOf(",", lineTxt.indexOf("type=")));
						if(type.equals("4")){
							String state = lineTxt.substring(lineTxt.indexOf("msg=") + 4, lineTxt.indexOf(",", lineTxt.indexOf("msg=")));
							delivVos.add(new DelivVo(msgId, 11, state, DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")));
							
							System.out.println(msgId + "#" + state + "#" + DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
						
						}
					}
					
					if(submitVos.size() >= 1000){
				      DatabaseTransaction trans = new DatabaseTransaction(true);
						try {
							DbService dbService = new DbService(trans);
							dbService.batchAddSubmit(submitVos);
							trans.commit();
						} catch (Exception ex) {
							trans.rollback();
						} finally {
							trans.close();
						}
						System.out.println(">>>>>>>>>>>保存1000提交");
						submitVos = new LinkedList<SubmitVo>();
					}
					
					if(submitRspVos.size() >= 1000){
					      DatabaseTransaction trans = new DatabaseTransaction(true);
							try {
								DbService dbService = new DbService(trans);
								dbService.batchAddSubmitRsp(submitRspVos);
								trans.commit();
							} catch (Exception ex) {
								trans.rollback();
							} finally {
								trans.close();
							}
							
							System.out.println(">>>>>>>>>>>保存1000提交响应");
							submitRspVos = new LinkedList<SubmitRspVo>();
					}
					

					if(delivVos.size() >= 1000){
					      DatabaseTransaction trans = new DatabaseTransaction(true);
							try {
								DbService dbService = new DbService(trans);
								dbService.batchAddDeliv(delivVos);
								trans.commit();
							} catch (Exception ex) {
								trans.rollback();
							} finally {
								trans.close();
							}
							System.out.println(">>>>>>>>>>>保存1000报告");
							delivVos = new LinkedList<DelivVo>();
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
