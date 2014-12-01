package com.ddk.smmp.thread;

import java.util.Timer;

/**
 * @author leeson 2014年10月31日 上午11:04:35 li_mr_ceo@163.com <br>
 */
public class AddSmsTimer {
	private Timer timer1 = null;
	private Timer timer2 = null;
	private Timer timer3 = null;
	private Timer timer4 = null;

	public AddSmsTimer() {
		this.timer1 = new Timer(true);
		this.timer2 = new Timer(true);
		this.timer3 = new Timer(true);
		this.timer4 = new Timer(true);
	}

	public void start() {
		timer1.schedule(new AddSmsTask(1), 5000, 1000);
		timer2.schedule(new AddSmsTask(2), 5000, 1000);
		timer3.schedule(new AddSmsTask(3), 5000, 1000);
		timer4.schedule(new AddSmsTask(4), 5000, 1000);
	}

	public void stop() {
		timer1.cancel();
		timer2.cancel();
		timer3.cancel();
		timer4.cancel();
	}
}