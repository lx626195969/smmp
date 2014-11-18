package com.ddk.smmp.channel.cmpp;

import com.ddk.smmp.model.SmQueue;

/**
 * @author leeson 2014年6月17日 下午12:17:20 li_mr_ceo@163.com <br>
 *         CMPP消息从提交到收到响应 缓存对象
 */
public class Message {
	private SmQueue queue;// 队列取出来的需要的消息结构
	private int messageId;//历史记录ID 更改提交状态时使用
	private int submit_seq;// 提交序列
	private int submit_seq_num = 0;// 提交序列数目
	private int response_seq_num = 0;// 响应序列数目
	private int num = 0;//消息条数
	
	/**
	 * 判断单条信息提交是否完成
	 * 
	 * @return
	 */
	public boolean isComplete() {
		if(num != 0 && submit_seq_num !=0 && response_seq_num != 0){
			return (submit_seq_num == response_seq_num) && (submit_seq_num == num) && (response_seq_num == num);
		}
		return false;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public SmQueue getQueue() {
		return queue;
	}

	public void setQueue(SmQueue queue) {
		this.queue = queue;
	}

	public int getSubmit_seq() {
		return submit_seq;
	}

	public void setSubmit_seq(int submit_seq) {
		this.submit_seq = submit_seq;
	}

	public int getSubmit_seq_num() {
		return submit_seq_num;
	}

	public void setSubmit_seq_num(int submit_seq_num) {
		this.submit_seq_num = submit_seq_num;
	}

	public int getResponse_seq_num() {
		return response_seq_num;
	}

	public void setResponse_seq_num(int response_seq_num) {
		this.response_seq_num = response_seq_num;
	}

	public Message(SmQueue queue, int submit_seq) {
		super();
		this.queue = queue;
		this.submit_seq = submit_seq;
	}
}