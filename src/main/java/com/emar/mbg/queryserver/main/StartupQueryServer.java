package com.emar.mbg.queryserver.main;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emar.mbg.queryserver.actions.AbstractQueryAction;
import com.emar.mbg.queryserver.actions.DefaultAction;
import com.emar.mbg.queryserver.actions.SimpleQueryAction;
import com.emar.mbg.queryserver.actions.GetFlowDomainReportAction;
import com.emar.mbg.queryserver.actions.GetProjectInfoAction;
import com.emar.mbg.queryserver.handles.Dispatcher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 整个项目的启动类。该启动类将加载所有的action
 * @author caizhenyu
 *
 */
public class StartupQueryServer {
	private static Logger logger = LoggerFactory.getLogger(StartupQueryServer.class);
	
	private void launchServer(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()/2+1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()/2+1);
		
		try {
			//服务启动
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(this.new ChildChannelHandler())
					.option(ChannelOption.SO_BACKLOG, 1024)
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_KEEPALIVE, true);
			Channel ch = b.bind(new InetSocketAddress(port)).sync().channel();
			logger.info("httpserver started on port[" + port+ "]");
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		} finally {
			//服务关闭
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String [] args) throws InterruptedException {
		int port = 9000;
		try{
			if(args.length>0) {
				port = Integer.parseInt(args[0]);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		new StartupQueryServer().launchServer(port);
		
		
	}
	
	 class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
	        @Override
	        protected void initChannel(SocketChannel arg0) throws Exception {
	        	logger.info("query server initChannel..");
	        	
	            ChannelPipeline p = arg0.pipeline();
	            //监控线程空闲事件
	            p.addLast("idleStateHandler", new IdleStateHandler(60, 60, 30));
	            //将httprequest和httpresponse处理合并成一个
	    		p.addLast("ServiceDecoder", new HttpServerCodec());
	    		//控制http消息的组合，参数表示支持的内容最大长度
	    		p.addLast("httpAggregator",new HttpObjectAggregator(1024));
	    		
	    		Dispatcher dispatcher = new Dispatcher();
	    		
	    		Map<String, Class<? extends AbstractQueryAction>> apiMappings = new HashMap<String, Class<? extends AbstractQueryAction>>();
	    		
	    		apiMappings.put("get_project", GetProjectInfoAction.class);
	    		apiMappings.put("default", DefaultAction.class);
	    		apiMappings.put("get_flow_domain_report", GetFlowDomainReportAction.class);
	    		apiMappings.put("get_flow_adslot_report", SimpleQueryAction.class);
	    		dispatcher.setApiMappings(apiMappings);
	    		
	    		p.addLast("dispatcher",dispatcher);
	        }
	    }

}
