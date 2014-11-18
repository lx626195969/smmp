package com.ddk.smmp.adapter.jdbc.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库事务
 * 
 */
public class DatabaseTransaction {

	/**
	 * 日志工具
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseTransaction.class);

	/**
	 * 数据库连接
	 */
	private Connection conn;

	/**
	 * 实例化一个默认连接的事务
	 */
	public DatabaseTransaction() {
		this(DruidDatabaseConnectionPool.getConnection());
	}

	/**
	 * 实例化一个默认连接的事务
	 * 
	 * @param isOpenTrans
	 *            是否打开事务
	 */
	public DatabaseTransaction(boolean isOpenTrans) throws DatabaseException {
		this(DruidDatabaseConnectionPool.getConnection(), isOpenTrans);
	}

	/**
	 * 实例化一个默认连接的事务
	 * 
	 * @param conn
	 *            数据库连接
	 */
	public DatabaseTransaction(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 实例化一个默认连接的事务
	 * 
	 * @param conn
	 *            数据库连接
	 * @param isOpenTrans
	 *            是否打开事务
	 */
	public DatabaseTransaction(Connection conn, boolean isOpenTrans) throws DatabaseException {
		this.conn = conn;
		setAutoCommit(!isOpenTrans);
	}

	/**
	 * @return 数据库连接
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * 设置是否自动提交
	 * 
	 * @param autoCommit
	 *            自动提交
	 * @throws DatabaseException
	 */
	private void setAutoCommit(boolean autoCommit) throws DatabaseException {
		try {
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}

	/**
	 * 开始事务
	 * 
	 * @throws DatabaseException
	 */
	public void begin() throws DatabaseException {
		setAutoCommit(false);
	}

	/**
	 * @return 是否打开事务
	 * @throws DatabaseException
	 */
	public boolean isBegin() throws DatabaseException {
		try {
			return !conn.getAutoCommit();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}

	/**
	 * 提交
	 * 
	 * @throws DatabaseException
	 */
	public void commit() throws DatabaseException {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}

	/**
	 * 回滚
	 * 
	 * @throws DatabaseException
	 */
	public void rollback() throws DatabaseException {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}

	/**
	 * 关闭连接
	 * 
	 * @throws DatabaseException
	 */
	public void close() throws DatabaseException {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}

	/**
	 * @return 连接是否关闭
	 * @throws DatabaseException
	 */
	public boolean isClose() throws DatabaseException {
		try {
			return conn.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}

}
