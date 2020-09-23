package com.github.bdqfork.core.serializtion;

/**
 * @author bdq
 * @since 2020/09/23
 */
public class SerializerFactory {

    public static Serializer getSerializer(String serializerType) {
        return new JdkSerializer();
    }

}
