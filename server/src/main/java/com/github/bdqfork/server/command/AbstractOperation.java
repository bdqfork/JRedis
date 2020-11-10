package com.github.bdqfork.server.command;

import com.github.bdqfork.core.protocol.EntryWrapper;
import com.github.bdqfork.server.database.Database;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/11/10
 */
public abstract class AbstractOperation implements Operation {
    @Override
    public EntryWrapper execute(Database database) {
        Object result = doExecute(database);
        return encodeResult(result);
    }

    protected abstract Object doExecute(Database database);

    protected EntryWrapper encodeResult(Object result) {
        EntryWrapper entryWrapper = null;
        if (result instanceof String) {
            entryWrapper = EntryWrapper.singleWrapper();
        }
        if (result instanceof Number) {
            entryWrapper = EntryWrapper.integerWrapper();
        }
        if (result instanceof byte[]) {
            entryWrapper = EntryWrapper.bulkWrapper();
        }
        if (result instanceof List) {
            entryWrapper = EntryWrapper.multiWrapper();
            List<?> items = (List<?>) result;
            List<EntryWrapper> entryWrappers = items.stream().map(this::encodeResult).collect(Collectors.toList());
            entryWrapper.setData(entryWrappers);
        }
        if (entryWrapper == null) {
            entryWrapper = EntryWrapper.bulkWrapper();
        }
        return entryWrapper;
    }
}
