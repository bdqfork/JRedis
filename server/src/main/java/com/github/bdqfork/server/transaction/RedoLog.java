package com.github.bdqfork.server.transaction;

import java.io.Serializable;

/**
 * 记录已经成功的操作
 *
 * @author bdq
 * @since 2020/09/21
 */
public class RedoLog implements Serializable {
    /**
     * 数据库id
     */
    private Integer databaseId;
    /**
     * 数据类型
     */
    private byte dataType;
    /**
     * key
     */
    private String key;
    /**
     * value
     */
    private Object value;
    /**
     * 过期时间
     */
    private Long expireAt;

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
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
