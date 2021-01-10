package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.operation.KeyOperation;
import com.github.bdqfork.server.database.Database;

/**
 * @author bdq
 * @since 2020/11/17
 */
public class ServerKeyOperation extends AbstractServerOperation implements KeyOperation {

    @Override
    public void del(String key) {
        execute(new DeleteCommand() {
            @Override
            public Object execute(Database database) {
                database.delete(key);
                return null;
            }

            @Override
            public String getKey() {
                return key;
            }
        });
    }

    @Override
    public void expire(String key, long expire) {

    }

    @Override
    public void expireAt(String key, long expireAt) {

    }

    @Override
    public Long ttl(String key) {
        return (Long) execute(database -> {
            Long ttl = database.ttl(key);
            if (ttl == null) {
                ttl = -1L;
            }
            return ttl;
        });
    }

    @Override
    public Long ttlAt(String key) {
        return (Long) execute(database -> {
            Long ttlAt = database.ttlAt(key);
            if (ttlAt == null) {
                ttlAt = -1L;
            }
            return ttlAt;
        });
    }
}
