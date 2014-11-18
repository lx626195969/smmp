package com.ddk.smmp.log4j;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author leeson 2014年10月27日 下午3:34:10 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelLogFilter extends Filter {
	boolean acceptOnMatch = false;
	private int levelMin;
	private int levelMax;

	public boolean isAcceptOnMatch() {
		return acceptOnMatch;
	}

	public void setAcceptOnMatch(boolean acceptOnMatch) {
		this.acceptOnMatch = acceptOnMatch;
	}

	public int getLevelMin() {
		return levelMin;
	}

	public void setLevelMin(int levelMin) {
		this.levelMin = levelMin;
	}

	public int getLevelMax() {
		return levelMax;
	}

	public void setLevelMax(int levelMax) {
		this.levelMax = levelMax;
	}

	@Override
	public int decide(LoggingEvent event) {
		int inputLevel = event.getLevel().toInt();

		if (inputLevel >= levelMin && inputLevel <= levelMax) {
			return 0;
		}

		return -1;
	}
}