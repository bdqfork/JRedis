package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.command.Command;

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
    private final Map<Long, List<Command>> transactionMap = new ConcurrentHashMap<>();
    private final Map<Long, UndoLog> undoLogMap = new ConcurrentHashMap<>();
    private static final AtomicLong id = new AtomicLong(0);

    private static Long newTransactionId() {
        return id.getAndIncrement();
    }

    /**
     * 提交事务
     *
     * @param commands
     * @return Long 返回事务id
     */
    public Long commit(List<Command> commands) {
        Long transactionId = newTransactionId();
        // todo: 创建UndoLog
        transactionMap.put(transactionId, commands);
        return transactionId;
    }

    /**
     * 执行事务
     *
     * @param transactionId 事务id
     * @return Object 事务执行结果
     */
    public Object execute(Long transactionId) {
        Object result = null;
        for (Command command : transactionMap.get(transactionId)) {
            result = command.execute();
        }
        return result;
    }

    /**
     * 回滚事务
     *
     * @param transactionId 事务id
     */
    public void rollback(Long transactionId) {
        UndoLog undoLog = undoLogMap.get(transactionId);
        undoLog.getCommands().forEach(Command::execute);
    }
}
