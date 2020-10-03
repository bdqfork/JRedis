package com.github.bdqfork.core;


import com.github.bdqfork.core.command.CommandChannelInitializer;
import com.github.bdqfork.core.config.Configuration;
import com.github.bdqfork.core.util.FileUtils;
import com.github.bdqfork.core.util.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
    private static final String DEFAULT_CONFIG_FILE_PATH = "jredis.conf";
    private static final Integer DEFAULT_CONFIG_DATABASES_NUMBER = 16;
    private static final String DEFAULT_CONFIG_SERIALIZER = "jdk";
    private static final String DEFAULT_CONFIG_BACKUP_STRATEGY = "aof";
    private final String host;
    private final Integer port;
    private Configuration configuration;
    private List<Database> databases;

    public Server(String host, Integer port) throws IOException {
        this(host, port, DEFAULT_CONFIG_FILE_PATH);
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
    public void start() throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new CommandChannelInitializer());

        ChannelFuture channelFuture = bootstrap.bind(host,port).sync();
        channelFuture.channel().closeFuture().sync();
    }

    /**
     * 加载配置文件
     * @throws IOException 配置文件不存在时抛出
     */
    private void loadConfiguration(String profilePath) throws IOException {
        Properties properties = FileUtils.loadConfiguration(profilePath);
        Configuration configuration = new Configuration();

        String databaseNumber = properties.getProperty("databaseNumber");
        if (StringUtils.isEmpty(databaseNumber)) {
            configuration.setDatabaseNumber(DEFAULT_CONFIG_DATABASES_NUMBER);
        }
        else {
            configuration.setDatabaseNumber(Integer.parseInt(databaseNumber));
        }

        String serializer = properties.getProperty("serializer");
        if (StringUtils.isEmpty(serializer)) {
            configuration.setSerializer(DEFAULT_CONFIG_SERIALIZER);
        }
        else {
            configuration.setSerializer(serializer);
        }

        String backupStrategy = properties.getProperty("backupStrategy");
        if (StringUtils.isEmpty(backupStrategy)){
            configuration.setBackupStrategy(DEFAULT_CONFIG_BACKUP_STRATEGY);
        }
        else {
            configuration.setBackupStrategy(backupStrategy);
        }

        String username = properties.getProperty("username");
        if (StringUtils.isEmpty(username)) {
            configuration.setUsername(null);
        }
        else {
            configuration.setUsername(username);
        }

        String password = properties.getProperty("password");
        if (StringUtils.isEmpty(password)) {
            configuration.setPassword(null);
        }
        else {
            configuration.setPassword(password);
        }

        this.configuration = configuration;
    }

    /**
     * 初始化所有数据库
     */
    private void initializeDatabases() {
        ArrayList<Database> databases = new ArrayList<>();
        for (int i = 0; i <= configuration.getDatabaseNumber(); i++) {
            databases.add(new Database());
        }
        //todo 调用redo方法

        this.databases = databases;
    }
}
