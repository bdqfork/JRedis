package com.github.bdqfork.server.transaction;

import com.github.bdqfork.core.exception.TransactionException;
import com.github.bdqfork.server.ops.Command;
import com.github.bdqfork.server.ops.DeleteCommand;
import com.github.bdqfork.server.ops.UpdateCommand;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.backup.BackupStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 负责进行事务管理
 *
 * @author bdq
 * @since 2020/9/20
 */
public class TransactionManager {
    private static final String VERSION = "0.1";
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final Map<Long, Transaction> transactionMap = new ConcurrentHashMap<>(256);
    private final BackupStrategy strategy;
    private final List<Database> databases;

    public TransactionManager(BackupStrategy strategy, List<Database> databases) {
        this.strategy = strategy;
        this.databases = new ArrayList<>(databases);
        strategy.redo(databases, transactionMap);
    }

    private static Long newId() {
        return ID_GENERATOR.getAndIncrement();
    }

    /**
     * 准备事务
     *
     * @param databaseId 数据库id
     * @param command    命令
     */
    public Long prepare(int databaseId, Command command) {
        Long transactionId = newId();
        Transaction transaction = new Transaction(transactionId, databaseId, command);
        transactionMap.put(transactionId, transaction);
        return transactionId;
    }

    /**
     * 提交事务
     *
     * @return Object 事务执行结果
     */
    public Object commit(Long transactionId) throws TransactionException {
        Object result = null;

        Transaction transaction = transactionMap.get(transactionId);
        int databaseId = transaction.getDatabaseId();
        Command command = transaction.getCommand();

        if (command instanceof UpdateCommand) {
            UpdateCommand updateOperation = (UpdateCommand) command;
            String key = updateOperation.getKey();

            UndoLog undoLog = createUndoLog(databaseId, key);

            transaction.addUndoLog(undoLog);

            result = command.execute(databases.get(databaseId));

            RedoLog redoLog = createRedoLog(databaseId, updateOperation.getKey(), OperationType.UPDATE);
            transaction.addRedoLog(redoLog);
            backup(transaction);
        }
        else if (command instanceof DeleteCommand) {
            DeleteCommand deleteCommand = (DeleteCommand) command;
            String key = deleteCommand.getKey();

            UndoLog undoLog = createUndoLog(databaseId, key);
            transaction.addUndoLog(undoLog);

            result = command.execute(databases.get(databaseId));

            RedoLog redoLog = createRedoLog(databaseId, deleteCommand.getKey(), OperationType.DELETE);
            transaction.addRedoLog(redoLog);
            backup(transaction);
        }
        else {
            result = command.execute(databases.get(databaseId));
        }

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
            Database database = databases.get(databaseId);
            database.saveOrUpdate(undoLog.getKey(), undoLog.getValue(), undoLog.getExpireAt());

            RedoLog redoLog = createRedoLog(databaseId, undoLog.getKey(), OperationType.UPDATE);
            transaction.addRedoLog(redoLog);
        }

        backup(transaction);
    }


    private void backup(Transaction transaction) {
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionId(transaction.getTransactionId());
        transactionLog.setRedoLogs(transaction.getRedoLogs());
        strategy.backup(transactionLog);
    }

    private UndoLog createUndoLog(int databaseId, String key) {
        Database database = databases.get(databaseId);

        Object value = database.get(key);
        Long expireAt = database.ttlAt(key) == null ? -1 : database.ttlAt(key);

        UndoLog undoLog = new UndoLog();
        undoLog.setDatabaseId(databaseId);
        undoLog.setKey(key);
        undoLog.setValue(value);
        // todo:设置datatype
        undoLog.setExpireAt(expireAt);

        return undoLog;
    }

    private RedoLog createRedoLog(int databaseId, String key, OperationType type) {
        Database database = databases.get(databaseId);

        Object value = database.get(key);
        Long expireAt = database.ttlAt(key) == null ? -1 : database.ttlAt(key);

        RedoLog redoLog = new RedoLog();
        redoLog.setDatabaseId(databaseId);
        redoLog.setKey(key);
        redoLog.setValue(value);
        // todo:设置datatype
        redoLog.setOperationType(type);
        redoLog.setExpireAt(expireAt);

        return redoLog;
    }
}
