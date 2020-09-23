package com.github.bdqfork.core.serializtion;

import com.github.bdqfork.core.exception.FailedDeserializeException;
import com.github.bdqfork.core.exception.FailedSerializeException;

/**
 * @author bdq
 * @since 2020/09/23
 */
public interface Serializer {

    byte[] serialize(Object instance) throws FailedSerializeException;

    Object deserialize(byte[] bytes, Class<?> clazz) throws FailedDeserializeException;
}
