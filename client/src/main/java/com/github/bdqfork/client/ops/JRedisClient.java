package com.github.bdqfork.client.ops;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.operation.Operation;
import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.serializtion.JdkSerializer;
import com.github.bdqfork.core.serializtion.Serializer;

/**
 * @author bdq
 * @since 2020/11/4
 */
public class JRedisClient {
    private final BlockingQueue<OperationContext> queue = new ArrayBlockingQueue<>(1024);
    private final String host;
    private final Integer port;
    private NettyChannel nettyChannel;
    private Serializer serializer;
    private Operation operation;

    public JRedisClient(String host, Integer port, int databaseId) {
        this.host = host;
        this.port = port;
        this.serializer = new JdkSerializer();
        this.operation = new DefaultOperation(databaseId, nettyChannel, queue);
    }

    public void connect() {
        nettyChannel = new NettyChannel(host, port, queue);
        nettyChannel.open();
    }

    public synchronized ValueOperation opsForValue() {
        return new DefaultValueOperation(operation, serializer);
    }

    public synchronized KeyOperation opsForKey() {
        return new DefaultKeyOperation(operation);
    }

    public synchronized Operation ops() {
        return operation;
    }

    public void close() {
        nettyChannel.close();
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

}
