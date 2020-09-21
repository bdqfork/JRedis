package com.github.bdqfork.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/9/20
 */
public class DataBase {
    private final Map<String, Object> dictMap;
    private final Map<String, Long> expireMap;

    public DataBase() {
        dictMap = new ConcurrentHashMap<>();
        expireMap = new ConcurrentHashMap<>();
    }

    public Map<String, Object> getDictMap() {
        return dictMap;
    }

    public Map<String, Long> getExpireMap() {
        return expireMap;
    }
}
