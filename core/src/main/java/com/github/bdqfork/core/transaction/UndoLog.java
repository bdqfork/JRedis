package com.github.bdqfork.core.transaction;

import java.io.Serializable;
import java.util.Map;

/**
 * 在事务执行前，记录数据的状态，以供发生异常之后进行回滚操作
 *
 * @author bdq
 * @since 2020/09/21
 */
public class UndoLog implements Serializable {
    private final Long transactionId;
    private final Integer databaseId;
    private Map<String, Object> dataMap;
    private Map<String, Long> expireMap;

    public UndoLog(Long transactionId, Integer databaseId) {
        this.transactionId = transactionId;
        this.databaseId = databaseId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Map<String, Long> getExpireMap() {
        return expireMap;
    }

    public void setExpireMap(Map<String, Long> expireMap) {
        this.expireMap = expireMap;
    }
}
