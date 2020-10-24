package com.github.bdqfork.core.handler.codec;

import org.junit.Test;

import java.util.ArrayList;

/**
 * @author Trey
 * @since 2020/10/24
 */

public class RESPDecoderTest {
    @Test
    public void decodeTest() throws Exception {
        RESPDecoder respDecoder = new RESPDecoder();
        String msg = "*3\r\n$3\r\nset\r\n$5\r\nmykey\r\n$7\r\nmyvalue";
        ArrayList<Object> out = new ArrayList<>();
        respDecoder.decode(null,msg,out);
        String[] strings = (String[]) out.get(0);
        System.out.println(strings[0] + " " + strings[1] + " " + strings[2]);
    }
}
