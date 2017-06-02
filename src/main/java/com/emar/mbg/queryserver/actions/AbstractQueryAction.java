package com.emar.mbg.queryserver.actions;

import java.util.Map;

import com.emar.mbg.queryserver.constants.QueryServerConstants;
import com.emar.mbg.queryserver.dto.QueryActionDto;
import com.emar.mbg.queryserver.exceptions.QueryServerException;
import com.emar.mbg.queryserver.utils.ResultAssemblyUtil;
import com.emar.mbg.queryserver.utils.UrlUtil;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * 业务行为抽象类
 * 定义了模板方法
 * @author caizhenyu
 *
 */
public abstract class AbstractQueryAction extends Thread {
	
	protected String apiName;
	protected ChannelHandlerContext ctx;
	protected FullHttpRequest msg;
	protected FullHttpResponse response;
	protected QueryActionDto tmpDto;
	private String url;
	private Map<String,String> parameters;
	private  HttpHeaders headers;
	private String bodyContent;
	private int cacheDependency;
	
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	public FullHttpRequest getMsg() {
		return msg;
	}
	public void setMsg(FullHttpRequest msg) {
		this.msg = msg;
	}
	public FullHttpResponse getResponse() {
		return response;
	}
	public void setResponse(FullHttpResponse response) {
		this.response = response;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public HttpHeaders getHeaders() {
		return headers;
	}
	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}
	public String getBodyContent() {
		return bodyContent;
	}
	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}
	public int getCacheDependency() {
		return cacheDependency;
	}
	public void setCacheDependency(int cacheDependency) {
		this.cacheDependency = cacheDependency;
	}
	public void setCacheDependency(QueryServerConstants.CacheDependency cacheDependency) {
		this.cacheDependency = cacheDependency.getDependency();
	}
	public QueryActionDto getTmpDto() {
		return tmpDto;
	}
	public void setTmpDto(QueryActionDto tmpDto) {
		this.tmpDto = tmpDto;
	}
	/**
	 * 初始化方法
	 */
	private void init() {
		
		this.setUrl(this.msg.getUri());
		this.setParameters(UrlUtil.getParameters(this.msg.getUri()));
		this.setApiName(UrlUtil.getApiName(this.url));
		this.setBodyContent(this.msg.content().toString());
		this.setHeaders(this.msg.headers());
		this.setCacheDependency(QueryServerConstants.CacheDependency.FULLY.getDependency());
	}
	
	protected void prepare() throws Exception{
		
	}
	abstract protected QueryActionDto getDataFromCache();
	abstract protected QueryActionDto getDataFromDB();
	protected void roundOff(){
		
	}
	/**
	 * 将查询结果转化为返回内容
	 * @param dto
	 */
	private void makeQueryResponseContent(QueryActionDto dto) {
		String result = ResultAssemblyUtil.getAssembledResult(dto);
		this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(result, CharsetUtil.UTF_8));
	}
	
	/**
	 * 将异常转化为返回内容
	 * @param e 异常
	 */
	private void makeQueryResponseContent(Throwable e) {
		String result = ResultAssemblyUtil.getAssembledResult(e);
		this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(result, CharsetUtil.UTF_8));
	}
	
	/**
	 * 模板方法
	 */
	private final void doTemplateQueryAction() {
		//初始化
		init();
		//查询cache
		try {
			this.prepare();
			int dependency = this.getCacheDependency();
			QueryActionDto dto = null;
			boolean accessDBFlag = false;
			if (dependency!=QueryServerConstants.CacheDependency.HARDLY.getDependency()) {
				dto = this.getDataFromCache();
				
				if (dto == null) {
					accessDBFlag = true;
				}else {
					if (dto.getTotal_num() == 0) {
						accessDBFlag = true;
					}
				}
				
				if (dependency==QueryServerConstants.CacheDependency.PARTLY.getDependency() ) {
					//部分依赖Cache的情况
					this.setTmpDto(dto);
					accessDBFlag = true;
				}
				
			}else {
				accessDBFlag = true;
			}
			if (accessDBFlag == true) {
				//查询db
				dto = this.getDataFromDB();
			}
			
			this.makeQueryResponseContent(dto);
			
		}catch(Exception e) {
			//异常处理
			e.printStackTrace();
			this.makeQueryResponseContent(e);
		}
		this.roundOff();
	}
	
	public final void run() {
		
		this.doTemplateQueryAction();
		
		this.ctx.writeAndFlush(this.getResponse());
		this.ctx.close();
	}
	
	
}
