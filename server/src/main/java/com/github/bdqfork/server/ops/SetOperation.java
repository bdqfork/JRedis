package com.github.bdqfork.server.ops;

import com.github.bdqfork.server.database.Database;

/**
 * @author bdq
 * @since 2020/11/6
 */
public class SetOperation extends AbstractOperation implements UpdateOperation {
    private final String key;
    private final Object value;

    public SetOperation(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    protected Object doExecute(Database database) {
        database.saveOrUpdate(key, value, -1L);
        return null;
    }
}
