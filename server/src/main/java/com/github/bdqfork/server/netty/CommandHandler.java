package com.github.bdqfork.server.netty;

import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.protocol.EntryWrapper;
import com.github.bdqfork.server.Dispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 接收解码后的命令，进行处理的Handler
 *
 * @author Trey
 * @since 2020/10/22
 */

public class CommandHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    private Long timeout;
    private Dispatcher dispatcher;

    public CommandHandler(Long timeout, Dispatcher dispatcher) {
        this.timeout = timeout;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        EntryWrapper entryWrapper = (EntryWrapper) msg;
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        CommandContext context = parse(socketAddress, entryWrapper);
        CommandFuture future = dispatcher.dispatch(context);
        EntryWrapper result;
        try {
            result = (EntryWrapper) future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            result = EntryWrapper.errorWrapper();
            result.setData("Timeout");
        }
        byte[] resp = result.encode().getBytes();
        ctx.writeAndFlush(resp);
    }

    private CommandContext parse(SocketAddress socketAddress, EntryWrapper command) {
        // todo: 解析cmd和args
        return null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        EntryWrapper entryWrapper = EntryWrapper.singleWrapper();
        entryWrapper.setData("Connect Ok!");
        ctx.writeAndFlush(entryWrapper.encode());
    }

}
