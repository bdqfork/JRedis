package com.github.bdqfork.server.transaction.backup;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.github.bdqfork.core.exception.SerializeException;
import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class AlwaysBackup extends AbstractBackupStrategy {
    protected static int head = 0x86;
    protected static int version = 1;
    protected static int redoLogHead = 0x87;

    private final static String REDO_LOG_DATA_SIZE = "redoLogDataSize";
    private final static String OPERATION_TYPE = "operationType";
    private final static String DATABASE_ID = "databaseId";
    private final static String KEY_SIZE = "keySize";
    private final static String KEY_STRING = "key";
    private final static String VALUE_SIZE = "valueSize";
    private final static String VALUE_STRING = "value";
    private final static String VALUE_TYPE_NAME_SIZE = "valueTypeNameSize";
    private final static String VALUE_TYPE = "valueType";
    private final static String EXPIRATION_TIME = "expirationTime";

    public AlwaysBackup() {
        super(null);
    }

    public AlwaysBackup(String logFilePath) {
        super(logFilePath, null);
    }

    @Override
    public void backup(TransactionLog transactionLog) {
        try (OutputStream outputStream = new FileOutputStream(new File(getLogFilePath()), true);
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            // TODO: 序列化
            dataOutputStream.writeByte(head);
            dataOutputStream.writeByte(version);
            int size = 0;

            List<RedoLog> redoLogs = transactionLog.getRedoLogs();
            Queue<Map<String, Object>> redoLogDataQueue = new LinkedList<>();
            for (RedoLog redoLog : redoLogs) {
                int redoLogDataSize = handleRedoLogData(redoLog, redoLogDataQueue);
                int redoLogSize = Byte.SIZE + Integer.SIZE + redoLogDataSize;
                size += redoLogSize;
            }

            dataOutputStream.writeInt(size);
            writeRedoLogs(redoLogDataQueue, dataOutputStream);
            dataOutputStream.flush();
        } catch (IOException | SerializeException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeRedoLogs(Queue<Map<String, Object>> redoLogDataQueue, DataOutputStream dataOutputStream) throws IOException {
        while (!redoLogDataQueue.isEmpty()) {
            Map<String, Object> redoLogData = redoLogDataQueue.poll();
            dataOutputStream.writeByte(redoLogHead);

            Integer redoLogDataSize = (Integer)redoLogData.get(REDO_LOG_DATA_SIZE);
            dataOutputStream.writeInt(redoLogDataSize);

            Integer operationType = (Integer) redoLogData.get(OPERATION_TYPE);
            dataOutputStream.writeByte(operationType);

            Long dataBaseId = (Long) redoLogData.get(DATABASE_ID);
            dataOutputStream.writeLong(dataBaseId);

            Integer keySize = (Integer) redoLogData.get(KEY_SIZE);
            dataOutputStream.writeInt(keySize);

            byte[] keyBuff = (byte[]) redoLogData.get(KEY_STRING);
            dataOutputStream.write(keyBuff);

            Integer valueSize = (Integer) redoLogData.get(VALUE_SIZE);
            dataOutputStream.writeInt(valueSize);

            byte[] valueBuff = (byte[]) redoLogData.get(VALUE_STRING);
            dataOutputStream.write(valueBuff);

            Integer valueTypeNameSize = (Integer) redoLogData.get(VALUE_TYPE_NAME_SIZE);
            dataOutputStream.writeInt(valueTypeNameSize);

            byte[] valueTypeBuff = (byte[]) redoLogData.get(VALUE_TYPE);
            dataOutputStream.write(valueTypeBuff);

            Long expirationTime = (Long) redoLogData.get(EXPIRATION_TIME);
            dataOutputStream.writeLong(expirationTime);
        }
    }

    /**
     * 将redoLog的内容按照协议拆分，存入redoLogDataQueue中
     * @param redoLog
     * @param redoLogDataList
     * @return redoLog数据报文段长度
     * @throws SerializeException
     */
    private int handleRedoLogData(RedoLog redoLog, Queue<Map<String, Object>> redoLogDataList) throws SerializeException {
        String key = redoLog.getKey();
        byte[] keyBuff = key.getBytes(StandardCharsets.UTF_8);
        Object value = redoLog.getValue();
        String valueType = value.getClass().getTypeName();
        byte[] valueBuff = getSerializer().serialize(value);
        byte[] valueTypeBuff = valueType.getBytes(StandardCharsets.UTF_8);

        int keySize = keyBuff.length;
        int valueTypeNameSize = valueTypeBuff.length;
        int valueSize = valueBuff.length;

        Map<String, Object> redoLogData = new HashMap<>();
        redoLogData.put(REDO_LOG_DATA_SIZE, redoLog);
        redoLogData.put(OPERATION_TYPE, redoLog.getOperationType().getValue());
        redoLogData.put(DATABASE_ID, redoLog.getDatabaseId());
        redoLogData.put(KEY_SIZE, keySize);
        redoLogData.put(KEY_STRING, keyBuff);
        redoLogData.put(VALUE_SIZE, valueSize);
        redoLogData.put(VALUE_STRING, valueBuff);
        redoLogData.put(VALUE_TYPE_NAME_SIZE, valueTypeNameSize);
        redoLogData.put(VALUE_TYPE, valueTypeBuff);
        redoLogData.put(EXPIRATION_TIME, redoLog.getExpireAt());
        redoLogDataList.offer(redoLogData);

        return getRedoLogDataSize(keySize, valueTypeNameSize, valueSize);
    }

    private int getRedoLogDataSize(int keySize, int valueNameSize, int valueSize) {
        return Byte.SIZE + Integer.SIZE + Byte.SIZE + Long.SIZE + Integer.SIZE + keySize + Integer.SIZE + valueSize + Integer.SIZE + valueNameSize + Long.SIZE;
    }
}
