package com.github.bdqfork.server.transaction.backup;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.github.bdqfork.core.serializtion.JdkSerializer;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.OperationType;
import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

/**
 * @author bdq
 * @since 2020/09/22
 */
public abstract class AbstractBackupStrategy implements BackupStrategy {
    protected static final int HEAD = 0x86;
    protected static final byte VERSION = 1;
    private static final String DEFAULT_LOG_FILE_PATH = "./jredis.log";
    private final String logFilePath;
    private RedoLogSerializer serializer;
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
        this.serializer = new RedoLogSerializer(new JdkSerializer());
    }

    @Override
    public void redo(List<Database> databases) {
        List<RedoLog> redoLogs = getRedoLogs();
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

    private List<RedoLog> getRedoLogs() {
        File file = new File(getLogFilePath());
        if (file.length() == 0) {
            return Collections.emptyList();
        }
        List<RedoLog> redoLogs = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file);
                DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {


            while (dataInputStream.available() > 0) {
                int head = dataInputStream.readInt();
                if (head != HEAD) {
                    throw new IllegalStateException(String.format("illegal head %s in back up file !", head));
                }
                byte version = dataInputStream.readByte();
                if (version != VERSION) {
                    throw new IllegalStateException(String.format("illegal version %s in back up file !", version));
                }

                int dataSize = dataInputStream.readInt();
                byte[] data = new byte[dataSize];
                dataInputStream.read(data);

                RedoLog redoLog = getSerializer().deserialize(data);
                redoLogs.add(redoLog);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return redoLogs;
    }

    public void setSerializer(RedoLogSerializer serializer) {
        this.serializer = serializer;
    }

    protected RedoLogSerializer getSerializer() {
        return this.serializer;
    }

    protected String getLogFilePath() {
        return logFilePath;
    }

    protected Queue<TransactionLog> getTransactionLogs() {
        return transactionLogs;
    }

}
