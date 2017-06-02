package com.emar.mbg.queryserver.dao;

public abstract class AbstractNoSQLDao {
	public abstract void setValue(String key,Object value,long liveMiliseconds);
	public abstract Object getObject(String key);
	public abstract String getString(String key);
}
