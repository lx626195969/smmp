package com.sioo.cmppgw.jdbc;

import java.sql.Connection;

import com.sioo.cmppgw.jdbc.database.DatabaseTransaction;

public abstract class BaseService {

	private DatabaseTransaction trans;

	protected BaseService(DatabaseTransaction trans) {
		this.trans = trans;
	}

	protected BaseService() {
	}

	protected DatabaseTransaction getTransaction() {
		return trans;
	}

	protected Connection getConnection() {
		return trans.getConnection();
	}
}