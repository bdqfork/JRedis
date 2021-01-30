package com.github.bdqfork.client.ops;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.operation.Operation;
import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.protocol.LiteralWrapper;

public class DefaultOperation implements Operation {
    private int databaseId;
    private NettyChannel nettyChannel;
    private BlockingQueue<OperationContext> queue;

    public DefaultOperation(int databaseId, NettyChannel nettyChannel, BlockingQueue<OperationContext> queue) {
        this.databaseId = databaseId;
        this.nettyChannel = nettyChannel;
        this.queue = queue;
    }

    @Override
    public Object exec(String cmd, Object... args) {
        OperationContext operationContext = new OperationContext(databaseId, cmd, args);

        CommandFuture commandFuture = new CommandFuture();
        operationContext.setResultFuture(commandFuture);

        try {
            queue.put(operationContext);
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }

        String msg = operationContext.encode();

        nettyChannel.send(msg);

        LiteralWrapper<?> literalWrapper;
        try {
            literalWrapper = (LiteralWrapper<?>) commandFuture.get();
            return literalWrapper.getData();
        } catch (InterruptedException | ExecutionException e) {
            throw new JRedisException(e);
        }
    }

}
