package com.emar.mbg.queryserver.dao;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * EhCache的数据操作对象类
 * @author caizhenyu
 *
 */
public class EhCacheDao extends AbstractNoSQLDao {
	
	private Cache cache;

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void setValue(String key, Object value,long liveMiliSeconds) {
		Element element = new Element(key,value);
		int seconds = new Long(liveMiliSeconds/1000).intValue();
		element.setTimeToLive(seconds);
		element.setTimeToIdle(seconds);
		cache.put(element);
	}
	
	public void setValue(String key, Object value) {
		Element element = new Element(key,value);
		cache.put(element);
	}

	@Override
	public Object getObject(String key) {
		Element element = cache.get(key);
		Object value = null;
		if (element!=null) {
			value = element.getObjectValue();
		}
		return value;
	}

	@Override
	public String getString(String key) {
		Element element = cache.get(key);
		String value = null;
		if (element!=null) {
			try{
				value = element.getObjectValue().toString();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public void close() {
		CacheManager.getInstance().shutdown();
	}
}
