package com.github.bdqfork.server.ops;

import com.github.bdqfork.server.transaction.OperationType;

/**
 * @author bdq
 * @since 2020/11/6
 */
public abstract class UpdateCommand<T> implements Command<T> {

    @Override
    public OperationType getOperationType() {
        return OperationType.UPDATE;
    }

}
