package com.emar.mbg.queryserver.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.emar.mbg.queryserver.dto.QueryActionDto;
import com.emar.mbg.queryserver.engine.es.ESQuery;
import com.emar.mbg.queryserver.engine.es.EsTemplate;
import com.emar.mbg.queryserver.engine.es.JTableData;

public class ESDao {
	private EsTemplate template;

	public EsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(EsTemplate template) {
		this.template = template;
	}
	
	/**
	 * 查询方法
	 * @param query
	 * @return
	 */
	public QueryActionDto findByQuery(ESQuery query) {
		QueryActionDto dto = null;
		JTableData table = template.queryTable(query);
		
		dto = assemblyToActionDto(table, query);
		
		return dto;
	}
	/**
	 * 
	 * @param table
	 * @param query
	 * @return
	 */
	private QueryActionDto assemblyToActionDto(JTableData table ,ESQuery query) {
		QueryActionDto dto = new QueryActionDto();
		long total_num = 0;
		if(table!=null) {
			total_num = table.getTotal();
			if(total_num > 0) {
				
				List<Map<String,Object>> rows = table.getRows();
				List<Object> results = new ArrayList<Object>();
				for (int i=0;i<rows.size();i++) {
					Map<String,Object> row = rows.get(i);
					JSONObject obj = new JSONObject();
					for(String key: row.keySet()) {
						obj.put(key, row.get(key));
					}
					results.add(obj);
				}
				dto.setDescription("SUCCESS");
				dto.setResults(results);
				
			}else {
				//查出来的返回结果是空
				dto.setDescription("NO DATA");
				List<Object> results = new ArrayList<Object>();
				dto.setResults(results);
			}
			
		}else {
			//异常情况导致的空
			dto.setDescription("NO DATA");
			List<Object> results = new ArrayList<Object>();
			dto.setResults(results);
		}
		
		dto.setTotal_num(total_num);
		int page_num = query.getPage().getCurrPage();
		int page_size = query.getPage().getPageSize();
		if(page_num <= 1) {
			page_num = 1;
		}
		dto.setPage_num(page_num);
		dto.setPage_size(page_size);
		
		return dto;
	}
}
