package com.github.bdqfork.server.ops;

import com.github.bdqfork.server.database.Database;

/**
 * @author bdq
 * @since 2020/11/6
 */
@Ops(value = "get")
public class GetOperation extends AbstractOperation {
    private final String key;

    public GetOperation(String key) {
        this.key = key;
    }

    @Override
    protected Object doExecute(Database database) {
        return database.get(key);
    }


}
