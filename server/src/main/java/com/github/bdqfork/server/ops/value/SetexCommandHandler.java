package com.github.bdqfork.server.ops.value;

import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.UpdateCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

/**
 * 插入值，同时设置过期时间
 * @author Trey
 * @since 2021/2/4
 */

public class SetexCommandHandler extends AbstractCommandHandler {
    public static final int ARGS_NUM = 3;

    public SetexCommandHandler(Integer databaseId, TransactionManager transactionManager) {
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
                transactionManager.getDatabaseManager().saveOrUpdate(databaseId, getKey(), args[1], (Long) args[2]);
                return null;
            }
        };
    }

    @Override
    public boolean support(String cmd) {
        return "setex".equals(cmd);
    }

    @Override
    public boolean supportArgs(Object[] args) {
        if (args.length == ARGS_NUM) {
            return args[2] instanceof Long;
        }
        return false;
    }
}
