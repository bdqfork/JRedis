package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.exception.IllegalCommandException;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.operation.Operation;
import com.github.bdqfork.core.operation.ValueOperation;
import com.github.bdqfork.core.protocol.LiteralWrapper;
import com.github.bdqfork.core.util.ReflectUtils;
import com.github.bdqfork.server.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/11/11
 */
public class GenericServerOperation extends AbstractServerOperation {
    private static final Logger log = LoggerFactory.getLogger(GenericServerOperation.class);

    private final Map<String, Class<?>> operations = new HashMap<>();
    private final Map<String, Operation> operationInstances = new HashMap<>();

    private ServerValueOperation serverValueOperation;

    public GenericServerOperation(int databaseId, TransactionManager transactionManager) {
        initValueOperation(databaseId, transactionManager);
    }

    private void initValueOperation(int databaseId, TransactionManager transactionManager) {
        serverValueOperation = new ServerValueOperation();
        serverValueOperation.setDatabaseId(databaseId);
        serverValueOperation.setTransactionManager(transactionManager);
        Arrays.stream(ValueOperation.class.getMethods()).map(Method::getName).distinct().forEach(name -> {
            operations.put(name, ValueOperation.class);
            operationInstances.put(name, serverValueOperation);
        });
    }

    public LiteralWrapper execute(String cmd, Object... args) throws JRedisException {

        if (!operations.containsKey(cmd)) {
            throw new JRedisException("Illegal command");
        }

        Class<?> operationClass = operations.get(cmd);
        Operation operation = operationInstances.get(cmd);

        //todo set方法执行时，返回值为空
        try {
            Method method = ReflectUtils.getMethod(operationClass, cmd, getParameterTypes(cmd));
            Object result = method.invoke(operation, args);
            return encodeResult(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JRedisException(e);
        }
    }

    private Class<?>[] getParameterTypes(String method) {
        if ("get".equals(method) || "ttl".equals(method) || "ttlAt".equals(method)) {
            return new Class[]{String.class};
        }

        if ("set".equals(method)) {
            return new Class[]{String.class, Object.class};
        }
        throw new IllegalCommandException(String.format("Illegal command %s", method));
    }

    protected LiteralWrapper encodeResult(Object result) {
        LiteralWrapper literalWrapper = null;
        if (result instanceof String) {
            literalWrapper = LiteralWrapper.singleWrapper();
        }
        if (result instanceof Number) {
            literalWrapper = LiteralWrapper.integerWrapper();
        }
        if (result instanceof byte[]) {
            literalWrapper = LiteralWrapper.bulkWrapper();
        }
        if (result instanceof List) {
            literalWrapper = LiteralWrapper.multiWrapper();
            List<?> items = (List<?>) result;
            result = items.stream().map(this::encodeResult).collect(Collectors.toList());
        }
        if (literalWrapper == null) {
            literalWrapper = LiteralWrapper.bulkWrapper();
        }
        literalWrapper.setData(result);
        return literalWrapper;
    }

}