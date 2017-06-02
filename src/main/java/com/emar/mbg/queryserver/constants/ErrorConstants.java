package com.emar.mbg.queryserver.constants;

/**
 * 用于记录错误码的常量类
 * @author caizhenyu
 *
 */
public class ErrorConstants {
	public enum ERROR_CODE {
		OK(0),NO_DATA(1001),TIME_OUT(1002),INVALID_URL(1003),PARAM_ERROR(1004),PARA_LOSS(1005),UNKOWN(9999);
		private int code;
		private ERROR_CODE(int code){
			this.code = code;
		}
		public int getCode() {
			return this.code;
		}
	}
}
