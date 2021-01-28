package com.github.bdqfork.server.transaction.backup;

import java.io.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.bdqfork.core.serializtion.JdkSerializer;
import com.github.bdqfork.core.util.DateUtils;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.database.DatabaseManager;
import com.github.bdqfork.server.transaction.OperationType;
import com.github.bdqfork.server.transaction.RedoLog;

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
    protected static final String LOG_FILE_NAME = "jredis.log";
    protected static final String LOG_DATE_FILE_NAME_FORMATER = "jredis.%s.log";
    protected final String logFilePath;
    private RedoLogSerializer serializer;
    public Queue<RedoLog> operationQueue = new ConcurrentLinkedQueue<>();
    private Lock lock = new ReentrantLock();
    private Thread reWriteHandler;

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

        File tmpFile = new File(getFullLogFilePath() + TEMP_SUFFIX);

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

    public void setSerializer(RedoLogSerializer serializer) {
        this.serializer = serializer;
    }

    protected RedoLogSerializer getSerializer() {
        return this.serializer;
    }

    @Override
    public void reWrite(DatabaseManager databaseManager) {
        // TODO 扫描所有数据库，并生成RedoLog，同时序列化到磁盘
        reWriteHandler = new Thread(() -> {
            log.debug("Rewriting...");
            List<Database> databases = databaseManager.dump();
            Iterator<Database> iterator = databases.iterator();
            int dataBasesSize = databases.size();
            constructRedoLogAndWrite(dataBasesSize, iterator);
            log.info("Rewriting complete!");
        });
        reWriteHandler.setName("rewrite");
        reWriteHandler.start();
    }

    @Override
    public void storageOperation(RedoLog redoLog) {
        operationQueue.offer(redoLog);
    }

    @Override
    public boolean isReWriteActive() {
        if (reWriteHandler == null) {
            return false;
        }
        return reWriteHandler.isAlive();
    }

    private void constructRedoLogAndWrite(int databasesSize, Iterator<Database> iterator) {
        File newLog = new File(logFilePath + "/" + "jredis_new.log");

        if (newLog.exists()) {
            newLog.delete();
        }

        try {
            newLog.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        try(FileOutputStream fileOutputStream = new FileOutputStream(newLog)) {
            lock.lock();
            for (int i = 0; i < databasesSize; i++) {
                Database database = iterator.next();
                Set<Map.Entry<String, Object>> dictEntrySet = database.getDictMap().entrySet();
                Map<String, Long> expiredMap = database.getExpireMap();

                for (Map.Entry<String, Object> dictEntry : dictEntrySet) {
                    RedoLog redoLog = new RedoLog();
                    redoLog.setDatabaseId(i);
                    String key = dictEntry.getKey();
                    redoLog.setKey(key);
                    redoLog.setValue(dictEntry.getValue());
                    redoLog.setOperationType(OperationType.UPDATE);
                    Long expired = expiredMap.get(key);
                    if (expired == null){
                        redoLog.setExpireAt(-1L);
                    } else {
                        redoLog.setExpireAt(expired);
                    }
                    byte[] data = serializer.serialize(redoLog);
                    fileOutputStream.write(data);
                }
            }

            while (!operationQueue.isEmpty()) {
                RedoLog redoLog = operationQueue.poll();
                byte[] data = serializer.serialize(redoLog);
                fileOutputStream.write(data);
            }
        } catch (IOException e) {
            log.warn("An error occurs during rewrite process: {}", e.getMessage());
            throw new IllegalStateException(e);
        } finally {
            lock.unlock();
        }
        File oldLog = new File(getFullLogFilePath());
        oldLog.renameTo(new File(getOldLogFilePath()));
        newLog.renameTo(new File(getFullLogFilePath()));
    }
}
