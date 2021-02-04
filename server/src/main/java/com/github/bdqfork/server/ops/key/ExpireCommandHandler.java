package com.github.bdqfork.server.ops.key;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.github.bdqfork.core.util.DateUtils;
import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.UpdateCommand;
import com.github.bdqfork.server.transaction.TransactionManager;

public class ExpireCommandHandler extends AbstractCommandHandler {
    public final static int ARGS_NUM = 2;

    public ExpireCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    public boolean support(String cmd) {
        return "expire".equals(cmd);
    }

    @Override
    public boolean supportArgs(Object[] args) {
        if (args.length == ARGS_NUM) {
            return args[1] instanceof Long;
        }
        return false;
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
                if (expire > 0) {
                    Date date = DateUtils.getDateFromNow(expire, ChronoUnit.SECONDS);
                    transactionManager.getDatabaseManager().expire(databaseId, getKey(), date.getTime());
                }
                return null;
            }
        };
    }

}
