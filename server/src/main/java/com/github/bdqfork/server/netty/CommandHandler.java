package com.github.bdqfork.server.netty;

import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.Session;
import com.github.bdqfork.core.SessionHolder;
import com.github.bdqfork.core.protocol.EntryWrapper;
import com.github.bdqfork.server.Dispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
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
    private final Dispatcher dispatcher;

    public CommandHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        EntryWrapper entryWrapper = (EntryWrapper) msg;
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        CommandContext context = parse((InetSocketAddress) socketAddress, entryWrapper);
        CommandFuture future = dispatcher.dispatch(context);
        EntryWrapper result = (EntryWrapper) future.get();
        byte[] resp = result.encode().getBytes();
        ctx.writeAndFlush(resp);
    }

    private CommandContext parse(InetSocketAddress socketAddress, EntryWrapper command) {
        Session session = SessionHolder.getSession(socketAddress.getHostName(),socketAddress.getPort());
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