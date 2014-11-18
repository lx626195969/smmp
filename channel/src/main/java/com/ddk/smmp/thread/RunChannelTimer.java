package com.ddk.smmp.thread;

import java.util.Timer;

/**
 * @author leeson 2014年7月29日 上午10:35:40 li_mr_ceo@163.com <br>
 *
 */
public class RunChannelTimer {
	private Timer timer = null;

	public RunChannelTimer() {
		this.timer = new Timer(true);
	}

	public void start() {
		timer.schedule(new RunChannelTask(timer), 1000 * 10, 60 * 60 * 1000);
	}
	
	public void stop(){
		timer.cancel();
	}
}