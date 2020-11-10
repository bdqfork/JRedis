package com.github.bdqfork.client.command;

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
public class ValueOperations extends Operations {

    public ValueOperations(int datebaseId, NettyChannel nettyChannel, BlockingQueue<CommandContext> queue) {
        super(datebaseId, nettyChannel, queue);
    }

    public Object get(String key) {
        CommandContext commandContext = new CommandContext(datebaseId, "get", new Object[]{key});
        CommandFuture commandFuture = new CommandFuture();
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
            return entryWrapper.getData();
        } catch (InterruptedException | ExecutionException e) {
            throw new JRedisException(e);
        }
    }

}
