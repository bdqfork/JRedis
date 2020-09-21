package com.github.bdqfork.core.command;

import com.github.bdqfork.core.Database;
import com.github.bdqfork.core.transaction.UndoLog;

import java.util.Map;

/**
 * @author bdq
 * @since 2020/9/21
 */
public class UndoCommand implements Command {
    private static final String DEFAULT_KEY = "undo";
    private final UndoLog undoLog;

    public UndoCommand(UndoLog undoLog) {
        this.undoLog = undoLog;
    }

    @Override
    public Object execute(Database database) throws Exception {
        Map<String, Object> dataMap = undoLog.getDataMap();
        Map<String, Long> expireMap = undoLog.getExpireMap();
        dataMap.forEach((k, v) -> {
            long expireAt = expireMap.get(k);
            database.saveOrUpdate(k, v, expireAt);
        });
        return null;
    }

    @Override
    public String getKey() {
        return DEFAULT_KEY;
    }

}
