package com.github.bdqfork.server.transaction.backup;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.github.bdqfork.core.exception.SerializeException;
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
        Queue<TransactionLog> transactionLogs = getTransactionLogQueue();
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

    private Queue<TransactionLog> getTransactionLogQueue() {
        File file = new File(getLogFilePath());
        if (file.length() == 0) {
            return null;
        }
        Queue<TransactionLog> transactionLogs = new LinkedList<>();
        try(FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream)){

            byte[] head = new byte[1];
            while ((fileInputStream.read(head)) != -1) {
                byte version = dataInputStream.readByte();
                int transactionLogSize = dataInputStream.readInt();

                TransactionLog transactionLog = new TransactionLog();
                List<RedoLog> redoLogs = getRedoLogs(dataInputStream, transactionLogSize);
                transactionLog.setRedoLogs(redoLogs);
                transactionLogs.offer(transactionLog);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return transactionLogs;
    }

    private List<RedoLog> getRedoLogs(DataInputStream dataInputStream, int transactionLogSize) {
        List<RedoLog> redoLogs = new LinkedList<>();
        try {
            // TODO: 反序列化
            for (int flag = 0; flag < transactionLogSize; ) {
                RedoLog redoLog = new RedoLog();
                byte redoLogHead = dataInputStream.readByte();
                flag += Byte.SIZE;

                int redoLogSize = dataInputStream.readInt();
                flag += Integer.SIZE;

                byte operationType = dataInputStream.readByte();
                redoLog.setOperationType(OperationType.getOperationTypeByValue(operationType));
                flag += Byte.SIZE;

                Integer databaseId = dataInputStream.readInt();
                redoLog.setDatabaseId(databaseId);
                flag += Integer.SIZE;

                int keySize = dataInputStream.readInt();
                flag += Integer.SIZE;

                byte[] keyBuff = new byte[keySize];
                dataInputStream.read(keyBuff);
                String key = new String(keyBuff);
                redoLog.setKey(key);
                flag += keySize;

                int valueSize = dataInputStream.readInt();
                flag += Integer.SIZE;

                byte[] valueBuff = new byte[valueSize];
                dataInputStream.read(valueBuff);
                flag += valueSize;

                int valueTypeSize = dataInputStream.readInt();
                flag += Integer.SIZE;

                byte[] valueTypeBuff = new byte[valueTypeSize];
                dataInputStream.read(valueTypeBuff);
                String valueTypeName = new String(valueTypeBuff);
                flag += valueTypeSize;


                Class<?> valueType = null;
                if (!"byte[]".equals(valueTypeName)) {
                    valueType = Class.forName(valueTypeName);
                }
                Object value = getSerializer().deserialize(valueBuff, valueType);
                redoLog.setValue(value);

                long expirationTime = dataInputStream.readLong();
                redoLog.setExpireAt(expirationTime);
                flag += Long.SIZE;

                redoLogs.add(redoLog);
            }
        } catch (IOException | ClassNotFoundException | SerializeException e) {
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
