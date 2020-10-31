package com.github.bdqfork.client;

/**
 * 主启动类
 *
 * @author Trey
 * @since 2020/10/31
 */

public class Application {
    public static void main(String[] args) {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        Client client = new Client(host, port);;
        client.connect();
    }
}
