package com.github.bdqfork.server.ops;

import com.github.bdqfork.server.transaction.OperationType;

public abstract class QueryCommand<T> implements Command<T> {

    @Override
    public OperationType getOperationType() {
        return OperationType.QUERY;
    }

}
