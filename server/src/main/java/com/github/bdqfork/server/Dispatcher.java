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
        return commandContext.getResultFutrue();
    }

    public void accept() {
        Thread handler = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!destroyed) {
                    CommandContext commandContext = queue.poll();
                    handle(commandContext);
                }
                latch.countDown();
            }
        });
        handler.setName("Main-Handler");
        handler.start();
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
