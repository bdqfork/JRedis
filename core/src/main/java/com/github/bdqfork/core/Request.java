package com.github.bdqfork.core;

/**
 * 对请求的命令进行封装
 *
 * @author bdq
 * @since 2020/9/20
 */
public class Request {
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

    public Request(Long requestId) {
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
