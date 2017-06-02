package com.emar.mbg.queryserver.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.emar.mbg.queryserver.constants.QueryServerConstants.ValueType;


public class QueryToSQLParserUtil {
	public static boolean isValid(String key,String content) {
		boolean flag = false;
		//TODO
		
		
		return flag;
	}
	
	public static boolean isValid(String key,String content,int key_type) {
		boolean flag = false;
		//TODO
		
		
		return flag;
	}
	
	private static Map<String,String> strSignals = new HashMap<String,String>();
	private static Map<String,String> valueSignals = new HashMap<String,String>();
	static {
		valueSignals.put("lt", " < ");
		valueSignals.put("gt", " > ");
		valueSignals.put("lte", " <= ");
		valueSignals.put("gte", " >= ");
		valueSignals.put("neq", " != ");
		valueSignals.put("eq", " = ");
		
		strSignals.put("eq", " = ");
		strSignals.put("neq", " != ");
		strSignals.put("like", " LIKE ");
		strSignals.put("nlike", " NOT LIKE ");
	}
	
	public static String parseContentForSQL(String key,String content, int key_type) {
		StringBuffer buf = new StringBuffer();

		try {
			if (key.equals("sort_by")) {
				JSONArray array = null;
				boolean validFlag =  false;
				if (content.trim().startsWith("[")&&content.trim().endsWith("]")) {
					array = new JSONArray(content);
					validFlag = true;
				}else{
					if (content.trim().startsWith("{")&&content.trim().endsWith("}")) {
						array = new JSONArray();
						array.put(new JSONObject(content));
						validFlag = true;
					}
				}
				if (validFlag == true) {
					
					StringBuffer subBuf = new StringBuffer();
					for(int i=0;i<array.length();i++) {
						JSONObject condition = array.getJSONObject(i);
						for(String field:condition.keySet()) {
							//防止大小写问题
							if(condition.getString(field).toLowerCase().equals("asc")||condition.getString(field).toLowerCase().equals("desc")) {
								subBuf.append(field);
								subBuf.append(" ");
								subBuf.append(condition.getString(field).toUpperCase());
								if (i<array.length()-1) {
									subBuf.append(",");
								}
							}
						}
					}
					if(subBuf.length()>0) {
						buf.append("ORDER BY ");
						buf.append(subBuf.toString());
					}
				}
			}else {
				if (content.trim().startsWith("[")&&content.trim().endsWith("]")) {
					buf.append(QueryToSQLParserUtil.getINPhrase(key,"eq",content,key_type));
				}else {
					if (content.trim().startsWith("{")&&content.trim().endsWith("}")) {
						JSONObject condition = new JSONObject(content);
						int index = 0;
						int size = condition.keySet().size();
						for (String signal : condition.keySet()) {
							if (key_type == ValueType.STRING.getType()) {
								//字符串型处理逻辑
								boolean finished = false;
								String signalContent = condition.get(signal).toString();
								if (signal.equals("like")||signal.equals("nlike")) {
									if (signalContent.trim().startsWith("[")&&signalContent.trim().endsWith("]")){
										buf.append(QueryToSQLParserUtil.getLIKEPhrase(key,signal,signalContent));
										finished = true;
									}
								}else {
									if(signal.equals("eq")||signal.equals("neq")) {
										if (signalContent.trim().startsWith("[")&&signalContent.trim().endsWith("]")){
											buf.append(QueryToSQLParserUtil.getINPhrase(key,signal,signalContent,key_type));
											finished = true;
										}
									}
								}
								
								if (finished == false) {
									String plus="";
									if(signal.equals("like")||signal.equals("nlike")){
										plus="%";
									}
									buf.append(key);
									buf.append(strSignals.get(signal));
									buf.append("'");
									buf.append(plus);
									buf.append(condition.get(signal));
									buf.append(plus);
									buf.append("'");
								}
							}
							if (key_type == ValueType.INT.getType()||key_type == ValueType.LONG.getType()||key_type == ValueType.DOUBLE.getType()) {
								String signalContent = condition.get(signal).toString();
								boolean orignalFlag = false;
								if(signal.equals("eq")||signal.equals("neq")) {
									if (signalContent.trim().startsWith("[")&&signalContent.trim().endsWith("]")){
										orignalFlag = false;
									}else {
										orignalFlag = true;
									}
								}else {
									orignalFlag = true;
								}
								if (orignalFlag == false) {
									buf.append(QueryToSQLParserUtil.getINPhrase(key,signal,signalContent,key_type));
								}else{
									buf.append(key);
									buf.append(valueSignals.get(signal));
									buf.append(condition.get(signal));
								}
							}
							if(index<size-1) {
								buf.append(" AND ");
								index++;
							}
							
						}
					}else {
						if (key_type == ValueType.STRING.getType()||key_type == ValueType.DATE.getType()) {
							buf.append(key);
							buf.append(strSignals.get("eq"));
							buf.append("'");
							buf.append(content);
							buf.append("'");
						}
						if (key_type == ValueType.INT.getType()||key_type == ValueType.LONG.getType()||key_type == ValueType.DOUBLE.getType()) {
							buf.append(key);
							buf.append(valueSignals.get("eq"));
							buf.append(content);
						}
					}
				}
				
			}
		}catch( Exception e) {
			e.printStackTrace();
		}
		
		return buf.toString();
	}
	private static String getINPhrase(String key,String signal,String content,int key_type){
		StringBuffer buf = new StringBuffer();
		JSONArray qConditions = new JSONArray(content);
		buf.append(key);
		String inSignal = signal.equals("eq")?" IN (":" NOT IN (";
		buf.append(inSignal);
		for(int i=0 ;i < qConditions.length();i++) {
			Object qcondition = qConditions.get(i);
			
			if (key_type == ValueType.STRING.getType()) {
				buf.append("'");
				buf.append(qcondition);
				buf.append("'");
			}
			if (key_type == ValueType.INT.getType()||key_type == ValueType.LONG.getType()||key_type == ValueType.DOUBLE.getType()) {
				buf.append(qcondition);
			}
			if (i < qConditions.length()-1) {
				buf.append(",");
			}
		}
		
		buf.append(")");
		return buf.toString();
	}
	
	private static String getLIKEPhrase(String key,String signal,String content){
		StringBuffer buf = new StringBuffer();
		JSONArray qConditions = new JSONArray(content);
		buf.append("(");
		
		for(int i=0 ;i < qConditions.length();i++) {
			Object qcondition = qConditions.get(i);
			buf.append(key);
			
			String plus = "";
			if(signal.equals("like")||signal.equals("nlike")){
				plus="%";
			}
			buf.append(strSignals.get(signal));
			buf.append("'");
			buf.append(plus);
			buf.append(qcondition);
			buf.append(plus);
			buf.append("'");
			
			if (i < qConditions.length()-1) {
				if(signal.equals("nlike")){
					buf.append(" AND ");
				}
				if(signal.equals("like")){
					buf.append(" OR ");
				}
			}
		}
		
		buf.append(")");
		return buf.toString();
	}
	
	
	public static void main(String [] args) {
//		String key="channel_id";
//		String content = "[1,2]";
//		String content = "{\"lt\":100,\"gte\":1,\"neq\":[7,8,9]}";
//		String content = "{\"neq\":[\"100\",\"101\"]}";
//		String content = "{\"neq\":\"100\"}";
//		String content = "ab";
		
//		key="sort_by";
//		String content="[{neq:desc},{eq:asc}]";
		Map<String,String> requestMapping = new HashMap<String,String>();
		requestMapping.put("channel_id", "[1,2]");
		requestMapping.put("project_id", "{lt:100,gt:3}");
		requestMapping.put("sort_by", "{channel:desc}");
		StringBuffer where = new StringBuffer("WHERE ");
		StringBuffer sort = new StringBuffer();
		for(String key:requestMapping.keySet()) {
			String content = requestMapping.get(key);
			if (key.equals("sort_by")) {
				sort.append(QueryToSQLParserUtil.parseContentForSQL(key, content, ValueType.INT.getType()));
			}else{
				if (!where.toString().trim().endsWith(" AND")&&!where.toString().trim().endsWith("WHERE")) {
					where.append(" AND ");
				}
				where.append(QueryToSQLParserUtil.parseContentForSQL(key, content, ValueType.INT.getType()));
			}
		}
		System.out.println(where.toString() +" " +sort.toString());
	}
}
