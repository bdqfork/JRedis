package com.github.bdqfork.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.operation.Operation;
import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.protocol.LiteralWrapper;
import com.github.bdqfork.server.ops.DefaultOperation;
import com.github.bdqfork.server.transaction.TransactionManager;

/**
 * @author bdq
 * @since 2020/11/6
 */
public class Dispatcher {
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile boolean destroyed;
    private BlockingQueue<OperationContext> queue;
    private TransactionManager transactionManager;
    private Map<Integer, Operation> serverOperations = new ConcurrentHashMap<>();

    public Dispatcher(TransactionManager transactionManager, BlockingQueue<OperationContext> queue) {
        this.transactionManager = transactionManager;
        this.queue = queue;
    }

    public CommandFuture dispatch(OperationContext operationContext) {
        CommandFuture commandFuture = new CommandFuture();
        operationContext.setResultFuture(commandFuture);
        try {
            queue.put(operationContext);
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }
        return operationContext.getResultFuture();
    }

    public void accept() {
        CountDownLatch startedLatch = new CountDownLatch(1);
        Thread handler = new Thread(new Runnable() {
            @Override
            public void run() {
                startedLatch.countDown();
                while (!destroyed) {
                    OperationContext operationContext;
                    try {
                        operationContext = queue.take();
                    } catch (InterruptedException e) {
                        throw new JRedisException(e);
                    }
                    handle(operationContext);
                }
                latch.countDown();
            }
        });
        handler.setName("Main-Handler");
        handler.start();
        try {
            startedLatch.await();
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }
    }

    private void handle(OperationContext operationContext) {
        int databaseId = operationContext.getDatabaseId();

        String cmd = operationContext.getCmd();
        Object[] args = operationContext.getArgs();

        CommandFuture commandFuture = operationContext.getResultFuture();
        try {
            Object result = getOperation(databaseId).exec(cmd, args);
            LiteralWrapper<?> literalWrapper = encodeResult(result);
            commandFuture.complete(literalWrapper);
        } catch (JRedisException e) {
            commandFuture.completeExceptionally(e);
        }

    }

    private LiteralWrapper<?> encodeResult(Object result) {
        if (result instanceof String) {
            return LiteralWrapper.singleWrapper((String) result);
        }
        if (result instanceof Long) {
            return LiteralWrapper.integerWrapper((Number) result);
        }
        if (result instanceof Boolean) {
            return LiteralWrapper.integerWrapper((Boolean) result ? 1L : 0L);
        }
        if (result instanceof byte[]) {
            return LiteralWrapper.bulkWrapper((byte[]) result);
        }
        if (result instanceof List) {
            @SuppressWarnings("unchecked")
            List<LiteralWrapper<?>> items = (List<LiteralWrapper<?>>) result;
            items = items.stream().map(this::encodeResult).collect(Collectors.toList());
            return LiteralWrapper.multiWrapper(items);
        }
        return LiteralWrapper.bulkWrapper();
    }

    private Operation getOperation(int databaseId) {
        return serverOperations.computeIfAbsent(databaseId, k -> {
            return new DefaultOperation(databaseId, transactionManager);
        });
    }

    public void stop() {
        destroyed = true;
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }
    }

}
