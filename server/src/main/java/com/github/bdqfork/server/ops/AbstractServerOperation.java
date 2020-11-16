package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.exception.FailedTransactionException;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.protocol.LiteralWrapper;
import com.github.bdqfork.server.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

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
        } catch (FailedTransactionException e) {
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
