package com.github.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2020/11/5
 */
public class JRedisException extends RuntimeException{
    public JRedisException(String message) {
        super(message);
    }

    public JRedisException(Throwable cause) {
        super(cause);
    }
}
