package com.github.bdqfork.client.gui;

import com.github.bdqfork.core.exception.IllegalCommandException;
import com.github.bdqfork.client.ops.JRedisClient;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.operation.Operation;
import com.github.bdqfork.core.operation.ValueOperation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/11/11
 */
public class GenericClientOperation implements Operation {
    private final Map<String, Class<?>> operations = new HashMap<>();
    private final Map<String, Operation> operationInstances = new HashMap<>();

    public void reset(JRedisClient jRedisClient) {
        initValueOperation(jRedisClient);
    }

    private void initValueOperation(JRedisClient jRedisClient) {
        ValueOperation valueOperation = jRedisClient.OpsForValue();
        Arrays.stream(ValueOperation.class.getMethods())
                .map(Method::getName)
                .forEach(name -> {
                    operations.put(name, ValueOperation.class);
                    operationInstances.put(name, valueOperation);
                });
    }

    public Object execute(String cmd, Object[] args) throws Throwable {
        if (!operations.containsKey(cmd)) {
            throw new IllegalCommandException(String.format("Illegal command %s", cmd));
        }
        Class<?> clazz = operations.get(cmd);
        Operation instance = operationInstances.get(cmd);
        Method method;
        try {
            method = clazz.getMethod(cmd, getParameterTypes(cmd));
        } catch (NoSuchMethodException e) {
            throw new IllegalCommandException(String.format("Illegal command %s", cmd));
        }
        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new JRedisException(e);
        } catch (Throwable e) {
            throw e.getCause();
        }
    }

    private Class<?>[] getParameterTypes(String method) {
        if ("get".equals(method) || "ttl".equals(method) || "ttlAt".equals(method)) {
            return new Class[]{String.class};
        }
        //todo 添加其他命令执行参数
        if ("set".equals(method)) {
            return new Class[]{String.class, Object.class};
        }
        throw new IllegalCommandException(String.format("Illegal command %s", method));
    }
}
