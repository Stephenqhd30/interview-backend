package com.stephen.interview.config.websocket;

import com.stephen.interview.config.websocket.condition.WebSocketCondition;
import com.stephen.interview.config.websocket.handler.TextWebSocketFrameHandler;
import com.stephen.interview.config.websocket.properties.WebSocketProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Netty WebSocket 服务器配置类。
 * 负责初始化 WebSocket 服务器，设置相关处理器，以及启动和关闭服务器。
 * <p>
 * WebSocket协议通过Http协议升级连接为WebSocket协议，保持长连接。
 * 本类根据传入的WebSocket配置，启动服务器并提供WebSocket服务。
 *
 * @author stephen qiu
 */
@Slf4j
@Configuration
@Conditional(WebSocketCondition.class)
public class NettyWebSocketServer {
	
	// 线程组：用于接收客户端连接请求的Boss线程组
	private EventLoopGroup bossGroup = null;
	
	// 线程组：用于处理客户端请求的Worker线程组
	private EventLoopGroup workerGroup = null;
	
	/**
	 * 构造函数：初始化WebSocket服务器的必要参数。
	 * 根据传入的配置，判断端口是否合法，并初始化线程组。
	 * 配置并启动Netty WebSocket服务器。
	 *
	 * @param webSocketProperties 配置WebSocket的相关属性（如端口、线程数等）
	 */
	public NettyWebSocketServer(WebSocketProperties webSocketProperties) {
		// 校验端口号是否合法
		if (webSocketProperties.getPort() > 65535 || webSocketProperties.getPort() < 0) {
			log.info("配置的WebSocket端口[{}]无效，使用默认端口39999", webSocketProperties.getPort());
			webSocketProperties.setPort(39999);
		}
		
		// 初始化Boss和Worker线程组
		bossGroup = new NioEventLoopGroup(webSocketProperties.getBossThread());
		workerGroup = new NioEventLoopGroup(webSocketProperties.getWorkerThread());
		
		// 创建服务器启动引导对象
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup)
				// 设置服务器端通道类型
				.channel(NioServerSocketChannel.class)
				// 设置TCP连接的最大等待队列长度
				.option(ChannelOption.SO_BACKLOG, 128)
				// 设置TCP连接保持活动状态
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				// 配置bossGroup的处理器
				.handler(new ChannelInitializer<NioServerSocketChannel>() {
					@Override
					protected void initChannel(NioServerSocketChannel nioServerSocketChannel) {
						// 添加日志处理器，记录接收的连接和请求
						nioServerSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
					}
				})
				// 配置workerGroup的处理器
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel nioSocketChannel) {
						ChannelPipeline pipeline = nioSocketChannel.pipeline();
						
						// 添加HTTP编解码器，用于WebSocket协议的升级过程
						pipeline.addLast(new HttpServerCodec());
						
						// 添加ChunkedWriteHandler，用于大文件传输
						pipeline.addLast(new ChunkedWriteHandler());
                        
                        /*
                        HttpObjectAggregator用于聚合HTTP请求的多个块，以便构建完整的HTTP请求。
                        maxContentLength设定单次聚合的最大内容长度。
                        */
						pipeline.addLast(new HttpObjectAggregator(8192));
                        
                        /*
                        WebSocketServerProtocolHandler用于将HTTP协议升级为WebSocket协议，
                        并且支持保持WebSocket长连接。它会自动处理WebSocket握手过程。
                        升级路径配置为“/websocket”，即客户端必须以ws://localhost:39999/websocket格式连接。
                        */
						pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
						
						// 添加自定义的WebSocket数据处理器，处理具体的消息逻辑
						pipeline.addLast(new TextWebSocketFrameHandler());
					}
				});
		
		// 异步绑定端口并启动服务器
		ChannelFuture bindFuture = serverBootstrap.bind(webSocketProperties.getPort());
		
		// 监听绑定端口后的操作结果
		bindFuture.addListener((ChannelFutureListener) channelFuture -> {
			if (channelFuture.isSuccess()) {
				log.info("WebSocket 服务器启动成功，监听端口：{}", webSocketProperties.getPort());
			} else {
				log.error("WebSocket 服务器启动失败：{}", channelFuture.cause().getMessage());
			}
		});
		
		// 异步监听服务器关闭事件
		ChannelFuture closeFuture = bindFuture.channel().closeFuture();
		closeFuture.addListener((ChannelFutureListener) channelFuture -> {
			if (channelFuture.isSuccess()) {
				log.info("WebSocket 服务器正在关闭...");
			} else {
				log.error("WebSocket 服务器关闭失败：{}", channelFuture.cause().getMessage());
			}
		});
	}
	
	/**
	 * 配置一个Spring Bean以便启动Netty WebSocket服务器，并在应用关闭时销毁。
	 *
	 * @return 返回当前的NettyWebSocketServer实例
	 */
	@Bean(destroyMethod = "destroy")
	protected NettyWebSocketServer startServer0() {
		return this;
	}
	
	/**
	 * 关闭WebSocket服务器，并优雅地释放资源。
	 */
	public void destroy() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		log.info("WebSocket 服务器已关闭");
	}
}
