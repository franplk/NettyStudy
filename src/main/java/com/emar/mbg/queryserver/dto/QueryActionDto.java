package com.emar.mbg.queryserver.dto;

import java.util.List;

/**
 * 用于进行返回内容处理的数据存储类
 * @author caizhenyu
 *
 */
public class QueryActionDto {
	private long total_num;
	private int page_num;
	private int page_size;
	private List<Object> results;
	private Object summary;
	private String description;
	public long getTotal_num() {
		return total_num;
	}
	public void setTotal_num(long total_num) {
		this.total_num = total_num;
	}
	public int getPage_num() {
		return page_num;
	}
	public void setPage_num(int page_num) {
		this.page_num = page_num;
	}
	public int getPage_size() {
		return page_size;
	}
	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}
	public List<Object> getResults() {
		return results;
	}
	public void setResults(List<Object> results) {
		this.results = results;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getSummary() {
		return summary;
	}
	public void setSummary(Object summary) {
		this.summary = summary;
	}
	
}
