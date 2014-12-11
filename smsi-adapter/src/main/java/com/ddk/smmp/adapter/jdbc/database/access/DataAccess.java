package com.ddk.smmp.adapter.jdbc.database.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddk.smmp.adapter.jdbc.database.DataModel;
import com.ddk.smmp.adapter.jdbc.database.convert.ResultConverter;

/**
 * 数据存取类
 */
public abstract class DataAccess {

	/**
	 * 日志工具
	 */
	private static final Logger LOG = LoggerFactory.getLogger((DataAccess.class).getSimpleName());
	/**
	 * 数据库连接
	 */
	private Connection conn;

	/**
	 * @param conn
	 *            数据库连接
	 */
	protected DataAccess(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 插入数据
	 * 
	 * @param sql
	 * @param generatedKeysConverter
	 *            主键映射
	 * @param params
	 * @return 主键
	 * @throws DataAccessException
	 */
	protected <T> T insert(String sql, ResultConverter<T> generatedKeysConverter, Object... params) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			setParameters(pstmt, params);
			executeUpdate(pstmt);
			rs = pstmt.getGeneratedKeys();
			nextResult(rs);
			return convertResult(rs, generatedKeysConverter);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		} finally {
			if(rs != null) try{rs.close();}catch (Exception e2) {}
			if(pstmt != null) try{pstmt.close();}catch (Exception e2) {}
		}
	}
	
	/**
	 * 批量更新
	 * 
	 * @param sqls
	 * @return
	 */
	protected int[] batchUpdate(String[] sqls){
		Statement pstmt = null;
		try {
			pstmt = conn.createStatement();
			for(String sql : sqls){
				pstmt.addBatch(sql);
			}
			return pstmt.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		} finally {
			if(pstmt != null) try{pstmt.close();}catch (Exception e2) {}
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 * @param params
	 * @return 影响行数
	 * @throws DataAccessException
	 */
	protected int update(String sql, Object... params) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			pstmt = getPreparedStatement(sql, params);
			return executeUpdate(pstmt);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		} finally {
			if(pstmt != null) try{pstmt.close();}catch (Exception e2) {}
		}
	}

	/**
	 * 查询单个结果
	 * 
	 * @param <T>
	 * @param sql
	 * @param converter
	 * @param params
	 * @return
	 */
	protected <T> T queryForObject(String sql, ResultConverter<T> converter, Object... params) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = getPreparedStatement(sql);
			setParameters(pstmt, params);
			
			rs = pstmt.executeQuery();
			if (nextResult(rs)) {
				return convertResult(rs, converter);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		} finally {
			if(rs != null) try{rs.close();}catch (Exception e2) {}
			if(pstmt != null) try{pstmt.close();}catch (Exception e2) {}
		}
	}

	/**
	 * 查询结果列表
	 * 
	 * @param <T>
	 * @param sql
	 * @param converter
	 * @param params
	 * @return
	 */
	protected <T> List<T> queryForList(String sql, ResultConverter<T> converter, Object... params) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = getPreparedStatement(sql);
			setParameters(pstmt, params);
			
			rs = pstmt.executeQuery();
			List<T> list = new ArrayList<T>();
			while (nextResult(rs)) {
				list.add(convertResult(rs, converter));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		} finally {
			if(rs != null) try{rs.close();}catch (Exception e2) {}
			if(pstmt != null) try{pstmt.close();}catch (Exception e2) {}
		}
	}
	
	/**
	 * 查询结果列表和id串 by lesson extend 
	 * 
	 * @param <T>
	 * @param sql
	 * @param converter
	 * @param params
	 * @return
	 */
	protected <T extends DataModel> Result<T> queryForListEx(String sql, ResultConverter<T> converter, Object... params) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = getPreparedStatement(sql);
			setParameters(pstmt, params);
			
			rs = pstmt.executeQuery();
			Result<T> result = new Result<T>();
			while (nextResult(rs)) {
				T t = convertResult(rs, converter);
				result.list.add(t);
				if(StringUtils.isNotEmpty(result.idStr)){
					result.idStr += "," + t.getId();
				}else{
					result.idStr += t.getId();
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		} finally {
			if(rs != null) try{rs.close();}catch (Exception e2) {}
			if(pstmt != null) try{pstmt.close();}catch (Exception e2) {}
		}
	}
	
	/**
	 * 
	 * @author leeson 2014-6-12 上午11:45:08 li_mr_ceo@163.com <br>
	 * 
	 * @param <T>
	 */
	public class Result<T>{
		/** 结果集 */
		List<T> list = new ArrayList<T>();
		/** 结果集中的ID的串,逗号分割 */
		String idStr = "";
		
		public List<T> getList() {
			return list;
		}
		
		public void setList(List<T> list) {
			this.list = list;
		}
		
		public String getIdStr() {
			return idStr;
		}
		
		public void setIdStr(String idStr) {
			this.idStr = idStr;
		}
		
		public Result() {
			super();
		}
	}

	/**
	 * @param sql
	 *            SQL语句
	 * @return 预编译声明
	 */
	private PreparedStatement getPreparedStatement(String sql, Object... params) throws DataAccessException {
		PreparedStatement pstmt = getPreparedStatement(sql);
		setParameters(pstmt, params);
		return pstmt;
	}

	/**
	 * @param sql
	 *            SQL语句
	 * @return 预编译声明
	 */
	private PreparedStatement getPreparedStatement(String sql) throws DataAccessException {
		try {
			return conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	/**
	 * 为预编译声明传入参数
	 * 
	 * @param pstmt
	 *            预编译声明
	 * @param params
	 *            参数
	 * @throws DataAccessException
	 */
	private void setParameters(PreparedStatement pstmt, Object... params) throws DataAccessException {
		try {
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	/**
	 * 执行更新操作
	 * 
	 * @param pstmt
	 * @return 影响行数
	 * @throws DataAccessException
	 */
	private int executeUpdate(PreparedStatement pstmt) throws DataAccessException {
		try {
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	/**
	 * 移动到下一行记录
	 * 
	 * @param rs
	 *            结果集
	 * @return 是否有下一行记录
	 * @throws DataAccessException
	 */
	private boolean nextResult(ResultSet rs) throws DataAccessException {
		try {
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}
	
	/**
	 * 映射
	 * 
	 * @param rs
	 *            结果集
	 * @return 映射结果
	 * @throws DataAccessException
	 */
	private <T> T convertResult(ResultSet rs, ResultConverter<T> converter) throws DataAccessException {
		try {
			return converter.convert(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}
}