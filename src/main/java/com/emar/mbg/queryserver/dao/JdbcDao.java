package com.emar.mbg.queryserver.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.emar.mbg.queryserver.dto.QueryActionDto;

public class JdbcDao {
	private JdbcTemplate template;
	
	public QueryActionDto findByQuery(String sql) {
		List<Map<String, Object>> list = template.queryForList(sql);
		
		
		
		return null;
	}
	
	public long getTotal(String sql) {
		String tmp = sql.toLowerCase();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) ");
		sb.append(tmp.substring(tmp.indexOf("from")));
		long result = 0;
		try{
			result = template.queryForObject(sb.toString(),Long.class);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
