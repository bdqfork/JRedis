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

    protected LiteralWrapper execute(Command command) {
        long transactionId = transactionManager.prepare(databaseId, command);
        try {
            Object result = transactionManager.commit(transactionId);
            return encodeResult(result);
        } catch (FailedTransactionException e) {
            log.error(e.getMessage(), e);
            transactionManager.rollback(transactionId);
            throw new JRedisException("Command error");
        }
    }

    protected LiteralWrapper encodeResult(Object result) {
        LiteralWrapper literalWrapper = null;
        if (result instanceof String) {
            literalWrapper = LiteralWrapper.singleWrapper();
        }
        if (result instanceof Number) {
            literalWrapper = LiteralWrapper.integerWrapper();
        }
        if (result instanceof byte[]) {
            literalWrapper = LiteralWrapper.bulkWrapper();
        }
        if (result instanceof List) {
            literalWrapper = LiteralWrapper.multiWrapper();
            List<?> items = (List<?>) result;
            result = items.stream().map(this::encodeResult).collect(Collectors.toList());
        }
        if (literalWrapper == null) {
            literalWrapper = LiteralWrapper.bulkWrapper();
        }
        literalWrapper.setData(result);
        return literalWrapper;
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
