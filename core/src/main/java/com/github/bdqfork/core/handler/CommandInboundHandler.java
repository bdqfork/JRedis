package com.github.bdqfork.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 接收解码后的命令，进行处理的Handler
 *
 * @author Trey
 * @since 2020/10/22
 */

public class CommandInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
