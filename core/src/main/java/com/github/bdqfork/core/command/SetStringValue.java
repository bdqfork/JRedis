package com.github.bdqfork.core.command;

import com.github.bdqfork.core.DataBase;

/**
 * 插入键值对到数据库中
 *
 * @author bdq
 * @since 2020/9/20
 */
public class SetStringValue implements Command {
    private final DataBase dataBase;
    private final String key;
    private final Object value;
    private final long expireAt;

    public SetStringValue(DataBase dataBase, String key, Object value) {
        this(dataBase, key, value, -1);
    }

    public SetStringValue(DataBase dataBase, String key, Object value, long expireAt) {
        this.dataBase = dataBase;
        this.key = key;
        this.value = value;
        this.expireAt = expireAt;
    }

    @Override
    public Object execute() {
        dataBase.insert(key, value, expireAt);
        return true;
    }
}
