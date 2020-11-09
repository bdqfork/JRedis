package com.github.bdqfork.server;

/**
 * 启动入口
 *
 * @author bdq
 * @since 2020/09/21
 */
public class Application {
    public static void main(String[] args) {
        String path = args[0];
        JRedisServer jRedisServer = new JRedisServer(path);
        jRedisServer.listen();
        Runtime.getRuntime().addShutdownHook(new Thread(jRedisServer::close));
    }
}
