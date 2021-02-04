package com.github.bdqfork.server.ops.value;

import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.QueryCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

/**
 * @author Trey
 * @since 2021/2/1
 */

public class GetCommandHandler extends AbstractCommandHandler {
    public static final int ARGS_NUM = 1;

    public GetCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    protected Command<?> parse(String cmd, Object[] args) {
        return new QueryCommand<Object>() {
            @Override
            public String getKey() {
                return (String) args[0];
            }

            @Override
            public int getDatabaseId() {
                return databaseId;
            }

            @Override
            public Object execute() {
                return transactionManager.getDatabaseManager().get(databaseId, getKey());
            }
        };
    }

    @Override
    public boolean support(String cmd) {
        return "get".equals(cmd);
    }

    @Override
    public boolean supportArgs(Object[] args) {
        return args.length == ARGS_NUM;
    }
}
