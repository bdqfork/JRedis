package com.github.bdqfork.client;

import com.github.bdqfork.client.netty.NettyChannel;

/**
 * @author bdq
 * @since 2020/11/4
 */
public class JRedisClient {
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
        nettyChannel = new NettyChannel(host, port);
        nettyChannel.open();
    }

    public StringCommand getStringCommand() {
        return new StringCommand(datebaseId, nettyChannel);
    }

    public void close() {
        nettyChannel.close();;
    }
}
