package com.stephen.interview.config.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * WebSocket处理器，用于处理WebSocket协议中的文本消息。
 * 通过继承SimpleChannelInboundHandler，专门处理TextWebSocketFrame类型的数据交换。
 * <p>
 * 主要功能：
 * - 处理客户端连接/断开事件
 * - 接收并广播文本消息
 * - 异常处理
 * <p>
 * 使用Spring管理的Bean，并且借助Netty的ChannelGroup管理所有活跃的客户端连接。
 *
 * @author stephen qiu
 */
@Slf4j
@Component
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	
	// 用于格式化日期，作为消息响应中的时间戳
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	// 管理所有连接的客户端，使用ChannelGroup进行广播
	private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	/**
	 * 当有新的客户端连接时被调用。
	 * 将当前连接的Channel添加到CHANNELS集合中，并记录连接信息。
	 *
	 * @param ctx ChannelHandlerContext
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		// 将当前连接的Channel添加到ChannelGroup中，方便广播
		CHANNELS.add(ctx.channel());
		// 打印客户端连接的日志信息
		log.info("客户端与服务端已连接：{}", ctx.channel().id().asLongText());
	}
	
	/**
	 * 当接收到客户端发送的文本消息时被调用。
	 * 服务器将会广播收到的消息到所有已连接的客户端。
	 *
	 * @param ctx ChannelHandlerContext
	 * @param msg 接收到的WebSocket文本消息
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
		// 打印接收到的消息
		log.info("服务器端收到消息：{}", msg.text());
		// 向所有连接的客户端广播消息，带上当前时间戳
		CHANNELS.writeAndFlush(new TextWebSocketFrame("[" + SIMPLE_DATE_FORMAT.format(new Date()) + "] " + msg.text()));
	}
	
	/**
	 * 当客户端断开连接时被调用。
	 * 从CHANNELS中移除该客户端，并记录断开连接的信息。
	 *
	 * @param ctx ChannelHandlerContext
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		// 从ChannelGroup中移除当前断开的Channel
		CHANNELS.remove(ctx.channel());
		// 打印客户端断开连接的日志
		log.info("客户端与服务端已断开连接：{}", ctx.channel().id().asLongText());
	}
	
	/**
	 * 当发生异常时被调用。
	 * 记录异常信息，并向客户端发送错误信息，最后关闭该Channel。
	 *
	 * @param ctx   ChannelHandlerContext
	 * @param cause 发生的异常
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// 记录异常信息
		log.error("发生异常：{}", cause.getMessage());
		// 向客户端发送错误信息
		ctx.channel().writeAndFlush(new TextWebSocketFrame("Error: " + cause.getMessage()));
		// 关闭当前的Channel连接
		ctx.close();
	}
}
