package com.ddk.smmp.pushserver.jdbc.database.convert;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 结果映射器
 * 
 */
public interface ResultConverter<T> {

	/**
	 * 映射
	 * 
	 * @param rs
	 *            结果集
	 * @return 映射结果
	 * @throws SQLException
	 */
	public T convert(ResultSet rs) throws SQLException;
}