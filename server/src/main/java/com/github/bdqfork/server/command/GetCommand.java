package com.github.bdqfork.server.command;

import com.github.bdqfork.server.database.Database;

/**
 * @author bdq
 * @since 2020/11/6
 */
public class GetCommand implements Command {
    private String key;

    @Override
    public Object execute(Database database) {
        return database.get(key);
    }
}
