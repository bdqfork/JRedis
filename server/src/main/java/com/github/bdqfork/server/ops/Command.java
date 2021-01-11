package com.github.bdqfork.server.ops;

import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.OperationType;

/**
 * @author bdq
 * @since 2020/11/6
 */
public interface Command {
    String getKey();

    Object execute(Database database);

    OperationType getOperationType();
}
