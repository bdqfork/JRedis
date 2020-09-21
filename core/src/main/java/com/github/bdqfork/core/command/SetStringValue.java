package com.github.bdqfork.core.command;

import com.github.bdqfork.core.DataBase;

/**
 * @author bdq
 * @since 2020/9/20
 */
public class SetStringValue implements Command {
    private final DataBase dataBase;
    private final String key;
    private final Object value;
    private final long expireAt;

    public SetStringValue(DataBase dataBase, String key, Object value, long expireAt) {
        this.dataBase = dataBase;
        this.key = key;
        this.value = value;
        this.expireAt = expireAt;
    }

    @Override
    public Object execute() {
        dataBase.getDictMap().put(key, value);
        dataBase.getExpireMap().put(key, expireAt);
        return true;
    }
}
