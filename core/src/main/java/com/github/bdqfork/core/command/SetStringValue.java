package com.github.bdqfork.core.command;

import com.github.bdqfork.core.Database;
import com.github.bdqfork.core.exception.FailedExecuteCommandException;

/**
 * 插入键值对到数据库中
 *
 * @author bdq
 * @since 2020/9/20
 */
public class SetStringValue extends AbstractCommand implements ModifyCommand {
    private final Object value;
    private final long expireAt;

    public SetStringValue(String key, Object value) {
        this(key, value, -1);
    }

    public SetStringValue(String key, Object value, long expireAt) {
        super(key);
        this.value = value;
        this.expireAt = expireAt;
    }

    @Override
    public Object execute(Database database) throws FailedExecuteCommandException {
        database.saveOrUpdate(key, value, expireAt);
        return null;
    }

}
