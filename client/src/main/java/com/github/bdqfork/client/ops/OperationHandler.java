package com.github.bdqfork.client.ops;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.protocol.LiteralWrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

/**
 * @author bdq
 * @since 2020/11/10
 */
public class OperationHandler implements InvocationHandler {
    private int datebaseId;
    private NettyChannel nettyChannel;
    private BlockingQueue<OperationContext> queue;

    public OperationHandler(int datebaseId, NettyChannel nettyChannel, BlockingQueue<OperationContext> queue) {
        this.datebaseId = datebaseId;
        this.nettyChannel = nettyChannel;
        this.queue = queue;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        OperationContext operationContext = new OperationContext(datebaseId, method.getName(), args);

        CommandFuture commandFuture = new CommandFuture();
        operationContext.setResultFutrue(commandFuture);

        try {
            queue.put(operationContext);
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }

        String cmd = encode(operationContext);

        nettyChannel.send(cmd);

        try {
            LiteralWrapper literalWrapper = (LiteralWrapper) commandFuture.get();
            return literalWrapper.getData();
        } catch (InterruptedException | ExecutionException e) {
            throw new JRedisException(e);
        }
    }

    private String encode(OperationContext operationContext) {
        LiteralWrapper literalWrapper = encodeArgs(operationContext.getArgs());

        List<LiteralWrapper> literalWrappers = literalWrapper.getData();
        LiteralWrapper cmdLiteralWrapper = LiteralWrapper.singleWrapper();
        cmdLiteralWrapper.setData(operationContext.getCmd());
        literalWrappers.add(0, cmdLiteralWrapper);

        return literalWrapper.encode();
    }

    private LiteralWrapper encodeArgs(Object[] args) {
        List<LiteralWrapper> literalWrappers = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof String) {
                LiteralWrapper singleWrapper = LiteralWrapper.singleWrapper();
                singleWrapper.setData(arg);
                literalWrappers.add(singleWrapper);
            }
            if (arg instanceof Number) {
                LiteralWrapper integerWrapper = LiteralWrapper.integerWrapper();
                integerWrapper.setData(arg);
                literalWrappers.add(integerWrapper);
            }
            if (arg instanceof byte[]) {
                LiteralWrapper bulkWrapper = LiteralWrapper.bulkWrapper();
                bulkWrapper.setData(arg);
                literalWrappers.add(bulkWrapper);
            }
            if (arg instanceof List) {
                List<?> items = (List<?>) arg;
                literalWrappers.add(encodeArgs(items.toArray()));
            }
        }
        LiteralWrapper wrapper = LiteralWrapper.multiWrapper();
        wrapper.setData(literalWrappers);
        return wrapper;
    }

}
