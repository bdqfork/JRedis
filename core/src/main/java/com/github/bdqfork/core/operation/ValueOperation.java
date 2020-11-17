package com.github.bdqfork.core.operation;

import java.util.concurrent.TimeUnit;

/**
 * @author bdq
 * @since 2020/11/11
 */
public interface ValueOperation extends Operation {

    /**
     * 插入值
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 插入值，同时设置过期时间
     *
     * @param key      键
     * @param value    值
     * @param expire   过期时间
     * @param timeUnit 单位
     */
    void set(String key, Object value, long expire, TimeUnit timeUnit);

    /**
     * 插入值，同时设置过期时间
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间，单位是秒
     */
    void setex(String key, Object value, long expire);

    /**
     * 插入值，同时设置过期时间
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间，单位是毫秒
     */
    void setpx(String key, Object value, long expire);

    /**
     * 只在键不存在时，才对键进行设置操作。
     *
     * @param key   键
     * @param value 值
     * @return boolean 是否设置成功
     */
    boolean setnx(String key, Object value);

    /**
     * 只在键已经存在时，才对键进行设置操作。
     *
     * @param key   键
     * @param value 值
     * @return boolean 是否设置成功
     */
    boolean setxx(String key, Object value);

    /**
     * 获取值
     *
     * @param key 键
     * @param <T> 返回值类型
     * @return T
     */
    <T> T get(String key);

    /**
     * 删除一个键值对
     *
     * @param key 键
     */
    void del(String key);

    /**
     * 给一个键添加或者更新过期时间
     *
     * @param key      键
     * @param expire   过期时间
     * @param timeUnit 单位
     */
    void expire(String key, long expire, TimeUnit timeUnit);

    /**
     * 返回剩余过期时间
     *
     * @param key 键
     * @return Long
     */
    Long ttl(String key);

    /**
     * 返回过期时间，unix time
     *
     * @param key 键
     * @return Long
     */
    Long ttlAt(String key);
}
