package com.github.bdqfork.client.util;

import org.junit.Test;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class RespUtilsTest {
    @Test
    public void parseStringTest() {
        String command = "set a 1";
        System.out.println(RespUtils.parseString(command));
    }

    @Test
    public void parseResponse() {
        String response1 = "*4\r\n$3\r\nfoo\r\n$3\r\nbar\r\n$5\r\nHello\r\n$5\r\nworld\r\n";
        String response2 = "+OK\r\n";
        System.out.println(RespUtils.parseResponse(response1));
        System.out.println(RespUtils.parseResponse(response2));
    }
}
