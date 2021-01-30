package com.github.bdqfork.server.ops.key;

import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.QueryCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

public class TtlAtCommandHandler extends AbstractCommandHandler {

    public TtlAtCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    public boolean support(String cmd) {
        return "ttlAt".equals(cmd);
    }

    @Override
    protected Command<?> parse(String cmd, Object[] args) {
        return new QueryCommand<Long>() {
            @Override
            public String getKey() {
                return (String) args[0];
            }

            @Override
            public int getDatabaseId() {
                return databaseId;
            }

            @Override
            public Long execute() {
                Long ttlAt = transactionManager.getDatabaseManager().ttlAt(databaseId, getKey());
                if (ttlAt == null) {
                    ttlAt = -1L;
                }
                return ttlAt;
            }
        };
    }

}
