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
     * @param key   键
     * @param value 值
     */
    public void insert(String key, Object value) {

    }

    /**
     * 插入数据
     *
     * @param key      键
     * @param value    值
     * @param expireAt 过期时间
     */
    public void insert(String key, Object value, Long expireAt) {

    }

    /**
     * 删除数据
     *
     * @param key 键
     */
    public void delete(String key) {

    }

    /**
     * 更新数据
     *
     * @param key   键
     * @param value 值
     */
    public void update(String key, Object value) {

    }

    /**
     * 查询数据
     *
     * @param key 键
     */
    public Object get(String key) {
        return null;
    }
}
