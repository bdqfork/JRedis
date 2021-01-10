package com.github.bdqfork.server.config;

/**
 * 管理配置
 *
 * @author bdq
 * @since 2020/09/21
 */
public class Configuration {
    public static final String DEFAULT_CONFIG_REDO_LOG_PATH = "./jredis.log";
    public static final String DEFAULT_CONFIG_FILE_PATH = "jredis.conf";
    public static final String DEFAULT_CONFIG_DATABASES_NUMBER = "16";
    public static final String DEFAULT_CONFIG_SERIALIZER = "jdk";
    public static final String DEFAULT_CONFIG_BACKUP_STRATEGY = "aof";
    public static final String DEFAULT_CONFIG_HOST = "127.0.0.1";
    public static final String DEFAULT_CONFIG_PORT = "7000";

    private String host;

    private Integer port;

    private Integer databaseNumber;

    private String serializer;

    private String backupStrategy;

    private String username;

    private String password;

    private Long timeout;

    private String redoLogPath;

    public String getRedoLogPath() {
        return redoLogPath;
    }

    public void setRedoLogPath(String redoLogPath) {
        this.redoLogPath = redoLogPath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getDatabaseNumber() {
        return databaseNumber;
    }

    public void setDatabaseNumber(Integer databaseNumber) {
        this.databaseNumber = databaseNumber;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getBackupStrategy() {
        return backupStrategy;
    }

    public void setBackupStrategy(String backupStrategy) {
        this.backupStrategy = backupStrategy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
