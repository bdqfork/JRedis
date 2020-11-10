package com.github.bdqfork.client;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.protocol.EntryWrapper;

import java.util.ArrayList;
import java.util.List;
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

    private String encode(CommandContext commandContext) {
        EntryWrapper entryWrapper = encodeArgs(commandContext.getArgs());
        List<EntryWrapper> entryWrappers = entryWrapper.getData();

        EntryWrapper cmdEntryWrapper = EntryWrapper.singleWrapper();
        cmdEntryWrapper.setData(commandContext.getCmd());

        entryWrappers.add(0, cmdEntryWrapper);
        return entryWrapper.encode();
    }

    private EntryWrapper encodeArgs(Object[] args) {
        List<EntryWrapper> entryWrappers = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof String) {
                EntryWrapper singleWrapper = EntryWrapper.singleWrapper();
                singleWrapper.setData(arg);
                entryWrappers.add(singleWrapper);
            }
            if (arg instanceof Number) {
                EntryWrapper integerWrapper = EntryWrapper.integerWrapper();
                integerWrapper.setData(arg);
                entryWrappers.add(integerWrapper);
            }
            if (arg instanceof byte[]) {
                EntryWrapper bulkWrapper = EntryWrapper.bulkWrapper();
                bulkWrapper.setData(arg);
                entryWrappers.add(bulkWrapper);
            }
            if (arg instanceof List) {
                List<?> items = (List<?>) arg;
                entryWrappers.add(encodeArgs(items.toArray()));
            }
        }
        EntryWrapper wrapper = EntryWrapper.multiWrapper();
        wrapper.setData(entryWrappers);
        return wrapper;
    }

}
