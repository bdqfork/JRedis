package com.github.bdqfork.server.ops;

import java.util.HashMap;
import java.util.Map;

import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.server.ops.key.DelCommandHandler;
import com.github.bdqfork.server.ops.key.ExpireAtCommandHandler;
import com.github.bdqfork.server.ops.key.ExpireCommandHandler;
import com.github.bdqfork.server.ops.key.TtlAtCommandHandler;
import com.github.bdqfork.server.ops.key.TtlCommandHandler;
import com.github.bdqfork.server.transaction.TransactionManager;

public class KeyCommandHandler implements CommandHandler {
    private Map<String, CommandHandler> handlers = new HashMap<>();

    public KeyCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        handlers.put("del", new DelCommandHandler(databaseId, transactionManager));
        handlers.put("expire", new ExpireCommandHandler(databaseId, transactionManager));
        handlers.put("expireAt", new ExpireAtCommandHandler(databaseId, transactionManager));
        handlers.put("ttl", new TtlCommandHandler(databaseId, transactionManager));
        handlers.put("ttlAt", new TtlAtCommandHandler(databaseId, transactionManager));
    }

    @Override
    public boolean support(String cmd) {
        return handlers.containsKey(cmd);
    }

    @Override
    public boolean supportArgs(Object[] args) {
        return true;
    }

    @Override
    public Object handle(String cmd, Object... args) {
        if (support(cmd)) {
            return handlers.get(cmd).handle(cmd, args);
        }
        throw new JRedisException(String.format("Unsupport command %s", cmd));
    }

}
