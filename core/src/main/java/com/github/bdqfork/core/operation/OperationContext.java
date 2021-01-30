package com.github.bdqfork.core.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.protocol.LiteralWrapper;

/**
 * @author bdq
 * @since 2020/11/9
 */
public class OperationContext {
    private int databaseId;
    private String cmd;
    private Object[] args;
    private CommandFuture resultFuture;

    public OperationContext(int databaseId, String cmd, Object[] args) {
        this.databaseId = databaseId;
        this.cmd = cmd;
        this.args = args;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public String getCmd() {
        return cmd;
    }

    public Object[] getArgs() {
        return args;
    }

    public CommandFuture getResultFuture() {
        return resultFuture;
    }

    public void setResultFuture(CommandFuture resultFuture) {
        this.resultFuture = resultFuture;
    }

    public String encode() {
        LiteralWrapper<LiteralWrapper<?>> literalWrapper = encodeArgs(args);

        @SuppressWarnings("unchecked")
        List<LiteralWrapper<?>> literalWrappers = (List<LiteralWrapper<?>>) literalWrapper.getData();

        LiteralWrapper<String> cmdLiteralWrapper = LiteralWrapper.singleWrapper();
        cmdLiteralWrapper.setData(cmd);
        literalWrappers.add(0, cmdLiteralWrapper);

        return literalWrapper.encode();
    }

    private LiteralWrapper<LiteralWrapper<?>> encodeArgs(Object[] args) {
        List<LiteralWrapper<?>> literalWrappers = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof String) {
                LiteralWrapper<String> singleWrapper = LiteralWrapper.singleWrapper();
                singleWrapper.setData(arg);
                literalWrappers.add(singleWrapper);
            }
            if (arg instanceof Number) {
                LiteralWrapper<Long> integerWrapper = LiteralWrapper.integerWrapper();
                integerWrapper.setData(((Number) arg).longValue());
                literalWrappers.add(integerWrapper);
            }
            if (arg instanceof byte[]) {
                LiteralWrapper<byte[]> bulkWrapper = LiteralWrapper.bulkWrapper();
                bulkWrapper.setData(arg);
                literalWrappers.add(bulkWrapper);
            }
            if (arg instanceof List) {
                List<?> items = (List<?>) arg;
                literalWrappers.add(encodeArgs(items.toArray()));
            }
        }
        LiteralWrapper<LiteralWrapper<?>> wrapper = LiteralWrapper.multiWrapper();
        wrapper.setData(literalWrappers);
        return wrapper;
    }
}
