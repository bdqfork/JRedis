package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.exception.TransactionException;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.server.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/11/11
 */
public class AbstractServerOperation implements ServerOperation {
    private static final Logger log = LoggerFactory.getLogger(AbstractServerOperation.class);
    private Integer databaseId;
    private TransactionManager transactionManager;

    protected Object execute(Command command) {
        long transactionId = transactionManager.prepare(databaseId, command);
        try {
            return transactionManager.commit(transactionId);
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);
            transactionManager.rollback(transactionId);
            throw new JRedisException("Command error");
        }
    }

    @Override
    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    @Override
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
