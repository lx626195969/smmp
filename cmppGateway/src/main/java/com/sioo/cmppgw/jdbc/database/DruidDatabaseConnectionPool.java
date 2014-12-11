package com.sioo.cmppgw.jdbc.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * 数据库连接池
 * 
 */
public final class DruidDatabaseConnectionPool {
	private static final Logger LOG = LoggerFactory.getLogger((DruidDatabaseConnectionPool.class).getSimpleName());
	
	private static DruidDataSource _dds = null;
	private static String confile = "config/jdbc_druid.properties";
	private static Properties p = null;
	private static InputStream inputStream = null;

	/**
	 * 开启连接池
	 */
	public static void startup() {
		try {
			p = new Properties();
			confile = Class.class.getClass().getResource("/").getPath() + confile;
			File file = new File(confile);  
            inputStream = new BufferedInputStream(new FileInputStream(file));  
            p.load(inputStream);
            
	        try {
	            _dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(p);
	            LOG.info("DruidPool Inited......");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		} finally {
			try {
				if (inputStream != null) {  
				    inputStream.close();  
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭连接池
	 */
	public static void shutdown() {
		_dds.close();
	}

	/**
	 * @return 数据库连接
	 */
	public static Connection getConnection() {
		try {
			return _dds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}
	
	public static void main(String[] args) {
		DruidDatabaseConnectionPool.startup();
	}

}
