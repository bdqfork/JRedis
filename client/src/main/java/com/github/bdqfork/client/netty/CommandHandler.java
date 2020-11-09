package com.github.bdqfork.client.netty;

import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.protocol.EntryWrapper;
import com.github.bdqfork.core.protocol.StateMachine;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class CommandHandler extends SimpleChannelInboundHandler<Object> {
    private BlockingQueue<CommandContext> queue;

    public CommandHandler(BlockingQueue<CommandContext> queue) {
        this.queue = queue;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connection dropped");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        EntryWrapper entryWrapper = (EntryWrapper) msg;
        CommandContext commandContext = queue.take();
        CommandFuture commandFuture = commandContext.getResultFutrue();
        commandFuture.complete(entryWrapper);
    }
}
