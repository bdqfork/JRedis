package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.Database;
import com.github.bdqfork.core.command.Command;
import com.github.bdqfork.core.exception.FailedTransactionException;
import com.github.bdqfork.core.transaction.backup.BackupStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    }

    private static Long newId() {
        return ID_GENERATOR.getAndIncrement();
    }

    /**
     * 准备事务
     *
     * @param databaseId 数据库id
     * @param commands   命令
     */
    public Long prepare(int databaseId, List<Command> commands) {
        Long transactionId = newId();
        Transaction transaction = new Transaction(transactionId, databaseId, commands);
        transactionMap.put(transactionId, transaction);
        return transactionId;
    }

    /**
     * 提交事务
     *
     * @return Object 事务执行结果
     */
    public Object commit(Long transactionId) throws FailedTransactionException {
        Object result = null;

        Transaction transaction = transactionMap.get(transactionId);
        int databaseId = transaction.getDatabaseId();

        Database database = databases.get(databaseId);

        for (Command command : transaction.getCommands()) {
            String key = command.getKey();

            UndoLog undoLog = createUndoLog(databaseId, key);

            transaction.addUndoLog(undoLog);

            result = command.execute(database);

            RedoLog redoLog = createRedoLog(databaseId, command.getKey());
            transaction.addRedoLog(redoLog);

        }

        backup(transaction);

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

            RedoLog redoLog = createRedoLog(databaseId, undoLog.getKey());
            transaction.addRedoLog(redoLog);
        }

        backup(transaction);
    }


    private void backup(Transaction transaction) {
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionId(transaction.getTransactionId());
        transactionLog.setRedoLogs(transactionLog.getRedoLogs());
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
        undoLog.setExpireAt(expireAt);

        return undoLog;
    }

    private RedoLog createRedoLog(int databaseId, String key) {
        Database database = databases.get(databaseId);

        Object value = database.get(key);
        Long expireAt = database.ttlAt(key) == null ? -1 : database.ttlAt(key);

        RedoLog redoLog = new RedoLog();
        redoLog.setDatabaseId(databaseId);
        redoLog.setKey(key);
        redoLog.setValue(value);
        redoLog.setExpireAt(expireAt);

        return redoLog;
    }

}
