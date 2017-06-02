package com.emar.mbg.queryserver.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;


public class SQLBasedDataUtil {
	private static JdbcTemplate jdbcTemplate;
	
	public static JdbcTemplate getJdbcTemplate() {
		if(SQLBasedDataUtil.jdbcTemplate == null) {
			synchronized (SQLBasedDataUtil.class) {
				try{
					ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("esconfig.xml");
					
					SQLBasedDataUtil.jdbcTemplate = (JdbcTemplate)context.getBean("dataTemplate");
					
					context.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return SQLBasedDataUtil.jdbcTemplate;
	}
}
