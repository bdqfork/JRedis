package com.github.bdqfork.client.ops;

import java.util.concurrent.TimeUnit;

import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.exception.SerializeException;
import com.github.bdqfork.core.operation.Operation;
import com.github.bdqfork.core.serializtion.Serializer;

public class DefaultValueOperation implements ValueOperation {
    private final Operation operation;
    private Serializer serializer;

    public DefaultValueOperation(Operation operation, Serializer serializer) {
        this.operation = operation;
        this.serializer = serializer;
    }

    @Override
    public void set(String key, Object value) {
        operation.exec("set", key, value);
    }

    @Override
    public void set(String key, Object value, long expire, TimeUnit timeUnit) {
        operation.exec("set", key, value, timeUnit.toMillis(expire));
    }

    @Override
    public void setex(String key, Object value, long expire) {
        operation.exec("setex", key, value, expire);
    }

    @Override
    public void setpx(String key, Object value, long expire) {
        operation.exec("setpx", key, value, expire);
    }

    @Override
    public boolean setnx(String key, Object value) {
        byte[] result = (byte[]) operation.exec("setnx", key, value);
        return (boolean) deserialize(result);
    }

    @Override
    public boolean setxx(String key, Object value) {
        byte[] result = (byte[]) operation.exec("setxx", key, value);
        return (boolean) deserialize(result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        byte[] result = (byte[]) operation.exec("get", key);
        return (T) deserialize(result);
    }

    private Object deserialize(byte[] bytes) {
        try {
            return serializer.deserialize(bytes, Object.class);
        } catch (SerializeException e) {
            throw new JRedisException(e);
        }
    }

}
