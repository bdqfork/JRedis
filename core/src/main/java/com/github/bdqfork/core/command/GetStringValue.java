package com.github.bdqfork.core.command;

import com.github.bdqfork.core.DataBase;

/**
 * @author bdq
 * @since 2020/9/20
 */
public class GetStringValue implements Command {
    private final DataBase dataBase;
    private final String key;

    public GetStringValue(DataBase dataBase, String key) {
        this.dataBase = dataBase;
        this.key = key;
    }

    @Override
    public Object execute() {
        long expireAt = dataBase.getExpireMap().getOrDefault(key, -1L);
        if (expireAt == -1) {
            return dataBase.getDictMap().get(key);
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - expireAt >= 0) {
            return null;
        }
        return dataBase.getDictMap().get(key);
    }
}
