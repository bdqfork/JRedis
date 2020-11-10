package com.github.bdqfork.client;

import com.github.bdqfork.client.netty.NettyChannel;
import com.sun.javafx.binding.StringFormatter;

import java.util.Scanner;

/**
 * 主启动类
 *
 * @author Trey
 * @since 2020/10/31
 */

public class Application {
    private static final String PREFIX_FORMAT = "%s:%d>";

    public static void main(String[] args) {
//        String host = args[0];
//        Integer port = Integer.parseInt(args[1]);
        JRedisClient jRedisClient = new JRedisClient("127.0.0.1", 7000, 0);
        jRedisClient.connect();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.printf(PREFIX_FORMAT, "127.0.0.1", 7000);
            String line = scanner.nextLine();
            if ("exit".equals(line)) {
                break;
            }
            // todo: 解析命令，这里只是简单例子，需要进行进一步封装
            String[] lits = line.split(" ");
            if ("get".equals(lits[0])) {
                ValueOperations valueOperations = jRedisClient.OpsForValue();
                Object value = valueOperations.get(lits[1]);
                if (value == null) {
                    System.out.println("nil");
                } else {
                    System.out.println(value);
                }
            }
        }
    }
}
