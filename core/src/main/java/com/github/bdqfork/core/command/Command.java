package com.github.bdqfork.core.command;

/**
 * 命令者模式，对操作命令进行封装
 *
 * @author bdq
 * @since 2020/9/20
 */
public interface Command {
    Object execute();
}
