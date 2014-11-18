package com.ddk.smmp.pushserver.jdbc.database.access;

/**
 * 数据库存取异常
 */
@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {

	public DataAccessException(Throwable cause) {
		super(cause);
	}

}
