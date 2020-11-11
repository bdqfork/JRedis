package com.github.bdqfork.server.ops;

import com.github.bdqfork.server.database.Database;

/**
 * @author bdq
 * @since 2020/11/6
 */
public interface Command {
    Object execute(Database database);
}
