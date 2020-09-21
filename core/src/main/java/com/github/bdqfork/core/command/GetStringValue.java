package com.github.bdqfork.core.command;

import com.github.bdqfork.core.DataBase;

/**
 * 从数据库中查询值
 *
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
        return dataBase.get(key);
    }
}
