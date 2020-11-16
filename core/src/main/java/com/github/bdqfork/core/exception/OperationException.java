package com.github.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class OperationException extends TransactionException {
    public OperationException(Throwable cause) {
        super(cause);
    }
}
