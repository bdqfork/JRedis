package com.github.bdqfork.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据存储，对数据进行管理，负责增删改查，同时负责淘汰过期数据
 *
 * @author bdq
 * @since 2020/9/20
 */
public class DataBase {
    private final Map<String, Object> dictMap;
    private final Map<String, Long> expireMap;

    public DataBase() {
        dictMap = new ConcurrentHashMap<>(256);
        expireMap = new ConcurrentHashMap<>(256);
    }

    /**
     * 插入数据
     *
     * @param key      键
     * @param value    值
     * @param expireAt 过期时间
     */
    public void insert(String key, Object value, Long expireAt) {
        dictMap.put(key, value);
        if (expireAt != -1) {
            expireMap.put(key, expireAt);
        }
    }

    /**
     * 删除数据
     *
     * @param key 键
     */
    public void delete(String key) {
        dictMap.remove(key);
        expireMap.remove(key);
    }

    /**
     * 更新数据
     *
     * @param key   键
     * @param value 值
     */
    public void update(String key, Object value, Long expireAt) {
        dictMap.put(key, value);
        if (expireAt != -1) {
            expireMap.put(key, expireAt);
        }
    }


    /**
     * 查询数据
     *
     * @param key 键
     */
    public Object get(String key) {
        if (ttl(key) > 0) {
            return dictMap.get(key);
        }else {
            dictMap.remove(key);
            expireMap.remove(key);
            return null;
        }
    }

    /**
     * 查询数据
     *
     * @param key 键
     */
    public Long ttl(String key) {
        return expireMap.get(key) - System.currentTimeMillis();
    }
}
