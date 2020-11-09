package com.github.bdqfork.client;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.protocol.EntryWrapper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

/**
 * @author bdq
 * @since 2020/11/9
 */
class ValueOperations {
    private int datebaseId;
    private NettyChannel nettyChannel;
    private BlockingQueue<CommandContext> queue;

    public ValueOperations(int datebaseId, NettyChannel nettyChannel, BlockingQueue<CommandContext> queue) {
        this.datebaseId = datebaseId;
        this.nettyChannel = nettyChannel;
        this.queue = queue;
    }

    public Object get(String key) {
        // todo: 构造命令
        CommandContext commandContext = new CommandContext(datebaseId, "", null);
        CommandFuture commandFuture = commandContext.getResultFutrue();
        commandContext.setResultFutrue(commandFuture);

        try {
            queue.put(commandContext);
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }

        String cmd = encode(commandContext);

        nettyChannel.send(cmd);

        try {
            EntryWrapper entryWrapper = (EntryWrapper) commandFuture.get();
            return entryWrapper.toPlain();
        } catch (InterruptedException | ExecutionException e) {
            throw new JRedisException(e);
        }
    }

    private String encode(CommandContext commandContext) {
        return null;
    }
}
