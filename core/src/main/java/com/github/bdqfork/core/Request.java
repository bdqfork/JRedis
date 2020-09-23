package com.github.bdqfork.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 对请求的命令进行封装
 *
 * @author bdq
 * @since 2020/9/20
 */
public class Request {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    /**
     * 请求id
     */
    private Long requestId;
    /**
     * 请求类型
     */
    private byte type;
    /**
     * 请求数据
     */
    private Object data;

    public static Long newId() {
        return ID_GENERATOR.getAndIncrement();
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
