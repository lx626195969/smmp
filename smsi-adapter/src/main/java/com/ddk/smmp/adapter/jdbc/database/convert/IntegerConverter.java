package com.ddk.smmp.adapter.jdbc.database.convert;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 整型映射工具
 * 
 */
public class IntegerConverter implements ResultConverter<Integer> {
	@Override
	public Integer convert(ResultSet rs) throws SQLException {
		return rs.getInt(1);
	}
}
