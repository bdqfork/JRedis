package com.github.bdqfork.server.transaction.backup;

import java.util.List;

import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.TransactionLog;

/**
 * 备份策略
 *
 * @author bdq
 * @since 2020/09/22
 */
public interface BackupStrategy {
    void backup(TransactionLog transactionLog);

    void redo(List<Database> databases);
}
