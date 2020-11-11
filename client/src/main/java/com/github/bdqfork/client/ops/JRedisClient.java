package com.github.bdqfork.client.ops;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.operation.ValueOperation;
import com.github.bdqfork.core.proxy.javassist.Proxy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author bdq
 * @since 2020/11/4
 */
public class JRedisClient {
    private final BlockingQueue<OperationContext> queue = new ArrayBlockingQueue<>(256);
    private final String host;
    private final Integer port;
    private int datebaseId;
    private NettyChannel nettyChannel;

    public JRedisClient(String host, Integer port, int datebaseId) {
        this.host = host;
        this.port = port;
        this.datebaseId = datebaseId;
    }

    public void connect() {
        nettyChannel = new NettyChannel(host, port, queue);
        nettyChannel.open();
    }

    public synchronized ValueOperation OpsForValue() {
        return (ValueOperation) Proxy.newProxyInstance(ValueOperation.class.getClassLoader(),
                new Class[]{ValueOperation.class}, new OperationHandler(datebaseId, nettyChannel, queue));
    }

    public void close() {
        nettyChannel.close();
    }

    public void setDatebaseId(int datebaseId) {
        this.datebaseId = datebaseId;
    }
}
