package com.github.bdqfork.server.ops.system;

import com.github.bdqfork.server.database.DatabaseManager;
import com.github.bdqfork.server.ops.AbstractCommandHandler;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.QueryCommand;
import com.github.bdqfork.server.transaction.TransactionManager;
import com.github.bdqfork.server.transaction.backup.BackupStrategy;

public class RewriteCommandHandler extends AbstractCommandHandler {

    public RewriteCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        super(databaseId, transactionManager);
    }

    @Override
    public boolean support(String cmd) {
        return "rewrite".equals(cmd);
    }

    @Override
    public boolean supportArgs(Object[] args) {
        return args.length == 0;
    }

    @Override
    protected Command<?> parse(String cmd, Object[] args) {
        return new QueryCommand<Void>() {

            @Override
            public String getKey() {
                return null;
            }

            @Override
            public int getDatabaseId() {
                return -1;
            }

            @Override
            public Void execute() {
                DatabaseManager databaseManager = transactionManager.getDatabaseManager();
                BackupStrategy backupStrategy = transactionManager.gBackupStrategy();
                backupStrategy.rewrite(databaseManager);
                return null;
            }

        };
    }

}
