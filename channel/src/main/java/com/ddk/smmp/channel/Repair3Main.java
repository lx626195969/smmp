package com.ddk.smmp.channel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
public class Repair3Main {
	static List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
	static List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
	static List<DelivVo> delivVos = new LinkedList<DelivVo>();
	
	public static void main(String[] args) {
		DruidDatabaseConnectionPool.startup();
		
		String filePath = "C:\\Users\\leeson\\Desktop\\src.txt";
        readTxtFile(filePath);
	}
	
	public static void readTxtFile(String filePath) {
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if(StringUtils.isNotEmpty(lineTxt)){
//				      DatabaseTransaction trans = new DatabaseTransaction(true);
//						try {
//							DbService dbService = new DbService(trans);
//							dbService.batchAddSubmit(submitVos);
//							dbService.batchAddSubmitRsp(submitRspVos);
//							dbService.batchAddDeliv(delivVos);
//							trans.commit();
//						} catch (Exception ex) {
//							trans.rollback();
//						} finally {
//							trans.close();
//						}
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
