package com.github.bdqfork.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class Client {
    private final String host;
    private final Integer port;
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private EventLoopGroup group;

    public Client(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                    }
                });

        try {
            bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            log.warn(e.getMessage(),e);
            destroy();
        }
    }

    public void destroy() {
        try {
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
