package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.Database;
import com.github.bdqfork.core.command.Command;

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
    private static final AtomicLong ID = new AtomicLong(0);
    private final Map<Long, Transaction> transactionMap = new ConcurrentHashMap<>();
    private final List<UndoLog> undoLogs = new LinkedList<>();
    private final Queue<RedoLog> redoLogs = new LinkedList<>();
    private final List<Database> databases;

    private static Long newTransactionId() {
        return ID.getAndIncrement();
    }

    public TransactionManager(List<Database> databases) {
        this.databases = new ArrayList<>(databases);
    }

    /**
     * 准备事务
     *
     * @param commands
     * @return Long 返回事务id
     */
    public Long prepare(int databaseId, List<Command> commands) {
        Long transactionId = newTransactionId();
        transactionMap.put(transactionId, new Transaction(transactionId, databaseId, commands));
        return transactionId;
    }

    /**
     * 提交事务
     *
     * @param transactionId 事务id
     * @return Object 事务执行结果
     */
    public Object commit(Long transactionId) throws Exception {
        Object result = null;

        Transaction transaction = transactionMap.get(transactionId);
        int databaseId = transaction.databaseId;
        Database database = databases.get(databaseId);
        for (Command command : transaction.commands) {
            String key = command.getKey();

            UndoLog undoLog = createUndoLog(databaseId, key);
            undoLogs.add(undoLog);

            result = command.execute(database);

            RedoLog redoLog = createRedoLog(databaseId, command.getKey());
            redoLogs.offer(redoLog);

            undoLogs.remove(undoLog);
        }

        return result;
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

    /**
     * 回滚事务
     */
    public void rollback() {
        for (UndoLog undoLog : undoLogs) {
            if (!undoLog.isValid()) {
                continue;
            }
            int databaseId = undoLog.getDatabaseId();
            Database database = databases.get(databaseId);
            database.saveOrUpdate(undoLog.getKey(), undoLog.getValue(), undoLog.getExpireAt());
            RedoLog redoLog = createRedoLog(databaseId, undoLog.getKey());
            redoLogs.offer(redoLog);
            undoLog.setValid(false);
        }
    }

    static class Transaction {
        private final Long transactionId;
        private final Integer databaseId;
        private final List<Command> commands;

        public Transaction(Long transactionId, Integer databaseId, List<Command> commands) {
            this.transactionId = transactionId;
            this.databaseId = databaseId;
            this.commands = commands;
        }

    }
}
