package com.ddk.smmp.pushserver.jdbc.database;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class DataModel implements Serializable {
	public abstract Integer getId();
}
