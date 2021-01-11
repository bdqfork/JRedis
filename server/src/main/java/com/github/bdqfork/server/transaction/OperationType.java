package com.github.bdqfork.server.transaction;

/**
 * @author Trey
 * @since 2021/1/10
 */

public enum OperationType {
    UPDATE(0), DELETE(1);

    private int value;

    private OperationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
