package com.github.bdqfork.core.util;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RespUtilsTest {

    @Test
    public void parserArray() {
        List<String> results = RespUtils.parserArray("*3\r\n+OK\r\n$3\r\nfoo\r\n$3\r\nbar\r\n");
        assertArrayEquals(new String[]{"OK", "foo", "bar"}, results.toArray());
    }

}