package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.operation.ValueOperation;
import com.github.bdqfork.server.database.Database;

import java.util.concurrent.TimeUnit;

/**
 * @author bdq
 * @since 2020/11/11
 */
public class ServerValueOperation extends AbstractServerOperation implements ValueOperation {
    @Override
    public void set(String key, Object value) {
        execute(new UpdateCommand() {
            @Override
            public Object execute(Database database) {
                database.saveOrUpdate(key, value, -1L);
                return null;
            }

            @Override
            public String getKey() {
                return key;
            }
        });
    }

    @Override
    public void set(String key, Object value, long expire, TimeUnit timeUnit) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) execute(database -> database.get(key));
    }

    @Override
    public Long ttl(String key) {
        return null;
    }

    @Override
    public Long ttlAt(String key) {
        return null;
    }

}
