package com.github.bdqfork.client.util;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class RespUtils {

    /**
     * 将客户端输入命令封装成resp字符串数组
     * @param command 用户输入
     * @return resp字符串数组
     */
    public static String parseString(String command) {
        String[] array = command.split(" ");
        StringBuilder result = new StringBuilder();
        result.append("*")
                .append(array.length)
                .append("\r\n");

        for (String s : array) {
            result.append("$")
                    .append(s.length())
                    .append("\r\n")
                    .append(s)
                    .append("\r\n");
        }
        return result.toString();
    }

    /**
     * 将服务端响应解析为字符串，多条数据用"\r\n"分割
     * @param resp resp数据
     * @return 字符串
     */
    public static String parseResponse(String resp) {
        StringBuilder result = new StringBuilder();
        char[] chs = resp.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            if (chs[i] == '+' || chs[i] == ':' || chs[i] == '-') {
                i++;
                while (chs[i] != '\r') {
                    result.append(chs[i]);
                    i++;
                }
            }

            if (chs[i] == '$') {
                while (chs[i] != '\r') {
                    i++;
                }
                i += 2;
                while (chs[i] != '\r') {
                    result.append(chs[i]);
                    i++;
                }
                result.append("\r\n");
            }
            if (chs[i] == '*') {
                i++;
                while (chs[i] == '\r') {
                    i++;
                }
                i += 3;
                StringBuilder builder = new StringBuilder();
                while (i < chs.length) {
                    builder.append(chs[i]);
                    i++;
                }
                result.append(parseResponse(builder.toString()));
            }
        }
        return result.toString();
    }
}
