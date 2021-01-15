package com.github.bdqfork.server.transaction.backup;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.github.bdqfork.core.serializtion.JdkSerializer;
import com.github.bdqfork.core.util.DateUtils;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.OperationType;
import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/09/22
 */
public abstract class AbstractBackupStrategy implements BackupStrategy {
    private static Logger log = LoggerFactory.getLogger(AbstractBackupStrategy.class);
    protected static final byte HEAD = 0x68;
    protected static final byte VERSION = 1;
    protected static final String TEMP_SUFFIX = ".tmp";
    protected static final String DEFAULT_LOG_FILE_PATH = ".";
    protected static final String LOG_FILE_NAME = "jredis.log";
    protected static final String LOG_DATE_FILE_NAME_FORMATER = "jredis.%s.log";
    protected final String logFilePath;
    private RedoLogSerializer serializer;
    /**
     * RedoLog buffer
     */
    private final Queue<TransactionLog> transactionLogs;

    public AbstractBackupStrategy(Queue<TransactionLog> transactionLogs) {
        this(DEFAULT_LOG_FILE_PATH, transactionLogs);
    }

    public AbstractBackupStrategy(String logFilePath, Queue<TransactionLog> transactionLogs) {
        this.logFilePath = logFilePath.replace("//", "/");
        this.transactionLogs = transactionLogs;
        File file = new File(getLogFilePath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("Failed to create log file {} !", getLogFilePath());
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
        File logFile = new File(getLogFilePath());

        if (logFile.length() == 0) {
            return Collections.emptyList();
        }

        File tmpFile = new File(getLogFilePath() + TEMP_SUFFIX);

        if (tmpFile.exists()) {
            tmpFile.delete();
        }

        try {
            tmpFile.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        List<RedoLog> redoLogs = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(logFile);
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile, true);
                DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            int head;
            while ((head = dataInputStream.read()) != -1) {
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

                dataOutputStream.writeByte(head);
                dataOutputStream.writeByte(version);
                dataOutputStream.writeInt(dataSize);
                dataOutputStream.write(data);
            }
        } catch (IOException e) {
            log.warn("An error occurs during redo process ! {}", e.getMessage());
        }
        String oldLogFileName = String.format(LOG_DATE_FILE_NAME_FORMATER, DateUtils.getNow("yyyy-MM-dd HH:mm:ss"));
        logFile.renameTo(new File(oldLogFileName));
        tmpFile.renameTo(new File(getLogFilePath()));
        return redoLogs;
    }

    protected String getLogFilePath() {
        return logFilePath + "/" + LOG_FILE_NAME;
    }

    public void setSerializer(RedoLogSerializer serializer) {
        this.serializer = serializer;
    }

    protected RedoLogSerializer getSerializer() {
        return this.serializer;
    }

    protected Queue<TransactionLog> getTransactionLogs() {
        return transactionLogs;
    }

}
