package com.github.bdqfork.core.transaction;

import java.io.Serializable;

/**
 * 在事务执行前，记录数据的状态，以供发生异常之后进行回滚操作
 *
 * @author bdq
 * @since 2020/09/21
 */
public class UndoLog implements Serializable {
    private Integer databaseId;
    private String key;
    private Object value;
    private Long expireAt;
    private boolean valid;

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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
