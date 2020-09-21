package com.github.bdqfork.core;

import com.github.bdqfork.core.config.Configuration;

import java.util.List;

/**
 * 服务端，接收用户请求
 *
 * @author bdq
 * @since 2020/9/20
 */
public class Server {
    private static final String DEFAULT_CONFIG_FILE_PATH = "jredis.conf";
    private Configuration configuration;
    private List<Database> databases;

    public Server(String host, Integer port) {
        this(host, port, DEFAULT_CONFIG_FILE_PATH);
    }

    public Server(String host, Integer port, String path) {
        // todo: 从指定文件加载配置文件，初始化数据库、事务以及持久化管理
    }

    /**
     * 启动服务端
     */
    public void start() {

    }

}
