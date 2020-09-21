package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.command.Command;

import java.util.List;

/**
 * 负责进行事务管理
 *
 * @author bdq
 * @since 2020/9/20
 */
public class TransactionManager {

    /**
     * 提交事务
     *
     * @param commands
     * @return Long 返回事务id
     */
    public Long commit(List<Command> commands) {
        return null;
    }

    /**
     * 执行事务
     *
     * @param transactionId 事务id
     * @return Object 事务执行结果
     */
    public Object execute(Long transactionId) {
        return null;
    }

    /**
     * 回滚事务
     *
     * @param transactionId 事务id
     */
    public void rollback(Long transactionId) {

    }
}
