package com.emar.mbg.queryserver.exceptions;

import com.emar.mbg.queryserver.constants.ErrorConstants;

/**
 * 用于描述QueryServer异常的实体类
 * 包含了说明和错误码
 * 错误码参考{@link com.emar.mbg.queryserver.constants.ErrorConstants.ERROR_CODE }
 * @author caizhenyu
 *
 */
public class QueryServerException extends Exception {
	
	private static final long serialVersionUID = 5921541094643427227L;
	private String description;
	private int errorCode;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public void setErrorCode(ErrorConstants.ERROR_CODE errorCode) {
		this.errorCode = errorCode.getCode();
	}
	
	/**
	 * 默认构造函数
	 */
	public QueryServerException() {
		super();
		this.setErrorCode(ErrorConstants.ERROR_CODE.UNKOWN);
		this.setDescription("未知错误");
	}
	
}
