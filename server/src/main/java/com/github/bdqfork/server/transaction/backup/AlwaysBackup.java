package com.github.bdqfork.server.transaction.backup;

import com.github.bdqfork.core.exception.SerializeException;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.OperationType;
import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

import java.io.*;
import java.util.*;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class AlwaysBackup extends AbstractBackupStrategy {

    public AlwaysBackup() {
        super(new LinkedList<>());
    }

    public AlwaysBackup(String logFilePath) {
        super(logFilePath, new LinkedList<>());
    }

    @Override
    protected void doBackup() {
        File file = new File(getLogFilePath());
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file, true);
            Queue<TransactionLog> transactionLogs = getTransactionLogs();
            while (!transactionLogs.isEmpty()) {
                TransactionLog transactionLog = transactionLogs.poll();

//                fileOutputStream.write("begin".getBytes(StandardCharsets.UTF_8));

                byte[] data = getSerializer().serialize(transactionLog);
//                int size = data.length;
//                fileOutputStream.write(size);
                fileOutputStream.write(data);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (SerializeException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void doRedo(List<Database> databases) {
        Queue<TransactionLog> transactionLogs = getTransactionLogsByRedoLog();
        if (transactionLogs == null) {
            return;
        }
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
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;
        Queue<TransactionLog> transactionLogs = new LinkedList<>();
        try {
            fileInputStream = new FileInputStream(file);
            if (fileInputStream.available() <= 0) {
                return null;
            }
            objectInputStream = new ObjectInputStream(fileInputStream);
            while (fileInputStream.available() > 0) {
                TransactionLog log = (TransactionLog) objectInputStream.readObject();
                transactionLogs.offer(log);
                byte[] buf = new byte[4];
                fileInputStream.read(buf);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        return transactionLogs;
    }
}
