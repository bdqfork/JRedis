package com.github.bdqfork.core.command;

/**
 * @author bdq
 * @since 2020/9/21
 */
public abstract class AbstractCommand implements Command {
    protected String key;

    public AbstractCommand(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
