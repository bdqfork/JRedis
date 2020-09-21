package com.github.bdqfork.core.transaction;

import com.github.bdqfork.core.command.Command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录已经成功的事务操作
 *
 * @author bdq
 * @since 2020/09/21
 */
public class RedoLog implements Serializable {
    private final String version;
    private final List<Command> commands;

    public RedoLog(String version, List<Command> commands) {
        this.version = version;
        this.commands = new ArrayList<>(commands);
    }

    public String getVersion() {
        return version;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
