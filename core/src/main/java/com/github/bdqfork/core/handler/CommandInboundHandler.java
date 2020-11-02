package com.github.bdqfork.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * 接收解码后的命令，进行处理的Handler
 *
 * @author Trey
 * @since 2020/10/22
 */

public class CommandInboundHandler extends SimpleChannelInboundHandler<List<Object>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<Object> msg) throws Exception {
        //todo 命令执行;
        msg.forEach(System.out::println);
        ctx.writeAndFlush("+OK\r\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("+Connect Ok!\r\n");
    }
}
