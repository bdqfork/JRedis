package com.github.bdqfork.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Scanner;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class InputCommandHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("connection dropped");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println(msg);
        Scanner scanner = new Scanner(System.in);
        ctx.writeAndFlush(scanner.nextLine());
    }
}
