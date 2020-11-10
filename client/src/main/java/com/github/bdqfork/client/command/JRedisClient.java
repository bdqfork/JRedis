package com.github.bdqfork.client.command;

import com.github.bdqfork.client.command.ValueOperations;
import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author bdq
 * @since 2020/11/4
 */
public class JRedisClient {
    private final BlockingQueue<CommandContext> queue = new ArrayBlockingQueue<>(1024);
    private String host;
    private Integer port;
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

    public ValueOperations OpsForValue() {
        return new ValueOperations(datebaseId, nettyChannel, queue);
    }

    public void close() {
        nettyChannel.close();
    }
}
