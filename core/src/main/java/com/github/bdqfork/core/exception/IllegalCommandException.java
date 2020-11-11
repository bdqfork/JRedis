package com.github.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2020/11/11
 */
public class IllegalCommandException extends JRedisException {
    public IllegalCommandException(String message) {
        super(message);
    }

    public IllegalCommandException(Throwable cause) {
        super(cause);
    }
}
