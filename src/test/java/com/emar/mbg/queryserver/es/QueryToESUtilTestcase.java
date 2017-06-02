package com.emar.mbg.queryserver.es;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.emar.mbg.queryserver.constants.QueryServerConstants;
import com.emar.mbg.queryserver.engine.es.DateSpan;
import com.emar.mbg.queryserver.utils.DateUtil;
import com.emar.mbg.queryserver.utils.QueryToESParserUtil;

import junit.framework.TestCase;

public class QueryToESUtilTestcase extends TestCase {

	public void testGetDateSpanEq() {
		JSONObject json = new JSONObject();
		Date today = DateUtil.getToday();
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, 1);
		Date tomorrow = ca.getTime();
		
		json.put("eq", DateUtil.formatDate(tomorrow, QueryServerConstants.dateFormatter));
		
		//明天
		DateSpan dateSpan = QueryToESParserUtil.getDateSpan(json.toString());
		assertNull(dateSpan);
		//包含今天
		json = new JSONObject();
		json.put("eq", DateUtil.formatDate(today, QueryServerConstants.dateFormatter));
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString(),false);
		assertEquals(DateUtil.getTodayStr(QueryServerConstants.dateFormatter), dateSpan.getStartDate());
		assertEquals(DateUtil.getTodayStr(QueryServerConstants.dateFormatter), dateSpan.getEndDate());
		
		//排除今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString(),true);
		assertNull(dateSpan);
	}
	
	public void testGetDateSpanLt() {
		JSONObject json = new JSONObject();
		Date today = DateUtil.getToday();
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, 1);
		Date tomorrow = ca.getTime();
		ca.add(Calendar.DATE, -2);
		Date yestoday = ca.getTime();
		DateSpan dateSpan = null;
		
		json.put("lt", DateUtil.formatDate(tomorrow, QueryServerConstants.dateFormatter));
		//排除今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString());
		assertEquals(DateUtil.formatDate(yestoday), dateSpan.getEndDate());
		
		//包含今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString(),false);
		assertEquals(DateUtil.getTodayStr(QueryServerConstants.dateFormatter), dateSpan.getEndDate());
		
		json = new JSONObject();
		json.put("lte", DateUtil.formatDate(today, QueryServerConstants.dateFormatter));
		//排除今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString());
		assertEquals(DateUtil.formatDate(yestoday), dateSpan.getEndDate());
		//包含今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString(),false);
		assertEquals(DateUtil.getTodayStr(QueryServerConstants.dateFormatter), dateSpan.getEndDate());		
	}
	
	public void testGetDateSpanGt() {
		JSONObject json = new JSONObject();
		Date today = DateUtil.getToday();
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, 1);
		Date tomorrow = ca.getTime();
		ca.add(Calendar.DATE, -2);
		Date yestoday = ca.getTime();
		DateSpan dateSpan = null;
		
		json.put("gte", DateUtil.formatDate(tomorrow, QueryServerConstants.dateFormatter));
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString(),false);
		assertNull(dateSpan);
		
		json.put("gt", DateUtil.formatDate(yestoday, QueryServerConstants.dateFormatter));
		//排除今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString());
		assertNull(dateSpan);
		
		//包含今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString(),false);
		assertEquals(DateUtil.getTodayStr(QueryServerConstants.dateFormatter), dateSpan.getStartDate());
		
		json = new JSONObject();
		json.put("gte", DateUtil.formatDate(today, QueryServerConstants.dateFormatter));
		//排除今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString());
		assertNull(dateSpan);
		//包含今天
		dateSpan = QueryToESParserUtil.getDateSpan(json.toString(),false);
		assertEquals(DateUtil.getTodayStr(QueryServerConstants.dateFormatter), dateSpan.getStartDate());		
	}
	
}
