package com.emar.mbg.queryserver.handles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.emar.mbg.queryserver.actions.AbstractQueryAction;
import com.emar.mbg.queryserver.actions.DefaultAction;
import com.emar.mbg.queryserver.utils.UrlUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 用来处理分发请求的分发类
 * @author caizhenyu
 *
 */
public class Dispatcher extends ChannelInboundHandlerAdapter {
	private Map<String,Class<? extends AbstractQueryAction>> apiMappings = new HashMap<String,Class<? extends AbstractQueryAction>>();
	
	public Map<String, Class<? extends AbstractQueryAction>> getApiMappings() {
		return apiMappings;
	}

	public void setApiMappings(Map<String, Class<? extends AbstractQueryAction>> apiMappings) {
		this.apiMappings = apiMappings;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
        	FullHttpRequest request = (FullHttpRequest) msg;
        	String url = request.getUri();
        	System.out.println(url);
        	ExecutorService executorService =  Executors.newFixedThreadPool(10);
        	String apiName = UrlUtil.getApiName(url);
        	
        	AbstractQueryAction executor = null;
        	if (this.apiMappings.containsKey(apiName)) {
        		
        		executor = this.apiMappings.get(apiName).newInstance();
        		
        	}else{
        		executor = new DefaultAction();
        	}
        	executor.setCtx(ctx);
    		executor.setMsg(request);
    		executorService.submit(executor);
        	
        }
    }
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		
	}
}
