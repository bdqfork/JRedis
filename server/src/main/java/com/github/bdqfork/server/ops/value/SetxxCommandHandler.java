package com.github.bdqfork.server.ops.value;

import com.github.bdqfork.server.database.DatabaseManager;
import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.UpdateCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

/**
 * 只在键已经存在时，才对键进行设置操作。
 *
 * @author Trey
 * @since 2021/2/4
 */

public class SetxxCommandHandler extends AbstractCommandHandler {

    public SetxxCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    protected Command<?> parse(String cmd, Object[] args) {
        return new UpdateCommand<Boolean>() {
            @Override
            public String getKey() {
                return (String) args[0];
            }

            @Override
            public int getDatabaseId() {
                return databaseId;
            }

            @Override
            public Boolean execute() {
                DatabaseManager databaseManager = transactionManager.getDatabaseManager();
                if (databaseManager.get(databaseId, getKey()) != null) {
                    databaseManager.saveOrUpdate(databaseId, getKey(), args[1], -1L);
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public boolean support(String cmd) {
        return "setxx".equals(cmd);
    }
}
