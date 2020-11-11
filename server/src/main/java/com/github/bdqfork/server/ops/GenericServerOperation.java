package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.exception.JRedisException;
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
import java.util.Map;

/**
 * @author bdq
 * @since 2020/11/11
 */
public class GenericServerOperation extends AbstractServerOperation {
    private static final Logger log = LoggerFactory.getLogger(GenericServerOperation.class);

    private final Map<String, Class<?>> operations = new HashMap<>();
    private final Map<String, ServerOperation> operationInstances = new HashMap<>();

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

        Class<?>[] parameterTypes = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class[]::new);

        ServerOperation serverOperation = operationInstances.get(cmd);

        try {
            Method method = ReflectUtils.getMethod(operationClass, cmd, parameterTypes);
            return (LiteralWrapper) method.invoke(serverOperation, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JRedisException(e);
        }
    }

}
