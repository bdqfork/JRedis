package com.github.bdqfork.client.util;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class RespUtil {
    public static String parseString(String command) {
        String[] array = command.split(" ");
        StringBuilder respCommand = new StringBuilder();
        respCommand.append("*")
                .append(array.length)
                .append("\r\n");

        for (String s : array) {
            respCommand.append("$")
                    .append(s.length())
                    .append("\r\n")
                    .append(s)
                    .append("\r\n");
        }
        return respCommand.toString();
    }
}
