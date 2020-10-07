package com.github.bdqfork.core;


import com.github.bdqfork.core.command.CommandChannelInitializer;
import com.github.bdqfork.core.config.Configuration;
import com.github.bdqfork.core.util.FileUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 服务端，接收用户请求
 *
 * @author bdq
 * @since 2020/9/20
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private final String host;
    private final Integer port;
    private Configuration configuration;
    private List<Database> databases;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public Server(String host, Integer port) throws IOException {
        this(host, port, Configuration.DEFAULT_CONFIG_FILE_PATH);
    }

    public Server(String host, Integer port, String path) throws IOException {
        this.host = host;
        this.port = port;
        // todo: 从指定文件加载配置文件，初始化数据库、事务以及持久化管理
        loadConfiguration(path);
        initializeDatabases();
    }

    /**
     * 启动服务端
     */
    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new CommandChannelInitializer());

        try {
            bootstrap.bind(host, port).sync();
        } catch (InterruptedException e) {
            destroy();
        }
    }

    /**
     * 停止jredis服务
     */
    public void stop() {
        destroy();
    }

    protected void destroy() {
        try {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 加载配置文件
     *
     * @throws IOException 配置文件不存在时抛出
     */
    private void loadConfiguration(String profilePath) throws IOException {
        Properties properties = FileUtils.loadProperties(profilePath);
        this.configuration = new Configuration();

        Integer databaseNumber = Integer.parseInt(properties.getOrDefault("databaseNumber", Configuration.DEFAULT_CONFIG_DATABASES_NUMBER).toString());
        configuration.setDatabaseNumber(databaseNumber);

        String serializer = properties.getOrDefault("serializer", Configuration.DEFAULT_CONFIG_SERIALIZER).toString();
        configuration.setSerializer(serializer);

        String backupStrategy = properties.getOrDefault("backupStrategy", Configuration.DEFAULT_CONFIG_BACKUP_STRATEGY).toString();
        configuration.setBackupStrategy(backupStrategy);

        String username = properties.getProperty("username");
        configuration.setUsername(username);

        String password = properties.getProperty("password");
        configuration.setPassword(password);
    }

    /**
     * 初始化所有数据库
     */
    private void initializeDatabases() {
        this.databases = new ArrayList<>();
        for (int i = 1; i <= configuration.getDatabaseNumber(); i++) {
            databases.add(new Database());
        }
        //todo 调用redo方法
    }
}
