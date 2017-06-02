package com.emar.mbg.queryserver.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.emar.mbg.queryserver.constants.QueryServerConstants;
import com.emar.mbg.queryserver.engine.es.ConfigColumn;
import com.emar.mbg.queryserver.engine.es.DateSpan;
import com.emar.mbg.queryserver.engine.es.ESFilter;

public class QueryToESParserUtil {
	/**
	 * 
	 * @param requestValue  请求的参数
	 * @param exclude_today 是否排除掉今天
	 * @return null 表示输入的参数不合规范
	 */
	public static DateSpan getDateSpan(String requestValue,boolean exclude_today) {
		DateSpan dateSpan = null;
		
		if(requestValue!=null) {
			String days = requestValue.trim();
			if(!StringUtil.isEmpty(days)) {
				dateSpan = new DateSpan();
				//也可以用来做有效性验证
				if (days.startsWith("{")&&days.endsWith("}")) {
					JSONObject obj = new JSONObject(days);
					Date endDate = null;
					Date startDate = null;
					Date today = DateUtil.getToday();
					if(obj.has("lt")||obj.has("lte")) {
						String key="lt";
						if (obj.has(key)) {
							String endDateStr = obj.getString(key);
							endDate = DateUtil.getDate(endDateStr, QueryServerConstants.dateFormatter);
							if (endDate == null) {
								return null;
							}
							Calendar ca = Calendar.getInstance();
							ca.setTime(endDate);
							ca.add(Calendar.DATE, -1);
							endDate = ca.getTime();
						}else {
							key = "lte";
							String endDateStr = obj.getString(key);
							endDate = DateUtil.getDate(endDateStr, QueryServerConstants.dateFormatter);
							if (endDate == null) {
								return null;
							}
						}
						
						if (today.compareTo(endDate) <=0 ) {
							endDate = today;
							if(exclude_today == true) {
								Calendar ca = Calendar.getInstance();
								ca.setTime(endDate);
								ca.add(Calendar.DATE, -1);
								endDate = ca.getTime();
							}
						}
						dateSpan.setEndDate(DateUtil.formatDate(endDate, QueryServerConstants.dateFormatter));
					}
					
					if(obj.has("gt")||obj.has("gte")) {
						startDate = null;
						String key="gt";
						if (obj.has(key)) {
							String startDateStr = obj.getString(key);
							startDate = DateUtil.getDate(startDateStr, QueryServerConstants.dateFormatter);
							if (startDate == null) {
								return null;
							}
							Calendar ca = Calendar.getInstance();
							ca.setTime(startDate);
							ca.add(Calendar.DATE, 1);
							startDate = ca.getTime();
						}else {
							key = "gte";
							String startDateStr = obj.getString(key);
							startDate = DateUtil.getDate(startDateStr, QueryServerConstants.dateFormatter);
							if (startDate == null) {
								return null;
							}
						}
						
						if (today.compareTo(startDate) < 0 ) {
							return null;
						}else {
							if (today.equals(startDate)&&exclude_today==true) {
								return null;
							}
						}
						if(endDate != null) {
							if(startDate.compareTo(endDate)>0) {
								//开始日期大于结束日期
								return null;
							}
						}
						dateSpan.setStartDate(DateUtil.formatDate(startDate, QueryServerConstants.dateFormatter));
					}	
					if(obj.has("eq")) {
						String key = "eq";
						String startDateStr = obj.getString(key);
						startDate = DateUtil.getDate(startDateStr, QueryServerConstants.dateFormatter);
						if (today.compareTo(startDate) < 0 ) {
							return null;
						}else {
							if (today.equals(startDate)&&exclude_today==true) {
								return null;
							}
						}
						dateSpan.setStartDate(DateUtil.formatDate(startDate, QueryServerConstants.dateFormatter));
						dateSpan.setEndDate(DateUtil.formatDate(startDate, QueryServerConstants.dateFormatter));
					}
					
					if (dateSpan!=null) {
						//日期字段补全
						if(StringUtil.isEmpty(dateSpan.getStartDate())&&StringUtil.isNotEmpty(dateSpan.getEndDate())) {
							dateSpan.setStartDate(dateSpan.getEndDate());
						}
						if(StringUtil.isEmpty(dateSpan.getEndDate())&&StringUtil.isNotEmpty(dateSpan.getStartDate())) {
							if(exclude_today == false) {
								dateSpan.setEndDate(DateUtil.getTodayStr());
							}else {
								Calendar ca = Calendar.getInstance();
								ca.setTime(today);
								ca.add(Calendar.DATE, -1);
								dateSpan.setEndDate(DateUtil.formatDate(ca.getTime()));
							}
						}
						if(StringUtil.isEmpty(dateSpan.getEndDate())&&StringUtil.isEmpty(dateSpan.getStartDate())) {
							dateSpan = null;
						}
					}
				}else{
					if (days.startsWith("[")&&days.endsWith("]")) {
						//TODO
						//目前不支持
					}else {
						try{
							Date date = DateUtil.getDate(days, QueryServerConstants.dateFormatter);
							if(date==null) {
								dateSpan = null;
							}else{
								dateSpan.setStartDate(days);
								dateSpan.setEndDate(days);
							}
						}catch(Exception e) {
							e.printStackTrace();
							dateSpan = null;
						}
					}
				}
				
			}
		}
		
		return dateSpan;
	}
	
	public static DateSpan getDateSpan(String requestValue) {
		return getDateSpan(requestValue, true);
	}
	
	/**
	 * 获取当天的DateSpan，用于实时查询
	 * @return
	 */
	public static DateSpan getTodayDateSpan() {
		DateSpan dateSpan = new DateSpan();
		String todayStr = DateUtil.getTodayStr();
		dateSpan.setStartDate(todayStr);
		dateSpan.setEndDate(todayStr);
		
		return dateSpan;
		
	}
	
	public static List<ESFilter> getDimFilterList(Map<String,String> params,Map<String,QueryServerConstants.ValueType> resultKeys) {
		List<ESFilter> dimfilterList = new ArrayList<ESFilter>();
		for(String key:params.keySet()) {

			if (resultKeys.containsKey(key)) {
				QueryServerConstants.ValueType type = resultKeys.get(key);
				String param = params.get(key);
				
				if(param.startsWith("{")&&param.endsWith("}")) {
					JSONObject json = new JSONObject(param);
					if (json.has("eq")) {
						ESFilter filter = new ESFilter();
						ConfigColumn column = new ConfigColumn(key);
						filter.setColumn(column);
						filter.setType("eq");
						filter.setValue(json.getString("eq"));
						dimfilterList.add(filter);
					}else{
						if(type.equals(QueryServerConstants.ValueType.STRING)) {
							ESFilter filter = new ESFilter();
							ConfigColumn column = new ConfigColumn(key);
							filter.setColumn(column);
							if(json.has("like")) {
								filter.setType("inc");
								filter.setValue(json.getString("like"));
								dimfilterList.add(filter);
							}
						}else{
							if(type.equals(QueryServerConstants.ValueType.INT)) {
								if(json.has("gt")||json.has("lt")||json.has("gte")||json.has("lte")) {
									for(String signal:json.keySet()){
										ESFilter filter = new ESFilter();
										ConfigColumn column = new ConfigColumn(key);
										filter.setColumn(column);
										if(signal.equals("gt")||signal.equals("gte")||signal.equals("lt")||signal.equals("lte")) {
											filter.setType(signal);
											filter.setValue(json.getString(signal));
											dimfilterList.add(filter);
										}
									}
								}
							}
						}
					}
					
				}else{
					ESFilter filter = new ESFilter();
					ConfigColumn column = new ConfigColumn(key);
					filter.setColumn(column);
					if(param.startsWith("[")&&param.endsWith("]")) {
						//TODO
						//暂不支持复杂语法
						JSONArray array = new JSONArray(param);
						StringBuffer buffer = new StringBuffer();
						for(int i=0;i<array.length();i++){
							String item = array.getString(i);
							if(item.startsWith("{")&&item.endsWith("}")) {
								JSONObject itemJson = array.getJSONObject(i);
								if(itemJson.has("eq")) {
									buffer.append(itemJson.get("eq"));
									if(i<array.length()-1){
										buffer.append(",");
									}
								}
							}else {
								buffer.append(item);
								if(i<array.length()-1){
									buffer.append(",");
								}
							}
						}
						filter.setValue(buffer.toString());
					}else {
						filter.setType("eq");
						filter.setValue(param);
					}
					dimfilterList.add(filter);
				}
				
			}

		}
		
		return dimfilterList;
	}
	
	public static List<ConfigColumn> getSortColumn(Map<String,String> params,Map<String,String> resultDims,Map<String,QueryServerConstants.ValueType> dims) {
		
		List<ConfigColumn> columns = new ArrayList<ConfigColumn> ();

		if(params.containsKey("sort_by")) {
			String param = params.get("sort_by");
			if (param.startsWith("[")&&param.endsWith("]")) {
				try{
					
					JSONArray array = new JSONArray(param);
					for(int i=0;i<array.length();i++) {
						JSONObject json = array.getJSONObject(i);
						for(String key:json.keySet()) {
						
							if(resultDims.containsKey(key)) {
								String value = json.getString(key);
								ConfigColumn column = new ConfigColumn(key);
								if(dims.containsKey(key)){
									//维度排序
									column.setDim(1);
								}else {
									column.setDim(0);
								}
								column.setDataType(resultDims.get(key));
								column.setSorting(1);
								if(value.equalsIgnoreCase("desc")) {
									column.setSortType(QueryServerConstants.SortType.DESC);
									columns.add(column);
								}
								if(value.equalsIgnoreCase("asc")) {
									column.setSortType(QueryServerConstants.SortType.ASC);
									columns.add(column);
								}
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}else {
			String key = "req_num";
			String value = "desc";
			if(resultDims.containsKey(key)) {
				
				ConfigColumn column = new ConfigColumn(key);
				column.setDim(0);
				column.setDataType(resultDims.get(key));
				column.setSorting(1);
				if(value.equalsIgnoreCase("desc")) {
					column.setSortType(QueryServerConstants.SortType.DESC);
					columns.add(column);
				}
				
			}
		}
		return columns;
	}
	
}
