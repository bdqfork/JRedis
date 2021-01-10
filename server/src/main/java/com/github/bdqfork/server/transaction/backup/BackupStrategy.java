package com.github.bdqfork.server.transaction.backup;

import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.Transaction;
import com.github.bdqfork.server.transaction.TransactionLog;

import java.util.List;
import java.util.Map;

/**
 * 备份策略
 *
 * @author bdq
 * @since 2020/09/22
 */
public interface BackupStrategy {
    void backup(TransactionLog transactionLog);

    void redo(List<Database> databases, Map<Long, Transaction> transactionMap);
}
