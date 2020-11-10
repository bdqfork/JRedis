package com.github.bdqfork.client.gui;

import com.github.bdqfork.client.command.JRedisClient;
import com.github.bdqfork.client.command.ValueOperations;

import java.util.Scanner;

/**
 * @author bdq
 * @since 2020/11/10
 */
public class CommandLineCli {
    private static final String PREFIX_FORMAT = "%s:%d>";

    public void run(String hot, Integer port, int databasedId) {
        JRedisClient jRedisClient = new JRedisClient(hot, port, databasedId);
        jRedisClient.connect();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.printf(PREFIX_FORMAT, hot, port);
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
