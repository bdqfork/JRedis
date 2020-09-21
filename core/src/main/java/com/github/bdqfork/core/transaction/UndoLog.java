package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.command.Command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 在事务执行前，记录数据的状态，以供发生异常之后进行回滚操作
 *
 * @author bdq
 * @since 2020/09/21
 */
public class UndoLog implements Serializable {
    private final Long transactionId;
    private final List<Command> commands;

    public UndoLog(Long transactionId, List<Command> commands) {
        this.transactionId = transactionId;
        this.commands = new ArrayList<>(commands);
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
