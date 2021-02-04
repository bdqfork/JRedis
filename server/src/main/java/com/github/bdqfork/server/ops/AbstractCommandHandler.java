package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.exception.TransactionException;
import com.github.bdqfork.server.transaction.TransactionManager;

public abstract class AbstractCommandHandler implements CommandHandler {
    protected final Integer databaseId;
    protected final TransactionManager transactionManager;

    public AbstractCommandHandler(Integer databaseId, TransactionManager transactionManager) {
        this.databaseId = databaseId;
        this.transactionManager = transactionManager;
    }

    @Override
    public Object handle(String cmd, Object... args) {
        if (!support(cmd)) {
            throw new JRedisException(String.format("Unsupport command %s", cmd));
        }
        if (!supportArgs(args)) {
            throw new JRedisException(String.format("Wrong %s command args", cmd));
        }
        Command<?> command = parse(cmd, args);
        long transactionId = transactionManager.prepare(command);
        try {
            return transactionManager.commit(transactionId);
        } catch (TransactionException e) {
            transactionManager.rollback(transactionId);
            throw new JRedisException(String.format("Execute command %s error, will rollback !", args), e);
        } finally {
            transactionManager.backup(transactionId);
        }
    }

    protected abstract Command<?> parse(String cmd, Object[] args);

}
