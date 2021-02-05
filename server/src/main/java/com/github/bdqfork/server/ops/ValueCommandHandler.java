package com.github.bdqfork.server.ops;

import java.util.HashMap;
import java.util.Map;

import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.server.ops.value.*;
import com.github.bdqfork.server.transaction.TransactionManager;

public class ValueCommandHandler implements CommandHandler {
    private Map<String, CommandHandler> handlers = new HashMap<>();

    public ValueCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        handlers.put("get", new GetCommandHandler(databaseId, transactionManager));
        handlers.put("set", new SetCommandHandler(databaseId, transactionManager));
        handlers.put("setex", new SetexCommandHandler(databaseId, transactionManager));
        handlers.put("setpx", new SetpxCommandHandler(databaseId, transactionManager));
        handlers.put("setnx", new SetnxCommandHandler(databaseId, transactionManager));
        handlers.put("setxx", new SetxxCommandHandler(databaseId, transactionManager));
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

    @Override
    public boolean supportArgs(Object[] args) {
        return true;
    }

}
