package com.github.bdqfork.server;

import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.util.FileUtils;
import com.github.bdqfork.server.config.Configuration;
import com.github.bdqfork.server.database.Database;
import com.github.bdqfork.server.netty.NettyServer;
import com.github.bdqfork.server.transaction.TransactionManager;
import com.github.bdqfork.server.transaction.backup.BackupStrategy;
import com.github.bdqfork.server.transaction.backup.BackupStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author bdq
 * @since 2020/11/9
 */
public class JRedisServer {
    private static final Logger log = LoggerFactory.getLogger(JRedisServer.class);
    private static final int DEFAULT_QUEUE_SIZE = 1024;
    private final BlockingQueue<OperationContext> queue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);

    private NettyServer nettyServer;
    private Configuration configuration;
    private List<Database> databases;
    private TransactionManager transactionManager;
    private Dispatcher dispatcher;

    public JRedisServer(String profilePath) {
        try {
            loadConfiguration(profilePath);
        } catch (IOException e) {
            throw new JRedisException(e);
        }
        initializeDatabases();
        initializeTransactionManager();
    }

    private void initializeTransactionManager() {
        String backupStrategyName = configuration.getBackupStrategy();
        BackupStrategy backupStrategy = BackupStrategyFactory.getBackupStrategy(backupStrategyName);
        transactionManager = new TransactionManager(backupStrategy, databases);
    }

    public void listen() {

        dispatcher = new Dispatcher(transactionManager, queue);
        dispatcher.accept();

        log.info("Dispatcher is already!");

        String host = configuration.getHost();
        Integer port = configuration.getPort();
        nettyServer = new NettyServer(host, port, dispatcher);

        nettyServer.start();

        log.info("Server is already to accept request!");
    }

    public void close() {
        nettyServer.stop();
        dispatcher.stop();
    }

    /**
     * 加载配置文件
     *
     * @throws IOException 配置文件不存在时抛出
     */
    private void loadConfiguration(String profilePath) throws IOException {
        Properties properties = FileUtils.loadPropertiesFile(profilePath);
        this.configuration = new Configuration();

        String redoLogPath = properties.getProperty("redoLogPath", Configuration.DEFAULT_CONFIG_FILE_PATH);
        configuration.setRedoLogPath(redoLogPath);

        String host = properties.getProperty("host", Configuration.DEFAULT_CONFIG_HOST);
        configuration.setHost(host);

        Integer port = Integer.valueOf(
                properties.getProperty("port", Configuration.DEFAULT_CONFIG_PORT));
        configuration.setPort(port);

        Integer databaseNumber = Integer.valueOf(properties.getProperty(
                "databaseNumber", Configuration.DEFAULT_CONFIG_DATABASES_NUMBER));
        configuration.setDatabaseNumber(databaseNumber);

        String serializer = properties.getProperty("serializer", Configuration.DEFAULT_CONFIG_SERIALIZER);
        configuration.setSerializer(serializer);

        String backupStrategy = properties.getProperty("backupStrategy", Configuration.DEFAULT_CONFIG_BACKUP_STRATEGY);
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
    }
}
