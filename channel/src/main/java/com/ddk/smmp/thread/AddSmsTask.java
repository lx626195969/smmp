package com.ddk.smmp.thread;

import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014年7月29日 上午10:37:46 li_mr_ceo@163.com <br>
 *
 */
public class AddSmsTask extends TimerTask {
	private static final Logger logger = Logger.getLogger(AddSmsTask.class);
	
	int index;
	
	public AddSmsTask(int index) {
		super();
		this.index = index;
	}

	@Override
	public void run() {
		if(!ConstantUtils.isPause_23_58()){
			if(index == 1){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<SubmitVo> submitVos = new LinkedList<SubmitVo>();
				
				int num = SmsCache.queue1.drainTo(submitVos, 1000);
				
				if(num > 0){
					long s = System.currentTimeMillis();
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						DbService service = new DbService(trans);
						service.batchAddSubmit(submitVos);//保存提交消息
						//service.batchDelQueue(channel.getId(), idStringBuffer.toString());//批量删除队列表记录
						trans.commit();
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						trans.rollback();
					} finally {
						trans.close();
					}
					
					long e = System.currentTimeMillis();
					logger.error("批量保存提交信息[" + num + "]条|耗时" + (e - s));
				}
			} else if(index == 2){
				List<SubmitRspVo> submitRspVos = new LinkedList<SubmitRspVo>();
				
				int num = SmsCache.queue2.drainTo(submitRspVos, 1000);
				
				if(num > 0){
					long s = System.currentTimeMillis();
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						new DbService(trans).batchAddSubmitRsp(submitRspVos);
						trans.commit();
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						trans.rollback();
					} finally {
						trans.close();
					}
					
					long e = System.currentTimeMillis();
					logger.error("批量保存提交响应[" + num + "]条|耗时" + (e - s));
				}
			} else if(index == 3){
				List<DelivVo> delivVos = new LinkedList<DelivVo>();
				
				int num = SmsCache.queue3.drainTo(delivVos, 1000);
				
				if(num > 0){
					long s = System.currentTimeMillis();
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						new DbService(trans).batchAddDeliv(delivVos);
						trans.commit();
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						trans.rollback();
					} finally {
						trans.close();
					}
					
					long e = System.currentTimeMillis();
					logger.error("批量保存提交报告[" + num + "]条|耗时" + (e - s));
				}
			}else if(index == 4){
				List<MtVo> mtVos = new LinkedList<MtVo>();
				
				int num = SmsCache.queue4.drainTo(mtVos, 1000);
				
				if(num > 0){
					long s = System.currentTimeMillis();
					
					DatabaseTransaction trans = new DatabaseTransaction(true);
					try {
						new DbService(trans).batchAddMt(mtVos);
						trans.commit();
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						trans.rollback();
					} finally {
						trans.close();
					}
					
					long e = System.currentTimeMillis();
					logger.error("批量保存上行消息[" + num + "]条|耗时" + (e - s));
				}
			}
		}
	}
}