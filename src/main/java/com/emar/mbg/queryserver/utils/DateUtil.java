package com.emar.mbg.queryserver.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.emar.mbg.queryserver.constants.QueryServerConstants;
import com.emar.mbg.queryserver.engine.es.DateSpan;


public class DateUtil {

	public static final String SPAN_CURR = "curr";
	public static final String SPAN_PREV = "prev";
	public static final String SPAN_WEEK = "week";
	public static final String SPAN_MONTH = "month";
	
	public static final DateSpan getDateSpan(String type) {
		DateSpan dateSpan = new DateSpan(type);
		Calendar now = Calendar.getInstance();
		String start = null, end = null;
		SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
		if (SPAN_PREV.equals(type)) {
			now.add(Calendar.DAY_OF_MONTH, -1);
			start = end = SDF.format(now.getTime());
		} else if (SPAN_WEEK.equals(type)) {
			now.add(Calendar.DAY_OF_MONTH, -1);
			end = SDF.format(now.getTime());
			now.add(Calendar.DAY_OF_MONTH, -6);
			start = SDF.format(now.getTime());
		} else if (SPAN_MONTH.equals(type)) {
			int day = now.get(Calendar.DAY_OF_MONTH);
			if (day != 1) {
				now.add(Calendar.DAY_OF_MONTH, -1);
			}
			end = SDF.format(now.getTime());
			now.set(Calendar.DAY_OF_MONTH, 1);
			start = SDF.format(now.getTime());
		} else {
			start = end = SDF.format(now.getTime());
		}
		dateSpan.setStartDate(start);
		dateSpan.setEndDate(end);
		return dateSpan;
	}
	
	public static final String getDateType(String start, String end) {
		DateSpan dateSpan = new DateSpan(start, end);
		if (isOneday(dateSpan)) {
			DateSpan currSpan = getDateSpan("curr");
			if (dateSpan.isEqual(currSpan)) {
				return "curr";
			} else {
				DateSpan befSpan = getDateSpan("prev");
				if (dateSpan.isEqual(befSpan)) {
					return "prev";
				}
			}
		} else {
			DateSpan monSpan = getDateSpan("month");
			if (dateSpan.isEqual(monSpan)) {
				return "month";
			} else {
				DateSpan weekSpan = getDateSpan("week");
				if (dateSpan.isEqual(weekSpan)) {
					return "week";
				}
			}
		}
		return "";
	}
	
	public static final String getTodayStr (String pattern) {
		SimpleDateFormat SDF = new SimpleDateFormat(pattern);
		SDF.applyPattern(pattern);
		return SDF.format(new Date());
	}
	public static final String getTodayStr () {
		return getTodayStr(QueryServerConstants.dateFormatter);
	}
	public static final Date getToday () {
		return  DateUtil.getDate(DateUtil.getTodayStr(QueryServerConstants.dateFormatter), QueryServerConstants.dateFormatter);
	}
	
	public static final boolean isToday(DateSpan dateSpan) {
		if (isOneday(dateSpan)) {
			SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
			String start = dateSpan.getStartDate();
			String nowStr = SDF.format(new Date());
			if (nowStr.equals(start)) {
				return true;
			}
		}
		return false;
	}

	public static final boolean isYesterday(DateSpan dateSpan) {
		if (isOneday(dateSpan)) {
			DateSpan beforeSpan = getDateSpan("before");
			if (dateSpan.isEqual(beforeSpan)) {
				return true;
			}
		}
		return false;
	}
	
	public static final boolean isCurMonth(DateSpan dateSpan) {
		DateSpan monthSpan = getDateSpan("month");
		if (dateSpan.isEqual(monthSpan)) {
			return true;
		}
		return false;
	}
	
	public static final boolean isOneday(DateSpan dateSpan) {
		String start = dateSpan.getStartDate();
		String end = dateSpan.getEndDate();
		if (!start.equals(end)) {
			return false;
		}
		return true;
	}
	
	public static Date getDate(String dateStr, String pattern) {
		SimpleDateFormat SDF = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = SDF.parse(dateStr);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	public static String formatDate(Date date,String pattern) {
		SimpleDateFormat SDF = new SimpleDateFormat(pattern);
		return SDF.format(date);
	}
	public static String formatDate(Date date) {
		SimpleDateFormat SDF = new SimpleDateFormat(QueryServerConstants.dateFormatter);
		return SDF.format(date);
	}
	
	public static void main(String[] args) {

		System.out.println(getTodayStr("yyyyMMdd"));

	}
	
}
