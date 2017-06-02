package com.emar.mbg.queryserver.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.emar.mbg.queryserver.constants.ErrorConstants;
import com.emar.mbg.queryserver.dto.QueryActionDto;
import com.emar.mbg.queryserver.exceptions.QueryServerException;


/**
 * 根据QueryActionDto生成JSON格式返回结果的工具类
 * @author caizhenyu
 *
 */
public class ResultAssemblyUtil {
	/**
	 * 根据查询结果生成返回结果
	 * @param dto
	 * @return
	 */
	public static String getAssembledResult(QueryActionDto dto) {
		JSONObject result = new JSONObject();
		
		int code = ErrorConstants.ERROR_CODE.UNKOWN.getCode();
		if (dto != null) {
			long total_num = dto.getTotal_num() ;
			if (total_num >0 ) {
				code = ErrorConstants.ERROR_CODE.OK.getCode();
				result.put("total_num", total_num);
				
				List<Object> resultMapping = dto.getResults();
				JSONArray stats = new JSONArray();
				
				for( Object tuple: resultMapping ) {
					if (tuple instanceof Integer) {
						stats.put((Integer) tuple);
					}
					if (tuple instanceof String) {
						stats.put((String) tuple);
					}
					if (tuple instanceof Double) {
						stats.put((Double) tuple);
					}
					if (tuple instanceof Boolean) {
						stats.put((Boolean) tuple);
					}
					if (tuple instanceof Long) {
						stats.put((Long) tuple);
					}
					if (tuple instanceof JSONObject) {
						stats.put((JSONObject)tuple);
					}
				}
				
				result.put("result", stats);
			}else {
				code = ErrorConstants.ERROR_CODE.NO_DATA.getCode();
			}
			
		}else {
			code = ErrorConstants.ERROR_CODE.NO_DATA.getCode();
		}
		
		//这个方法里只有两种码
		if(code == 0) {
			String description = dto.getDescription();
			if (StringUtil.isEmpty(description)) {
				description = "SUCCESS";
			}
			result.put("description", description);
		}else {
			result.put("description", "NO DATA");
		}
		
		
		result.put("error", code);
		
		return result.toString();
	}
	
	/**
	 * 根据异常生成返回结果
	 * @param e 异常信息
	 * @return
	 */
	public static String getAssembledResult(Throwable e) {
		JSONObject result = new JSONObject();
		int code = ErrorConstants.ERROR_CODE.UNKOWN.getCode();
		
		String msg = e.getStackTrace()[0].toString();
		//TODO
		/**
		 * 根据不同的类型给予错误代码
		 */
		if(e instanceof QueryServerException){
			QueryServerException exception = (QueryServerException) e;
			code = exception.getErrorCode();
			msg = exception.getDescription();
		}
		
		result.put("description", msg);
		result.put("error", code);
		return result.toString();
		
	}
}
