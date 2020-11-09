package com.github.bdqfork.client;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.protocol.EntryWrapper;

import java.util.concurrent.ExecutionException;

/**
 * @author bdq
 * @since 2020/11/9
 */
class StringCommand {
    private int datebaseId;
    private NettyChannel nettyChannel;

    public StringCommand(int datebaseId, NettyChannel nettyChannel) {
        this.datebaseId = datebaseId;
        this.nettyChannel = nettyChannel;
    }

    public Object get(String key) {
        // todo: 构造命令
        CommandContext commandContext = new CommandContext(datebaseId, "", null);
        CommandFuture commandFuture = commandContext.getResultFutrue();
        commandContext.setResultFutrue(commandFuture);

        String cmd = encode(commandContext);

        nettyChannel.send(cmd);

        try {
            EntryWrapper entryWrapper = (EntryWrapper) commandFuture.get();
            return entryWrapper.toPlain();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    private String encode(CommandContext commandContext) {
        return null;
    }
}
