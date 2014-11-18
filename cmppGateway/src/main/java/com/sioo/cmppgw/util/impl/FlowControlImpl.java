package com.sioo.cmppgw.util.impl;

import java.util.concurrent.atomic.AtomicInteger;

import com.sioo.cmppgw.util.FlowControl;

/**
 * 
 * @author leeson 2014年8月22日 上午9:22:51 li_mr_ceo@163.com <br>
 *
 */
public class FlowControlImpl implements FlowControl {
	private int limitNum;// 流量限额
	private AtomicInteger actNum;// 实际计数器

	@SuppressWarnings("unused")
	private FlowControlImpl() {
		
	}

	public FlowControlImpl(int limitNum) {
		this.limitNum = limitNum;
		actNum = new AtomicInteger(limitNum);
	}

	@Override
	public boolean isOverFlow() {
		return actNum.decrementAndGet() < 0;
	}

	@Override
	public boolean isOverFlow(int checkNum) {
		return actNum.addAndGet(-checkNum) < 0;
	}

	@Override
	public void resetFlow() {
		actNum.set(limitNum);
	}

	@Override
	public void changeSpeed(int speed) {
		limitNum = speed;
	}

	@Override
	public Integer getSpeed() {
		return limitNum;
	}
}
