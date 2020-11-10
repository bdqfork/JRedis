package com.github.bdqfork.client;

import com.github.bdqfork.client.gui.CommandLineCli;

/**
 * 主启动类
 *
 * @author Trey
 * @since 2020/10/31
 */

public class Application {

    public static void main(String[] args) {
        String host;
        int port;
        if (args.length == 0) {
            host = "127.0.0.1";
            port = 7000;
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        CommandLineCli commandLineCli = new CommandLineCli();
        commandLineCli.run(host, port, 0);
    }
}
