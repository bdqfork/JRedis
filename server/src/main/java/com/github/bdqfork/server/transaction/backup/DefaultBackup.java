package com.github.bdqfork.server.transaction.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.github.bdqfork.core.exception.SerializeException;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.OperationType;
import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class DefaultBackup extends AbstractBackupStrategy {
    protected static int head = 0x86;

    public DefaultBackup() {
        super(new LinkedList<>());
    }

    public DefaultBackup(String logFilePath) {
        super(logFilePath, new LinkedList<>());
    }

    @Override
    protected void doBackup() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(getLogFilePath()), true)) {
            Queue<TransactionLog> transactionLogs = getTransactionLogs();
            while (!transactionLogs.isEmpty()) {
                TransactionLog transactionLog = transactionLogs.poll();
                byte[] data = getSerializer().serialize(transactionLog);
                // TODO: 序列化
            }
            fileOutputStream.flush();
        } catch (IOException | SerializeException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void doRedo(List<Database> databases) {
        Queue<TransactionLog> transactionLogs = getTransactionLogsByRedoLog();
        while (!transactionLogs.isEmpty()) {
            TransactionLog transactionLog = transactionLogs.poll();
            List<RedoLog> redoLogs = transactionLog.getRedoLogs();
            for (RedoLog redoLog : redoLogs) {
                int databaseId = redoLog.getDatabaseId();
                Database database = databases.get(databaseId);
                OperationType operationType = redoLog.getOperationType();
                String key = redoLog.getKey();

                if (operationType == OperationType.UPDATE) {
                    Object value = redoLog.getValue();
                    Long expireAt = redoLog.getExpireAt();
                    database.saveOrUpdate(key, value, expireAt);
                }

                if (operationType == OperationType.DELETE) {
                    database.delete(key);
                }
            }
        }
    }

    private Queue<TransactionLog> getTransactionLogsByRedoLog() {
        File file = new File(getLogFilePath());
        if (file.length() == 0) {
            return new LinkedList<>();
        }
        Queue<TransactionLog> transactionLogs = new LinkedList<>();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                TransactionLog log = (TransactionLog) objectInputStream.readObject();
                if (log == null) {
                    break;
                }
                transactionLogs.offer(log);
            }
            // TODO: 反序列化
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        return transactionLogs;
    }
}
