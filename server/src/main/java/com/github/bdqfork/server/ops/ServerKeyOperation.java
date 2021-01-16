package com.github.bdqfork.server.ops;

import com.github.bdqfork.core.operation.KeyOperation;
import com.github.bdqfork.core.util.DateUtils;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.transaction.OperationType;

import java.time.temporal.ChronoUnit;
import java.util.Date;

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
        execute(new Command() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Object execute(Database database) {
                if (expire > 0) {
                    Date date = DateUtils.getDateFromNow(expire, ChronoUnit.SECONDS);
                    database.expire(key, date.getTime());
                }
                return null;
            }

            @Override
            public OperationType getOperationType() {
                return OperationType.QUERY;
            }
        });
    }

    @Override
    public void expireAt(String key, long expireAt) {
        execute(new Command() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Object execute(Database database) {
                database.expire(key, expireAt);
                return null;
            }

            @Override
            public OperationType getOperationType() {
                return OperationType.QUERY;
            }
        });
    }

    @Override
    public Long ttl(String key) {
        return (Long) execute(new Command() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Object execute(Database database) {
                Long ttl = database.ttl(key);
                if (ttl == null) {
                    ttl = -1L;
                }
                return ttl;
            }

            @Override
            public OperationType getOperationType() {
                return OperationType.QUERY;
            }
        });
    }

    @Override
    public Long ttlAt(String key) {
        return (Long) execute(new Command() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Object execute(Database database) {
                Long ttlAt = database.ttlAt(key);
                if (ttlAt == null) {
                    ttlAt = -1L;
                }
                return ttlAt;
            }

            @Override
            public OperationType getOperationType() {
                return OperationType.QUERY;
            }
        });
    }
}
