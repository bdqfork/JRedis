package com.github.bdqfork.client.util;

import org.junit.Test;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class RespUtilTest {
    @Test
    public void parseStringTest() {
        String command = "set a 1 3 2 213 11";
        System.out.println(RespUtil.parseString(command));
    }
}
