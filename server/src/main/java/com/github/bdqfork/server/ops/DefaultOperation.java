package com.github.bdqfork.server.ops;

import java.util.ArrayList;
import java.util.List;

import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.operation.Operation;
import com.github.bdqfork.server.transaction.TransactionManager;

/**
 * @author bdq
 * @since 2020/11/11
 */
public class DefaultOperation implements Operation {
    private List<CommandHandler> handlers = new ArrayList<>();

    public DefaultOperation(Integer databaseId, TransactionManager transactionManager) {
        handlers.add(new KeyCommandHandler(databaseId, transactionManager));
        handlers.add(new ValueCommandHandler(databaseId, transactionManager));
        handlers.add(new SystemCommandHandler(databaseId, transactionManager));
    }

    @Override
    public Object exec(String cmd, Object... args) {
        for (CommandHandler handler : handlers) {
            if (handler.support(cmd)) {
                return handler.handle(cmd, args);
            }
        }
        throw new JRedisException(String.format("Unsupport command %s", cmd));
    }

}
