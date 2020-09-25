package com.github.bdqfork.core.util;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RespUtilsTest {

    @Test
    public void parserArray() {
        String commands = "*2\r\n*3\r\n:1\r\n:2\r\n:3\r\n*2\r\n+Foo\r\n-Bar\r\n";
        List<Object> results = RespUtils.parserArray(commands);
        results.forEach(System.out::println);
    }

}