package com.github.bdqfork.client.netty;

import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.protocol.LiteralWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class CommandHandler extends SimpleChannelInboundHandler<Object> {
    private BlockingQueue<OperationContext> queue;

    public CommandHandler(BlockingQueue<OperationContext> queue) {
        this.queue = queue;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connection is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connection dropped");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LiteralWrapper literalWrapper = (LiteralWrapper) msg;
        if (!queue.isEmpty()) {
            OperationContext operationContext = queue.take();
            CommandFuture commandFuture = operationContext.getResultFutrue();
            commandFuture.complete(literalWrapper);
        }
    }
}
