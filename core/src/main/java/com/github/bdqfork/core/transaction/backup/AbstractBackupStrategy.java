package com.github.bdqfork.core.transaction.backup;

import com.github.bdqfork.core.transaction.TransactionLog;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author bdq
 * @since 2020/09/22
 */
public abstract class AbstractBackupStrategy implements BackupStrategy {
    private static final String DEFAULT_LOG_FILE_PATH = "./jredis.log";
    private Lock lock = new ReentrantLock();
    private String logFilePath;
    /**
     * RedoLog buffer
     */
    private final Queue<TransactionLog> transactionLogs;

    public AbstractBackupStrategy(Queue<TransactionLog> transactionLogs) {
        this(DEFAULT_LOG_FILE_PATH, transactionLogs);
    }

    public AbstractBackupStrategy(String logFilePath, Queue<TransactionLog> transactionLogs) {
        this.logFilePath = logFilePath;
        this.transactionLogs = transactionLogs;
        File file = new File(logFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }


    @Override
    public void backup(TransactionLog transactionLog) throws IOException {
        transactionLogs.offer(transactionLog);
        try {
            lock.lock();
            doBackup();
        } finally {
            lock.unlock();
        }
    }

    protected String getLogFilePath() {
        return logFilePath;
    }

    protected Queue<TransactionLog> getTransactionLogs() {
        return transactionLogs;
    }

    protected abstract void doBackup() throws IOException;
}
