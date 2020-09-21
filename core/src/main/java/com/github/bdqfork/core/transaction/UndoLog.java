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
    private final Map<String, Object> datas;

    public UndoLog(Long transactionId, Map<String, Object> datas) {
        this.transactionId = transactionId;
        this.datas = datas;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Map<String, Object> getDatas() {
        return datas;
    }
}
