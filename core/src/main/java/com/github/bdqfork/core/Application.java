package com.github.bdqfork.core;

/**
 * 启动入口
 *
 * @author bdq
 * @since 2020/09/21
 */
public class Application {
    public static void main(String[] args) {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        Server server = new Server(host, port);
        server.start();
    }
}
