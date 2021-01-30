package com.github.bdqfork.server.ops;

import com.github.bdqfork.server.transaction.OperationType;

/**
 * @author bdq
 * @since 2020/11/6
 */
public interface Command<T> {
    /**
     * 获取待操作的key
     * 
     * @return String
     */
    String getKey();

    /**
     * 获取待操作的数据库id
     * 
     * @return int
     */
    int getDatabaseId();

    /**
     * 执行命令
     */
    T execute();

    /**
     * 获取操作类型
     */
    OperationType getOperationType();
}
