package com.github.bdqfork.server.ops.key;

import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.UpdateCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

public class ExpireAtCommandHandler extends AbstractCommandHandler {

    public ExpireAtCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    public boolean support(String cmd) {
        return "expireAt".equals(cmd);
    }

    @Override
    protected Command<?> parse(String cmd, Object[] args) {
        return new UpdateCommand<Void>() {
            @Override
            public String getKey() {
                return (String) args[0];
            }

            @Override
            public int getDatabaseId() {
                return databaseId;
            }

            @Override
            public Void execute() {
                long expire = (long) args[1];
                transactionManager.getDatabaseManager().expire(databaseId, getKey(), expire);
                return null;
            }
        };
    }

}
