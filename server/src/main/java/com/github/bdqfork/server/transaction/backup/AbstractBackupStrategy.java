package com.github.bdqfork.server.transaction.backup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.github.bdqfork.core.serializtion.JdkSerializer;
import com.github.bdqfork.core.util.DateUtils;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.database.DatabaseManager;
import com.github.bdqfork.server.transaction.OperationType;
import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @author bdq
 * @since 2020/09/22
 */
public abstract class AbstractBackupStrategy implements BackupStrategy {
    private static Logger log = LoggerFactory.getLogger(AbstractBackupStrategy.class);
    protected static final byte HEAD = 0x68;
    protected static final byte VERSION = 1;
    protected static final String TEMP_SUFFIX = ".tmp";
    protected static final String LOG_FILE_NAME = "jredis.log";
    protected static final String LOG_DATE_FILE_NAME_FORMATER = "jredis.%s.log";

    protected final String logFilePath;
    protected final ReentrantLock lock;

    private RedoLogSerializer serializer;

    private volatile Future<Void> future;

    private Queue<TransactionLog> cacheLogs = new ConcurrentLinkedQueue<>();
    private ThreadPoolExecutor reWriteExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new DefaultThreadFactory("rewrite"), new ThreadPoolExecutor.DiscardPolicy());

    public AbstractBackupStrategy(String logFilePath) {
        this.logFilePath = logFilePath.replace("//", "/");
        File logDir = new File(logFilePath);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        File file = new File(getFullLogFilePath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("Failed to create log file {} !", getFullLogFilePath());
                throw new IllegalStateException(e);
            }
        }
        this.serializer = new RedoLogSerializer(new JdkSerializer());
        this.lock = new ReentrantLock();
    }

    @Override
    public void redo(DatabaseManager databaseManager) {
        List<RedoLog> redoLogs = getRedoLogs();
        for (RedoLog redoLog : redoLogs) {
            int databaseId = redoLog.getDatabaseId();

            OperationType operationType = redoLog.getOperationType();

            String key = redoLog.getKey();

            if (operationType == OperationType.UPDATE) {
                Object value = redoLog.getValue();
                Long expireAt = redoLog.getExpireAt();
                databaseManager.saveOrUpdate(databaseId, key, value, expireAt);
            }

            if (operationType == OperationType.DELETE) {
                databaseManager.delete(databaseId, key);
            }
        }
    }

    private List<RedoLog> getRedoLogs() {
        File logFile = new File(getFullLogFilePath());

        if (logFile.length() == 0) {
            return Collections.emptyList();
        }

        File tmpFile = new File(getTempLogFilePath());

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
            log.warn("An error occurs during redo process: {}", e.getMessage());
        }
        logFile.renameTo(new File(getOldLogFilePath()));
        tmpFile.renameTo(new File(getFullLogFilePath()));
        return redoLogs;
    }

    protected String getOldLogFilePath() {
        return logFilePath + "/" + String.format(LOG_DATE_FILE_NAME_FORMATER, DateUtils.getNow("yyyy-MM-dd HH:mm:ss"));
    }

    protected String getFullLogFilePath() {
        return logFilePath + "/" + LOG_FILE_NAME;
    }

    protected String getTempLogFilePath() {
        return getFullLogFilePath() + TEMP_SUFFIX;
    }

    public void setSerializer(RedoLogSerializer serializer) {
        this.serializer = serializer;
    }

    protected RedoLogSerializer getSerializer() {
        return this.serializer;
    }

    @Override
    public void rewrite(DatabaseManager databaseManager) {
        if (future != null) {
            return;
        }
        future = reWriteExecutor.submit(() -> {
            log.debug("Rewriting...");
            List<Database> databases = databaseManager.dump();
            doRewrite(databases);
            log.info("Rewriting complete!");
            return null;
        });
    }

    private void doRewrite(List<Database> databases) {
        File newLog = new File(logFilePath + "/" + "jredis_new.log");
        if (newLog.exists()) {
            newLog.delete();
        }

        try {
            newLog.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(newLog)) {
            for (int i = 0; i < databases.size(); i++) {
                Database database = databases.get(i);
                Map<String, Long> expiredMap = database.getExpireMap();

                Map<String, Object> dictMap = database.getDictMap();

                for (Map.Entry<String, Object> entry : dictMap.entrySet()) {
                    RedoLog redoLog = new RedoLog();
                    redoLog.setDatabaseId(i);
                    redoLog.setKey(entry.getKey());
                    redoLog.setValue(entry.getValue());
                    redoLog.setOperationType(OperationType.UPDATE);

                    Long expired = expiredMap.get(entry.getKey());
                    if (expired == null) {
                        redoLog.setExpireAt(-1L);
                    } else {
                        redoLog.setExpireAt(expired);
                    }

                    byte[] data = serializer.serialize(redoLog);
                    fileOutputStream.write(data);
                }
            }

        } catch (IOException e) {
            log.warn("An error occurs during rewrite process: {}", e.getMessage());
        }

    }

    @Override
    public void backup(TransactionLog transactionLog) {
        synchronized (this) {

            doBackup(transactionLog);

            if (future != null) {
                if (!future.isDone()) {
                    cacheLogs.offer(transactionLog);
                    return;
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(getTempLogFilePath())) {
                    while (!cacheLogs.isEmpty()) {
                        TransactionLog log = cacheLogs.poll();
                        for (RedoLog redoLog : log.getRedoLogs()) {
                            byte[] data = serializer.serialize(redoLog);
                            fileOutputStream.write(data);
                        }
                    }
                } catch (IOException e) {
                    log.warn("failed to rewrite, due to {}", e.getMessage());
                }

                while (lock.tryLock()) {
                    try {
                        File curLogFile = new File(getFullLogFilePath());
                        curLogFile.renameTo(new File(getOldLogFilePath()));

                        File tempLogFile = new File(getTempLogFilePath());
                        tempLogFile.renameTo(new File(getFullLogFilePath()));

                        break;
                    } finally {
                        lock.unlock();
                    }
                }

                future = null;
                return;
            }
        }
    }

    protected abstract void doBackup(TransactionLog transactionLog);
}
