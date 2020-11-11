package com.github.bdqfork.core.protocol;

import java.util.*;

/**
 * @author bdq
 * @since 2020/11/6
 */
public class LiteralWrapper {
    private Type type;
    private Object data;

    public static LiteralWrapper singleWrapper() {
        return new LiteralWrapper(Type.SINGLE);
    }

    public static LiteralWrapper singleWrapper(String data) {
        LiteralWrapper literalWrapper = singleWrapper();
        literalWrapper.setData(data);
        return literalWrapper;
    }

    public static LiteralWrapper errorWrapper() {
        return new LiteralWrapper(Type.ERROR);
    }

    public static LiteralWrapper errorWrapper(String data) {
        LiteralWrapper literalWrapper = errorWrapper();
        literalWrapper.setData(data);
        return literalWrapper;
    }

    public static LiteralWrapper integerWrapper() {
        return new LiteralWrapper(Type.INTEGER);
    }

    public static LiteralWrapper integerWrapper(Number number) {
        LiteralWrapper literalWrapper = integerWrapper();
        literalWrapper.setData(number);
        return literalWrapper;
    }

    public static LiteralWrapper bulkWrapper() {
        return new LiteralWrapper(Type.BULK);
    }

    public static LiteralWrapper bulkWrapper(byte[] data) {
        LiteralWrapper literalWrapper = bulkWrapper();
        literalWrapper.setData(data);
        return literalWrapper;
    }

    public static LiteralWrapper multiWrapper() {
        return new LiteralWrapper(Type.MULTI);
    }

    public static LiteralWrapper multiWrapper(List<?> data) {
        LiteralWrapper literalWrapper = multiWrapper();
        literalWrapper.setData(data);
        return literalWrapper;
    }

    private LiteralWrapper(Type type) {
        this.type = type;
    }

    public boolean isTypeOf(Type type) {
        return this.type == type;
    }

    public Type getType() {
        return type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

    public String encode() {
        StringBuilder builder = new StringBuilder();
        Stack<LiteralWrapper> stack = new Stack<>();
        stack.push(this);

        while (!stack.isEmpty()) {
            LiteralWrapper literalWrapper = stack.pop();
            if (literalWrapper.isTypeOf(Type.SINGLE)) {
                String data = literalWrapper.getData();
                builder.append('+').append(data).append("\r\n");
            }

            if (literalWrapper.isTypeOf(Type.ERROR)) {
                String data = literalWrapper.getData();
                builder.append('-').append(data).append("\r\n");
            }

            if (literalWrapper.isTypeOf(Type.INTEGER)) {
                Integer data = literalWrapper.getData();
                builder.append(':').append(data).append("\r\n");
            }

            if (literalWrapper.isTypeOf(Type.BULK)) {
                byte[] data = literalWrapper.getData();
                if (data == null) {
                    builder.append('$').append(-1).append("\r\n");
                } else {
                    String bulk = new String(data);
                    builder.append('$').append(bulk.length()).append("\r\n");
                    builder.append(bulk).append("\r\n");
                }
            }

            if (literalWrapper.isTypeOf(Type.MULTI)) {
                List<LiteralWrapper> data = literalWrapper.getData();
                if (data == null) {
                    builder.append('*').append(-1).append("\r\n");
                } else {
                    builder.append('*').append(data.size()).append("\r\n");
                    for (int i = data.size() - 1; i >= 0; i--) {
                        stack.push(data.get(i));
                    }
                }
            }
        }
        return builder.toString();
    }

    public String toPlain() {
        StringBuilder builder = new StringBuilder();
        Stack<LiteralWrapper> stack = new Stack<>();
        stack.push(this);

        while (!stack.isEmpty()) {
            LiteralWrapper literalWrapper = stack.pop();
            if (literalWrapper.isTypeOf(Type.SINGLE)) {
                String data = literalWrapper.getData();
                builder.insert(0, "\r\n").insert(0, data);
            }

            if (literalWrapper.isTypeOf(Type.ERROR)) {
                String data = literalWrapper.getData();
                builder.insert(0, "\r\n").insert(0, data);
            }

            if (literalWrapper.isTypeOf(Type.INTEGER)) {
                Integer data = literalWrapper.getData();
                builder.insert(0, "\r\n").insert(0, data);
            }

            if (literalWrapper.isTypeOf(Type.BULK)) {
                byte[] data = literalWrapper.getData();
                if (data == null) {
                    builder.insert(0, "\r\n").insert(0, "nil");
                } else {
                    builder.insert(0, "\r\n").insert(0, new String(data));
                }
            }

            if (literalWrapper.isTypeOf(Type.MULTI)) {
                List<LiteralWrapper> data = literalWrapper.getData();
                if (data == null) {
                    builder.insert(0, "\r\n").insert(0, "nil");
                } else {
                    data.forEach(stack::push);
                }
            }
        }
        return builder.toString();
    }
}
