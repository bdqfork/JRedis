package com.github.bdqfork.core;

import java.io.IOException;

/**
 * 启动入口
 *
 * @author bdq
 * @since 2020/09/21
 */
public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        Server server;
        if (args.length == 3){
            String profilePath = args[2];
            server = new Server(host, port, profilePath);
        } else {
            server = new Server(host, port);
        }
        server.start();
    }
}
