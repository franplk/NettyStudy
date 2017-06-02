package com.emar.mbg.queryserver.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.emar.mbg.queryserver.constants.QueryServerConstants;

public class ActionPrepareUtil {
	/**
	 * 
	 * @param apiName
	 * @param properties
	 * @return
	 */
	public static String getDefaultDim(Properties properties) {
		String apiName = properties.getProperty("apiName");
		return properties.getProperty(apiName+".defaultDim");
	}
	
	public static String getHistoryEngine(Properties properties){
		String apiName = properties.getProperty("apiName");
		String[] engineInfo = properties.getProperty(apiName+".history.engine").split(":");
		return engineInfo[0];
		
	}
	
	public static String getHistoryIndex(Properties properties){
		String apiName = properties.getProperty("apiName");
		String[] engineInfo = properties.getProperty(apiName+".history.engine").split(":");
		return engineInfo[1];
	}
	
	
	public static String getRealtimeEngine(Properties properties){
		String apiName = properties.getProperty("apiName");
		String[] engineInfo = properties.getProperty(apiName+".realtime.engine").split(":");
		return engineInfo[0];
		
	}
	
	public static String getRealtimeIndex(Properties properties){
		String apiName = properties.getProperty("apiName");
		String[] engineInfo = properties.getProperty(apiName+".realtime.engine").split(":");
		return engineInfo[1];
	}
	
	public static Map<String,QueryServerConstants.ValueType> getDims(Properties properties) {
		String apiName = properties.getProperty("apiName");
		Map<String,QueryServerConstants.ValueType> dims = new HashMap<String,QueryServerConstants.ValueType>();
		String [] dimsInfo = properties.getProperty(apiName+".dims").split(",");
		for(String dim:dimsInfo) {
			String dimName = dim; 
			if (dim.contains(":")) {
				String [] dimDetail = dim.split(":");
				dimName = dimDetail[0];
				String dimType = dimDetail[1];
				if(dimType.equalsIgnoreCase("string")) {
					dims.put(dimName, QueryServerConstants.ValueType.STRING);
				}
				if(dimType.equalsIgnoreCase("int")) {
					dims.put(dimName, QueryServerConstants.ValueType.INT);
				}
			}else {
				dims.put(dimName, QueryServerConstants.ValueType.STRING);
			}
		}
		return dims;
	}
	
	public static Map<String,String> getQuotas(Properties properties) {
		String apiName = properties.getProperty("apiName");
		Map<String,String> quotas = new HashMap<String,String>();
		String [] quotaInfo = properties.getProperty(apiName+".quota").split(",");
		for(String quota:quotaInfo) {
			String quotaName = quota; 
			if (quota.contains(":")) {
				String [] quotaDetail = quota.split(":");
				quotaName = quotaDetail[0];
				String quotaType = quotaDetail[1];
				if(quotaType.equalsIgnoreCase("string")) {
					quotas.put(quotaName, "other");
				}
				if(quotaType.equalsIgnoreCase("int")) {
					quotas.put(quotaName, "int");
				}
				if(quotaType.equalsIgnoreCase("double")) {
					quotas.put(quotaName, "digit");
				}
			}else {
				quotas.put(quotaName, "int");
			}
		}
		return quotas;
	}
	
	public static Map<String,String> getSortables(Properties properties) {
		String apiName = properties.getProperty("apiName");
		Map<String,String> sortables = new HashMap<String,String>();
		String [] sortableInfo = properties.getProperty(apiName+".sortable").split(",");
		for(String quota:sortableInfo) {
			String quotaName = quota; 
			if (quota.contains(":")) {
				String [] quotaDetail = quota.split(":");
				quotaName = quotaDetail[0];
				String quotaType = quotaDetail[1];
				if(quotaType.equalsIgnoreCase("string")) {
					sortables.put(quotaName, "other");
				}
				if(quotaType.equalsIgnoreCase("int")) {
					sortables.put(quotaName, "int");
				}
				if(quotaType.equalsIgnoreCase("double")) {
					sortables.put(quotaName, "digit");
				}
			}else {
				sortables.put(quotaName, "int");
			}
		}
		return sortables;
	}
	
}
