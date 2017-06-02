package com.emar.mbg.queryserver.actions;

import com.emar.mbg.queryserver.dto.QueryActionDto;

/**
 * 默认的接口。如果出现无法处理的接口，则跳转到此接口进行处理
 * @author caizhenyu
 *
 */
public class DefaultAction extends AbstractQueryAction {

	@Override
	protected QueryActionDto getDataFromCache() {
		return null;
	}

	@Override
	protected QueryActionDto getDataFromDB() {
		
		return null;
	}


}
