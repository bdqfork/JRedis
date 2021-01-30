package com.github.bdqfork.server.transaction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.bdqfork.core.exception.TransactionException;
import com.github.bdqfork.server.database.DatabaseManager;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.DeleteCommand;
import com.github.bdqfork.server.ops.UpdateCommand;
import com.github.bdqfork.server.transaction.backup.BackupStrategy;

/**
 * 负责进行事务管理
 *
 * @author bdq
 * @since 2020/9/20
 */
public class TransactionManager {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final Map<Long, Transaction> transactionMap = new ConcurrentHashMap<>(256);
    private final BackupStrategy backupStrategy;
    private final DatabaseManager databaseManager;

    public TransactionManager(BackupStrategy backupStrategy, DatabaseManager databaseManager) {
        this.backupStrategy = backupStrategy;
        this.databaseManager = databaseManager;
        backupStrategy.redo(databaseManager);
    }

    private static Long newId() {
        return ID_GENERATOR.getAndIncrement();
    }

    /**
     * 准备事务
     *
     * @param command 命令
     */
    public Long prepare(Command<?> command) {
        Long transactionId = newId();
        Transaction transaction = new Transaction(transactionId, command.getDatabaseId(), command);
        transactionMap.put(transactionId, transaction);
        return transactionId;
    }

    /**
     * 提交事务
     *
     * @return T 事务执行结果
     */
    public <T> T commit(Long transactionId) throws TransactionException {
        Transaction transaction = transactionMap.get(transactionId);
        Command<?> command = transaction.getCommand();
        return doCommit(transaction, command);
    }

    @SuppressWarnings("unchecked")
    private <T> T doCommit(Transaction transaction, Command<?> command) {
        if (!(command instanceof UpdateCommand) && !(command instanceof DeleteCommand)) {
            return (T) command.execute();
        }

        UndoLog undoLog = createUndoLog(command.getDatabaseId(), command.getKey());
        transaction.addUndoLog(undoLog);

        T result = (T) command.execute();

        RedoLog redoLog = createRedoLog(command.getDatabaseId(), command.getKey(), command.getOperationType());
        transaction.addRedoLog(redoLog);
        return result;
    }

    /**
     * 回滚事务
     */
    public void rollback(Long transactionId) {

        Transaction transaction = transactionMap.get(transactionId);
        List<UndoLog> undoLogs = transaction.getUndoLogs();

        for (UndoLog undoLog : undoLogs) {

            int databaseId = undoLog.getDatabaseId();
            databaseManager.saveOrUpdate(databaseId, undoLog.getKey(), undoLog.getValue(), undoLog.getExpireAt());

            RedoLog redoLog = createRedoLog(databaseId, undoLog.getKey(), OperationType.UPDATE);
            transaction.addRedoLog(redoLog);
        }
    }

    /**
     * 备份Log
     * 
     * @param transactionId
     */
    public void backup(Long transactionId) {
        Transaction transaction = transactionMap.get(transactionId);
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionId(transaction.getTransactionId());
        transactionLog.setRedoLogs(transaction.getRedoLogs());

        backupStrategy.backup(transactionLog);

        transactionMap.remove(transactionId);
    }

    private UndoLog createUndoLog(int databaseId, String key) {
        Object value = databaseManager.get(databaseId, key);
        Long expireAt = databaseManager.ttlAt(databaseId, key) == null ? -1 : databaseManager.ttlAt(databaseId, key);

        UndoLog undoLog = new UndoLog();
        undoLog.setDatabaseId(databaseId);
        undoLog.setKey(key);
        undoLog.setValue(value);
        undoLog.setExpireAt(expireAt);

        return undoLog;
    }

    private RedoLog createRedoLog(int databaseId, String key, OperationType type) {
        Object value = databaseManager.get(databaseId, key);
        Long expireAt = databaseManager.ttlAt(databaseId, key) == null ? -1 : databaseManager.ttlAt(databaseId, key);

        RedoLog redoLog = new RedoLog();
        redoLog.setDatabaseId(databaseId);
        redoLog.setKey(key);
        redoLog.setValue(value);
        redoLog.setOperationType(type);
        redoLog.setExpireAt(expireAt);

        return redoLog;
    }

    public BackupStrategy gBackupStrategy() {
        return backupStrategy;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

}
