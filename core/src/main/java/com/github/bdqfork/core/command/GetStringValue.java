package com.github.bdqfork.core.command;

import com.github.bdqfork.core.Database;

/**
 * 从数据库中查询值
 *
 * @author bdq
 * @since 2020/9/20
 */
public class GetStringValue extends AbstractCommand implements QueryCommand {

    public GetStringValue(String key) {
        super(key);
    }

    @Override
    public Object execute(Database database) throws Exception {
        return database.get(key);
    }
    
}
