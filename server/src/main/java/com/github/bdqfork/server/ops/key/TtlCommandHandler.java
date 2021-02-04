package com.github.bdqfork.server.ops.key;

import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.QueryCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

public class TtlCommandHandler extends AbstractCommandHandler {
    public static final int ARGS_NUM = 1;

    public TtlCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    public boolean support(String cmd) {
        return "ttl".equals(cmd);
    }

    @Override
    public boolean supportArgs(Object[] args) {
        return args.length == ARGS_NUM;
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
                Long ttl = transactionManager.getDatabaseManager().ttl(databaseId, getKey());
                if (ttl == null) {
                    ttl = -1L;
                }
                return ttl;
            }
        };
    }

}
