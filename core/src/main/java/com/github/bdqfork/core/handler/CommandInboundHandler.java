package com.github.bdqfork.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.beans.binding.ObjectBinding;

import java.util.List;

/**
 * 接收解码后的命令，进行处理的Handler
 *
 * @author Trey
 * @since 2020/10/22
 */

public class CommandInboundHandler extends SimpleChannelInboundHandler<List<Object>> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<Object> msg) throws Exception {
        //todo 命令执行
        System.out.println(msg.get(0));
    }
}
