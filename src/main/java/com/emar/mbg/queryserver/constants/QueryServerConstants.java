package com.emar.mbg.queryserver.constants;
/**
 * 用于记录系统支持的常量的常量类
 * @author caizhenyu
 *
 */
public class QueryServerConstants {
	
	/**
	 * 用于记录系统支持的变量类型的常量类
	 * @author caizhenyu
	 *
	 */
	public enum ValueType {
		STRING(1),INT(2),LONG(3),DOUBLE(4),DATE(5);
		private int type;
		private ValueType(int type) {
			this.type = type;
		}
		public int getType() {
			return this.type;
		}
	}
	
	/**
	 * 用于记录系统支持的变量依赖的常量类
	 * @author caizhenyu
	 *
	 */
	public enum CacheDependency {
		HARDLY(0),PARTLY(1),FULLY(2);
		private int dependency;
		private CacheDependency(int value) {
			this.dependency = value;
		}
		public int getDependency() {
			return this.dependency;
		}
	}
	
	/**
	 * 用于记录排序依赖的常量
	 * @author caizhenyu
	 *
	 */
	public enum SortType {
		ASC(0),DESC(1);
		private int type;
		private SortType(int type) {
			this.type = type;
		}
		public int getType() {
			return this.type;
		}
	}
	
	public static final String dateFormatter = "yyyy-MM-dd";
	public static final String dateFormatter_old_version = "yyyyMMdd";
	
	public static final int default_page_size = 50;
	public static final int min_page_size = 1;
	public static final int max_page_size = 500;
}
