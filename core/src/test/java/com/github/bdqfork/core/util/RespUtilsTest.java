package com.github.bdqfork.core.util;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RespUtilsTest {

    @Test
    public void parserArray() {
        String commands = "*6\r\n" +
                "$9\r\n" +
                "sismember\r\n" +
                ":3\r\n" +
                "*2\r\n" +
                "+readonly\r\n" +
                "+fast\r\n" +
                ":1\r\n" +
                ":2\r\n" +
                "*2\r\n" +
                "$6\r\n" +
                "msetnx\r\n" +
                ":-3\r\n";
        List<Object> results = (List<Object>) RespUtils.parserArray(commands).get("res");
        results.forEach(System.out::println);
    }

}