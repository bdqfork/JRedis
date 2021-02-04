package com.github.bdqfork.server.ops.value;

import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.UpdateCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

/**
 * @author Trey
 * @since 2021/2/1
 */

public class SetCommandHandler extends AbstractCommandHandler {

    public SetCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
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
                transactionManager.getDatabaseManager().saveOrUpdate(databaseId, getKey(), args[1], -1L);
                return null;
            }
        };
    }

    @Override
    public boolean support(String cmd) {
        return "set".equals(cmd);
    }
}
