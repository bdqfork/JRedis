package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.Database;
import com.github.bdqfork.core.command.Command;
import com.github.bdqfork.core.command.ModifyCommand;
import com.github.bdqfork.core.command.UndoCommand;

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
    private final Map<Long, UndoLog> undoLogMap = new ConcurrentHashMap<>();
    private final List<RedoLog> redoLogs = new LinkedList<>();
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

        RedoLog redoLog = new RedoLog(VERSION, commands);
        redoLogs.add(redoLog);

        UndoLog undoLog = createUndoLog(databaseId, transactionId, commands);
        undoLogMap.put(transactionId, undoLog);

        transactionMap.put(transactionId, new Transaction(databaseId, commands));
        return transactionId;
    }

    private UndoLog createUndoLog(int databaseId, Long transactionId, List<Command> commands) {
        UndoLog undoLog = new UndoLog(transactionId, databaseId);
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Long> expireMap = new HashMap<>();

        Database database = databases.get(databaseId);

        for (Command command : commands) {
            if (command instanceof ModifyCommand) {
                String key = command.getKey();
                dataMap.put(key, database.get(key));
                expireMap.put(key, database.ttlAt(key));
            }
        }

        undoLog.setDataMap(dataMap);
        undoLog.setExpireMap(expireMap);
        return undoLog;
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
        Database database = databases.get(transaction.databaseId);
        for (Command command : transaction.commands) {
            result = command.execute(database);
        }
        undoLogMap.remove(transactionId);
        return result;
    }

    /**
     * 回滚事务
     *
     * @param transactionId 事务id
     */
    public void rollback(Long transactionId) {
        UndoLog undoLog = undoLogMap.get(transactionId);

        int databaseId = undoLog.getDatabaseId();

        Database database = databases.get(databaseId);

        UndoCommand undoCommand = new UndoCommand(undoLog);
        RedoLog redoLog = new RedoLog(VERSION, Collections.singletonList(undoCommand));
        redoLogs.add(redoLog);

        try {
            undoCommand.execute(database);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        undoLogMap.remove(transactionId);
    }

    static class Transaction {
        private final Integer databaseId;
        private final List<Command> commands;

        public Transaction(Integer databaseId, List<Command> commands) {
            this.databaseId = databaseId;
            this.commands = commands;
        }
    }
}
