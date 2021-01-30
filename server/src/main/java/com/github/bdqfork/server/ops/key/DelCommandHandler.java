package com.github.bdqfork.server.ops.key;

import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.DeleteCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

public class DelCommandHandler extends AbstractCommandHandler {

    public DelCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    public boolean support(String cmd) {
        return "del".equals(cmd);
    }

    @Override
    protected Command<?> parse(String cmd, Object[] args) {
        return new DeleteCommand<Void>() {
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
                transactionManager.getDatabaseManager().delete(databaseId, getKey());
                return null;
            }
        };
    }

}
