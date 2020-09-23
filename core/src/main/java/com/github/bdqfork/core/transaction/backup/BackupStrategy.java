package com.github.bdqfork.core.transaction.backup;

import com.github.bdqfork.core.transaction.TransactionLog;

import java.io.IOException;

/**
 * 备份策略
 *
 * @author bdq
 * @since 2020/09/22
 */
public interface BackupStrategy {
    void backup(TransactionLog transactionLog);
}
