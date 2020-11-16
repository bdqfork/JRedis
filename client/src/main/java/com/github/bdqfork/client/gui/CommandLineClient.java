package com.github.bdqfork.client.gui;

import com.github.bdqfork.core.exception.IllegalCommandException;
import com.github.bdqfork.client.ops.JRedisClient;
import com.github.bdqfork.core.exception.JRedisException;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author bdq
 * @since 2020/11/10
 */
public class CommandLineClient {
    private static final String PREFIX_FORMAT = "%s:%d>";
    private static final String EXIT_CMD = "exit";
    private JRedisClient jRedisClient;
    private GenericClientOperation operation;
    private String host;
    private Integer port;

    public CommandLineClient(String hot, Integer port) {
        this.host = hot;
        this.port = port;
        this.jRedisClient = new JRedisClient(host, port, 0);
        this.operation = new GenericClientOperation();
    }

    public void run() {
        jRedisClient.connect();
        operation.reset(jRedisClient);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.printf(PREFIX_FORMAT, host, port);
            String line = scanner.nextLine();
            if (EXIT_CMD.equals(line)) {
                break;
            }
            String[] lits = line.split(" ");
            if (lits.length == 0) {
                continue;
            }
            String cmd = getCmd(lits);
            Object[] args = getArgs(cmd, lits);

            Object result = null;
            try {
                result = operation.execute(cmd, args);
            } catch (IllegalCommandException e) {
                System.out.println(e.getMessage());
                continue;
            } catch (JRedisException e) {
                System.out.println("Error " + e.getMessage());
                System.exit(0);
            } catch (Throwable e) {
                System.out.println("Error " + e.getMessage());
                continue;
            }

            if (result == null) {
                if ("get".equals(cmd)) {
                    System.out.println("nil");
                } else {
                    System.out.println("OK");
                }
            } else {
                System.out.println(result);
            }
        }
    }

    private Object[] getArgs(String cmd, String[] lits) {
        // todo: 需要进一步将数据进行类型转换
        Object[] objs = Arrays.stream(lits).skip(1).toArray();
        if ("set".equals(cmd) && objs.length > 1) {
            try {
                objs[1] = Long.parseLong((String) objs[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        return objs;
    }

    private String getCmd(String[] lits) {
        return lits[0];
    }

}
