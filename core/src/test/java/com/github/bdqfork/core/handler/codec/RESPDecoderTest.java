package com.github.bdqfork.core.handler.codec;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Trey
 * @since 2020/10/24
 */

public class RESPDecoderTest {
    @Test
    public void decodeTest() throws Exception {
        RESPDecoder respDecoder = new RESPDecoder();
        String msg = "*3\r\n$3\r\nset\r\n$5\r\nmykey\r\n$7\r\nmyvalue\r\n";
        ArrayList<Object> out = new ArrayList<>();
        respDecoder.decode(null,msg,out);
        Map<String,Object> map = (Map<String, Object>) out.get(0);
        List<Object> list = (List<Object>) map.get("res");
        list.forEach(System.out::println);
    }
}
