package com.github.bdqfork.server;

import com.github.bdqfork.core.CommandContext;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.server.transaction.TransactionManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author bdq
 * @since 2020/11/6
 */
public class Dispatcher {
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile boolean destroyed;
    private BlockingQueue<CommandContext> queue;
    private TransactionManager transactionManager;

    public Dispatcher(TransactionManager transactionManager, BlockingQueue<CommandContext> queue) {
        this.transactionManager = transactionManager;
        this.queue = queue;
    }

    public CommandFuture dispatch(CommandContext commandContext) {
        try {
            queue.put(commandContext);
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }
        return commandContext.getResultFutrue();
    }

    public void accept() {
        CountDownLatch startedLatch = new CountDownLatch(1);
        Thread handler = new Thread(new Runnable() {
            @Override
            public void run() {
                startedLatch.countDown();
                while (!destroyed) {
                    CommandContext commandContext;
                    try {
                        commandContext = queue.take();
                    } catch (InterruptedException e) {
                        throw new JRedisException(e);
                    }
                    handle(commandContext);
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

    private void handle(CommandContext commandContext) {
        String cmd = commandContext.getCmd();
        Object[] args = commandContext.getArgs();

        // todo: 执行命令
        CommandFuture commandFuture = commandContext.getResultFutrue();
        commandFuture.complete("");
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
