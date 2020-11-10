package com.github.bdqfork.client.command;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.protocol.EntryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author bdq
 * @since 2020/11/10
 */
public class Operations {
    protected int datebaseId;
    protected NettyChannel nettyChannel;
    protected BlockingQueue<CommandContext> queue;

    public Operations(int datebaseId, NettyChannel nettyChannel, BlockingQueue<CommandContext> queue) {
        this.datebaseId = datebaseId;
        this.nettyChannel = nettyChannel;
        this.queue = queue;
    }

    protected String encode(CommandContext commandContext) {
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
