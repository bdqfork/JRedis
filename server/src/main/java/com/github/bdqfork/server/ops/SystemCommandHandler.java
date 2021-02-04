package com.github.bdqfork.server.ops;

import java.util.HashMap;
import java.util.Map;

import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.server.ops.system.RewriteCommandHandler;
import com.github.bdqfork.server.transaction.TransactionManager;

public class SystemCommandHandler implements CommandHandler {

    private Map<String, CommandHandler> handlers = new HashMap<>();

    public SystemCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        handlers.put("rewrite", new RewriteCommandHandler(databaseId, transactionManager));
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
        return args.length == 0;
    }

}
