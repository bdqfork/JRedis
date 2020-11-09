package com.github.bdqfork.core.protocol;

import java.util.*;

/**
 * @author bdq
 * @since 2020/11/6
 */
public class EntryWrapper {
    private Type type;
    private Object data;

    public static EntryWrapper singleWrapper() {
        return new EntryWrapper(Type.SINGLE);
    }

    public static EntryWrapper errorWrapper() {
        return new EntryWrapper(Type.ERROR);
    }

    public static EntryWrapper integerWrapper() {
        return new EntryWrapper(Type.INTEGER);
    }

    public static EntryWrapper bulkWrapper() {
        return new EntryWrapper(Type.BULK);
    }

    public static EntryWrapper multiWrapper() {
        return new EntryWrapper(Type.MULTI);
    }

    private EntryWrapper(Type type) {
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
        Stack<EntryWrapper> stack = new Stack<>();
        stack.push(this);

        while (!stack.isEmpty()) {
            EntryWrapper entryWrapper = stack.pop();
            if (entryWrapper.isTypeOf(Type.SINGLE)) {
                String data = entryWrapper.getData();
                builder.append('+').append(data).append("\r\n");
            }

            if (entryWrapper.isTypeOf(Type.ERROR)) {
                String data = entryWrapper.getData();
                builder.append('-').append(data).append("\r\n");
            }

            if (entryWrapper.isTypeOf(Type.INTEGER)) {
                Integer data = entryWrapper.getData();
                builder.append(':').append(data).append("\r\n");
            }

            if (entryWrapper.isTypeOf(Type.BULK)) {
                byte[] data = entryWrapper.getData();
                if (data == null) {
                    builder.append('$').append(-1).append("\r\n");
                } else {
                    String bulk = new String(data);
                    builder.append('$').append(bulk.length()).append("\r\n");
                    builder.append(bulk).append("\r\n");
                }
            }

            if (entryWrapper.isTypeOf(Type.MULTI)) {
                List<EntryWrapper> data = entryWrapper.getData();
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
        Stack<EntryWrapper> stack = new Stack<>();
        stack.push(this);

        while (!stack.isEmpty()) {
            EntryWrapper entryWrapper = stack.pop();
            if (entryWrapper.isTypeOf(Type.SINGLE)) {
                String data = entryWrapper.getData();
                builder.insert(0, "\r\n").insert(0, data);
            }

            if (entryWrapper.isTypeOf(Type.ERROR)) {
                String data = entryWrapper.getData();
                builder.insert(0, "\r\n").insert(0, data);
            }

            if (entryWrapper.isTypeOf(Type.INTEGER)) {
                Integer data = entryWrapper.getData();
                builder.insert(0, "\r\n").insert(0, data);
            }

            if (entryWrapper.isTypeOf(Type.BULK)) {
                byte[] data = entryWrapper.getData();
                if (data == null) {
                    builder.insert(0, "\r\n").insert(0, "nil");
                } else {
                    builder.insert(0, "\r\n").insert(0, new String(data));
                }
            }

            if (entryWrapper.isTypeOf(Type.MULTI)) {
                List<EntryWrapper> data = entryWrapper.getData();
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
