package com.github.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class FailedExecuteCommandException extends FailedTransactionException{
    public FailedExecuteCommandException(Throwable cause) {
        super(cause);
    }
}
