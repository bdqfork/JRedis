package com.github.bdqfork.core.protocol;

import com.github.bdqfork.core.exception.JRedisException;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author bdq
 * @since 2020/11/5
 */
public class StateMachine {
    private Stack<State> stack;

    public StateMachine() {
        this.stack = new Stack<>();
    }

    public EntryWrapper decode(ByteBuf byteBuf) {
        if (stack.isEmpty()) {
            stack.push(new State());
        }
        EntryWrapper latestEntryWrapper = null;
        while (!stack.isEmpty()) {
            State state = stack.peek();
            if (state.type == null) {
                state.type = parseType(byteBuf);
            }
            if (state.wrapper == null) {
                state.wrapper = getWrapper(state.type);
            }

            if (state.type == Type.SINGLE) {
                state.wrapper.setData(readLine(byteBuf));
                stack.pop();
                latestEntryWrapper = state.wrapper;
            }
            if (state.type == Type.ERROR) {
                state.wrapper.setData(readLine(byteBuf));
                stack.pop();
                latestEntryWrapper = state.wrapper;
            }
            if (state.type == Type.INTEGER) {
                state.wrapper.setData(readLong(byteBuf));
                stack.pop();
                latestEntryWrapper = state.wrapper;
            }
            if (state.type == Type.BYTES) {
                state.wrapper.setData(readBytes(byteBuf));
                stack.pop();
                latestEntryWrapper = state.wrapper;
            }
            if (state.type == Type.BULK) {
                state.count = readLong(byteBuf);
                if (state.count == -1) {
                    stack.pop();
                    latestEntryWrapper = state.wrapper;
                } else {
                    state.type = Type.BYTES;
                    break;
                }
            }
            if (state.type == Type.MULTI) {
                if (state.count == -1) {
                    stack.pop();
                    latestEntryWrapper = state.wrapper;
                } else if (state.count == Long.MIN_VALUE) {
                    state.count = readLong(byteBuf);
                    state.wrapper = EntryWrapper.multiWrapper();
                    if (state.count >= 0) {
                        List<EntryWrapper> entryWrappers = new ArrayList<>((int) state.count);
                        state.wrapper.setData(entryWrappers);
                    }
                } else if (state.count == 0) {
                    stack.pop();
                    if (latestEntryWrapper != null) {
                        List<EntryWrapper> entryWrappers = state.wrapper.getData();
                        entryWrappers.add(latestEntryWrapper);
                    }
                    latestEntryWrapper = state.wrapper;
                } else {
                    if (latestEntryWrapper != null) {
                        List<EntryWrapper> entryWrappers = state.wrapper.getData();
                        entryWrappers.add(latestEntryWrapper);
                    }
                    stack.push(new State());
                    state.count--;
                    break;
                }
            }

            if (stack.isEmpty()) {
                return state.wrapper;
            }
        }
        return null;
    }

    private Object readBytes(ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        return data;
    }

    private EntryWrapper getWrapper(Type type) {
        switch (type) {
            case SINGLE:
                return EntryWrapper.singleWrapper();
            case ERROR:
                return EntryWrapper.errorWrapper();
            case INTEGER:
                return EntryWrapper.integerWrapper();
            case BULK:
                return EntryWrapper.bulkWrapper();
            case MULTI:
                return EntryWrapper.multiWrapper();
            default:
                throw new JRedisException("Not support type");
        }
    }

    private boolean checkIfOneLine(State state) {
        return state.type == Type.SINGLE || state.type == Type.ERROR
                || state.type == Type.INTEGER || state.type == Type.BYTES;
    }

    private Long readLong(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return Long.parseLong(new String(bytes));
    }

    private String readLine(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return new String(bytes);
    }

    private Type parseType(ByteBuf byteBuf) {
        switch (byteBuf.readByte()) {
            case '+':
                return Type.SINGLE;
            case '-':
                return Type.ERROR;
            case ':':
                return Type.INTEGER;
            case '$':
                return Type.BULK;
            case '*':
                return Type.MULTI;
            default:
                throw new JRedisException("Invalid first byte");
        }
    }

    private static class State {
        Type type;
        long count = Long.MIN_VALUE;
        EntryWrapper wrapper;
    }

}
