package com.github.bdqfork.core.operation;

/**
 * @author bdq
 * @since 2020/11/11
 */
public interface Operation {
    /**
     * 执行命令
     * 
     * @param cmd  命令
     * @param args 参数
     * @return Object
     */
    public Object exec(String cmd, Object... args);
}
