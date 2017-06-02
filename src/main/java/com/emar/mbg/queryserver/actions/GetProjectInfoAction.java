package com.emar.mbg.queryserver.actions;

import java.util.ArrayList;
import java.util.List;

import com.emar.mbg.queryserver.constants.QueryServerConstants;
import com.emar.mbg.queryserver.dto.QueryActionDto;


public class GetProjectInfoAction extends AbstractQueryAction {

	@Override
	protected QueryActionDto getDataFromCache() {
		// TODO Auto-generated method stub
		System.out.println("GetProjectInfo getDataFromCache");
		return null;
	}

	@Override
	protected QueryActionDto getDataFromDB() {
		QueryActionDto dto = new QueryActionDto();
		List<Object> list = new ArrayList<Object>();
		
		list.add(1.1);
		list.add(2);
		
		dto.setResults(list);
		dto.setTotal_num(list.size());
		return dto;
	}
	@Override
	protected void prepare() {
		this.setCacheDependency(QueryServerConstants.CacheDependency.FULLY.getDependency());
	}
	@Override
	protected void roundOff() {
		System.out.println("GetProjectInfo roundOff");
	}
	


}
