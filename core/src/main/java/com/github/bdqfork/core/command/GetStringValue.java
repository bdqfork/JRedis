package com.github.bdqfork.core.command;

import com.github.bdqfork.core.Database;
import com.github.bdqfork.core.exception.FailedExecuteCommandException;

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
    public Object execute(Database database) throws FailedExecuteCommandException {
        return database.get(key);
    }
    
}
