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

    public static OperationType getOperationTypeByValue(int value) {
        if (value == 0) {
            return OperationType.UPDATE;
        }
        if (value == 1) {
            return OperationType.DELETE;
        }
        return null;
    }
}
