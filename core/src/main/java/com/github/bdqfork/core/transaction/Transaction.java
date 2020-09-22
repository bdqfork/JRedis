package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.command.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class Transaction {
    private final Long transactionId;
    private final Integer databaseId;
    private final List<Command> commands;
    private final List<RedoLog> redoLogs = new ArrayList<>();
    private final List<UndoLog> undoLogs = new LinkedList<>();

    public Transaction(Long transactionId, Integer databaseId, List<Command> commands) {
        this.transactionId = transactionId;
        this.databaseId = databaseId;
        this.commands = commands;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    public List<RedoLog> getRedoLogs() {
        return new ArrayList<>(redoLogs);
    }

    public List<UndoLog> getUndoLogs() {
        return new ArrayList<>(undoLogs);
    }

    public void addRedoLog(RedoLog redoLog) {
        this.redoLogs.add(redoLog);
    }

    public void addUndoLog(UndoLog undoLog) {
        this.undoLogs.add(undoLog);
    }

}
