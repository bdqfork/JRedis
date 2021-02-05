package com.github.bdqfork.server.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager {
    private List<Database> databases;
    private Lock lock = new ReentrantLock();
    private volatile boolean dumping = false;

    public DatabaseManager(int size) {
        databases = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            databases.add(new Database());
        }
    }

    /**
     * 删除数据
     *
     * @param key 键
     */
    public void delete(int databaseId, String key) {
        if (dumping) {
            try {
                lock.lock();
                databases.get(databaseId).delete(key);
            } finally {
                lock.unlock();
            }
        } else {
            databases.get(databaseId).delete(key);
        }
    }

    /**
     * 更新数据
     *
     * @param key   键
     * @param value 值
     */
    public void saveOrUpdate(int databaseId, String key, Object value, Long expire) {
        if (dumping) {
            try {
                lock.lock();
                databases.get(databaseId).saveOrUpdate(key, value, expire);
            } finally {
                lock.unlock();
            }
        } else {
            databases.get(databaseId).saveOrUpdate(key, value, expire);
        }
    }

    /**
     * 查询数据
     *
     * @param key 键
     */
    public Object get(int databaseId, String key) {
        if (!dumping) {
            return databases.get(databaseId).get(key);
        }
        try {
            lock.lock();
            return databases.get(databaseId).get(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 查询数据剩余过期时间， 如果key不存在，返回-1， 如果key存在，且已经过期，返回-2， 否则返回剩余时间
     *
     * @param key 键
     */
    public Long ttl(int databaseId, String key) {
        if (!dumping) {
            return databases.get(databaseId).ttl(key);
        }
        try {
            lock.lock();
            return databases.get(databaseId).ttl(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 查询数据过期时间，-1表示查询的数据已经过期或者不存在
     *
     * @param key 键
     */
    public Long ttlAt(int databaseId, String key) {
        if (!dumping) {
            return databases.get(databaseId).ttlAt(key);
        }
        try {
            lock.lock();
            return databases.get(databaseId).ttlAt(key);
        } finally {
            lock.unlock();
        }
    }

    public void expire(int databaseId, String key, long expire) {
        if (dumping) {
            try {
                lock.lock();
                databases.get(databaseId).expire(key, expire);
            } finally {
                lock.unlock();
            }
        } else {
            databases.get(databaseId).expire(key, expire);
        }
    }

    /**
     * 获取数据库备份
     * 
     * @return List<Database>
     */
    public List<Database> dump() {
        try {
            lock.lock();
            dumping = true;
            List<Database> dumps = new ArrayList<>(this.databases.size());
            for (Database database : this.databases) {
                Map<String, Object> dictMapDump = new ConcurrentHashMap<>(database.getDictMap());
                Map<String, Long> expireMapDump = new ConcurrentHashMap<>(database.getExpireMap());
                Database dump = new Database(dictMapDump, expireMapDump);
                dumps.add(dump);
            }
            return dumps;
        } finally {
            dumping = false;
            lock.unlock();
        }
    }
}
