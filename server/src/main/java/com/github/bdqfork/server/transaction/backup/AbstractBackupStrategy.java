package com.github.bdqfork.server.transaction.backup;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.github.bdqfork.core.serializtion.JdkSerializer;
import com.github.bdqfork.core.serializtion.Serializer;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.OperationType;
import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

/**
 * @author bdq
 * @since 2020/09/22
 */
public abstract class AbstractBackupStrategy implements BackupStrategy {
    private static final String DEFAULT_LOG_FILE_PATH = "./jredis.log";
    private final String logFilePath;
    private Serializer serializer = new JdkSerializer();
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
    public void redo(List<Database> databases) {
        Queue<RedoLog> redoLogs = getRedoLogs();
        while (!redoLogs.isEmpty()) {
            RedoLog redoLog = redoLogs.poll();

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

    private Queue<RedoLog> getRedoLogs() {
        File file = new File(getLogFilePath());
        if (file.length() == 0) {
            return new LinkedList<>();
        }
        Queue<RedoLog> redoLogs = new LinkedList<>();
        try (InputStream inputStream = new FileInputStream(file);
                DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            while (true) {
                RedoLog log = null;
                if (log == null) {
                    break;
                }
                redoLogs.offer(log);
            }
            // TODO: 反序列化
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return redoLogs;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    protected Serializer getSerializer() {
        return this.serializer;
    }

    protected String getLogFilePath() {
        return logFilePath;
    }

    protected Queue<TransactionLog> getTransactionLogs() {
        return transactionLogs;
    }

}
