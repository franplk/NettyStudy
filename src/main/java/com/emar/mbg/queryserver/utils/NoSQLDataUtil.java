package com.emar.mbg.queryserver.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.emar.mbg.queryserver.dao.ESDao;
import com.emar.mbg.queryserver.dao.EhCacheDao;
import com.emar.mbg.queryserver.engine.es.EsTemplate;

import net.sf.ehcache.CacheManager;

public class NoSQLDataUtil {
	private static CacheManager manager;
	private static ESDao esHistoryDao;
	private static ESDao esRealtimeDao;
	/**
	 * 获取EhCache实例
	 * @return
	 */
	public static EhCacheDao getEhCache() {
		if (NoSQLDataUtil.manager==null) {
			synchronized(CacheManager.class) {
				NoSQLDataUtil.manager = new CacheManager(NoSQLDataUtil.class.getClassLoader().getResourceAsStream("ehcache.xml"));
			}
		}
		
		EhCacheDao dao = new EhCacheDao();
		dao.setCache(manager.getCache("data"));
		
		return dao;
	}
	
	/**
	 * 获取ESDao离线实例
	 * @return
	 */
	public static ESDao getESHistoryDao() {
		if (esHistoryDao == null) {
			synchronized(ESDao.class) {
				try {
					ESDao dao = new ESDao();
					dao.setTemplate(getHistoryTemplate());
					esHistoryDao = dao;
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		return esHistoryDao;
	}
	
	/**
	 * 获取ESDao实例
	 * @return
	 */
	public static ESDao getESRealtimeDao() {
		if (esRealtimeDao == null) {
			synchronized(ESDao.class) {
				try {
					ESDao dao = new ESDao();
					dao.setTemplate(getRealtimeTemplate());
					esRealtimeDao = dao;
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		return esRealtimeDao;
	}
	private static EsTemplate realtimeTemplate;
	private static EsTemplate historyTemplate;
	
	public static EsTemplate getHistoryTemplate() {
		if (historyTemplate==null) {
			synchronized (EsTemplate.class) {
				try{
					ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("esconfig.xml");
					
					historyTemplate = (EsTemplate) context.getBean("historyTemplate");
					
					context.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return historyTemplate;
	}
	
	public static EsTemplate getRealtimeTemplate() {
		if (realtimeTemplate==null) {
			synchronized (EsTemplate.class) {
				try{
					ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("esconfig.xml");
					
					realtimeTemplate = (EsTemplate) context.getBean("realTemplate");
					
					context.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return realtimeTemplate;
	}
}
