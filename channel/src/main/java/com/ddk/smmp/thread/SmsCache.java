package com.ddk.smmp.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ddk.smmp.dao.DelivVo;
import com.ddk.smmp.dao.MtVo;
import com.ddk.smmp.dao.SubmitRspVo;
import com.ddk.smmp.dao.SubmitVo;

/**
 * @author leeson 2014年10月31日 上午11:11:21 li_mr_ceo@163.com <br>
 * 
 */
public class SmsCache {
	public static BlockingQueue<SubmitVo> queue1 = new LinkedBlockingQueue<SubmitVo>();
	public static BlockingQueue<SubmitRspVo> queue2 = new LinkedBlockingQueue<SubmitRspVo>();
	public static BlockingQueue<DelivVo> queue3 = new LinkedBlockingQueue<DelivVo>();
	public static BlockingQueue<MtVo> queue4 = new LinkedBlockingQueue<MtVo>();
}