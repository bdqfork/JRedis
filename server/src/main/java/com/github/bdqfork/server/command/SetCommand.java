package com.github.bdqfork.server.command;

import com.github.bdqfork.server.database.Database;

/**
 * @author bdq
 * @since 2020/11/6
 */
public class SetCommand implements UpdateCommand {
    private String key;
    private Object value;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object execute(Database database) {
        database.saveOrUpdate(key, value, -1L);
        return null;
    }
}
