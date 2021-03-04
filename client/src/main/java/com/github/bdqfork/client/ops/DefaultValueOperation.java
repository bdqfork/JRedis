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
        try {
            operation.exec("set", key, serializer.serialize(value));
        } catch (SerializeException e) {
            throw new JRedisException(e);
        }
    }

    @Override
    public void set(String key, Object value, long expire, TimeUnit timeUnit) {
        try {
            operation.exec("set", key, serializer.serialize(value), timeUnit.toMillis(expire));
        } catch (SerializeException e) {
            throw new JRedisException(e);
        }
    }

    @Override
    public void setex(String key, Object value, long expire) {
        try {
            operation.exec("setex", key, serializer.serialize(value), expire);
        } catch (SerializeException e) {
            throw new JRedisException(e);
        }
    }

    @Override
    public void setpx(String key, Object value, long expire) {
        try {
            operation.exec("setpx", key, serializer.serialize(value), expire);
        } catch (SerializeException e) {
            throw new JRedisException(e);
        }
    }

    @Override
    public boolean setnx(String key, Object value) {
        long result;
        try {
            result = (long) operation.exec("setnx", key, serializer.serialize(value));
        } catch (SerializeException e) {
            throw new JRedisException(e);
        }
        return result == 1;
    }

    @Override
    public boolean setxx(String key, Object value) {
        long result;
        try {
            result = (long) operation.exec("setnx", key, serializer.serialize(value));
        } catch (SerializeException e) {
            throw new JRedisException(e);
        }
        return result == 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        byte[] result = (byte[]) operation.exec("get", key);
        if (result == null) {
            return null;
        }
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
