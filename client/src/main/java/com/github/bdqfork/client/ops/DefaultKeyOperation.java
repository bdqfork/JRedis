package com.github.bdqfork.client.ops;

import com.github.bdqfork.core.operation.Operation;

public class DefaultKeyOperation implements KeyOperation {
    private final Operation operation;

    public DefaultKeyOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public void del(String key) {
        operation.exec("del", key);
    }

    @Override
    public void expire(String key, long expire) {
        operation.exec("expire", expire);
    }

    @Override
    public void expireAt(String key, long expireAt) {
        operation.exec("expireAt", expireAt);
    }

    @Override
    public Long ttl(String key) {
        return (Long) operation.exec("ttl", key);
    }

    @Override
    public Long ttlAt(String key) {
        return (Long) operation.exec("ttlAt", key);
    }

}
