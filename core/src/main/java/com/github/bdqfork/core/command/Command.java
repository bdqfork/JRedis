package com.github.bdqfork.core.command;

import com.github.bdqfork.core.Database;

import java.io.Serializable;

/**
 * 命令者模式，对操作命令进行封装
 *
 * @author bdq
 * @since 2020/9/20
 */
public interface Command extends Serializable {

    Object execute(Database database) throws Exception;

    String getKey();
}
