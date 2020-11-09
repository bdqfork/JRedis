package com.github.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class FailedExecuteOperationException extends FailedTransactionException{
    public FailedExecuteOperationException(Throwable cause) {
        super(cause);
    }
}
