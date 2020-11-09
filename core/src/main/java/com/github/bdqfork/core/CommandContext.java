package com.github.bdqfork.core;

/**
 * @author bdq
 * @since 2020/11/9
 */
public class CommandContext {
    private int datebaseId;
    private String cmd;
    private Object[] args;
    private CommandFuture resultFutrue;

    public CommandContext(int datebaseId, String cmd, Object[] args) {
        this.datebaseId = datebaseId;
        this.cmd = cmd;
        this.args = args;
    }

    public int getDatebaseId() {
        return datebaseId;
    }

    public String getCmd() {
        return cmd;
    }

    public Object[] getArgs() {
        return args;
    }

    public CommandFuture getResultFutrue() {
        return resultFutrue;
    }

    public void setResultFutrue(CommandFuture resultFutrue) {
        this.resultFutrue = resultFutrue;
    }
}
