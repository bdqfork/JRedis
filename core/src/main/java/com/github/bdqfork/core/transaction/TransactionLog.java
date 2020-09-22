package com.github.bdqfork.core.transaction;

import java.io.Serializable;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class TransactionLog implements Serializable {
    private Long transactionId;
    private RedoLog[] redoLogs;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public RedoLog[] getRedoLogs() {
        return redoLogs;
    }

    public void setRedoLogs(RedoLog[] redoLogs) {
        this.redoLogs = redoLogs;
    }
}
