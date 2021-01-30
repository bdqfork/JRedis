package com.github.bdqfork.server.ops;

import java.util.HashMap;
import java.util.Map;

import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.server.transaction.TransactionManager;

public class ValueCommandHandler implements CommandHandler {
    private Map<String, CommandHandler> handlers = new HashMap<>();

    public ValueCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        // TODO: 添加子handler
    }

    @Override
    public Object handle(String cmd, Object... args) {
        if (support(cmd)) {
            return handlers.get(cmd).handle(cmd, args);
        }
        throw new JRedisException(String.format("Unsupport command %s", cmd));
    }

    @Override
    public boolean support(String cmd) {
        return handlers.containsKey(cmd);
    }

}