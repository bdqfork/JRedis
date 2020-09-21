package com.github.bdqfork.core.transaction;

import java.io.Serializable;

/**
 * 记录已经成功的操作
 *
 * @author bdq
 * @since 2020/09/21
 */
public class RedoLog implements Serializable {
    private Integer databaseId;
    private String key;
    private Object value;
    private Long expireAt;

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }
}
