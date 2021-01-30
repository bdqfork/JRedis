package com.github.bdqfork.server.ops;

public interface CommandHandler {
    /**
     * 处理命令
     * 
     * @param cmd  命令
     * @param args 参数
     * @return Object 处理结果
     */
    Object handle(String cmd, Object... args);

    /**
     * 判断是否可以处理
     * 
     * @param cmd 命令
     * @return boolean
     */
    boolean support(String cmd);
}
