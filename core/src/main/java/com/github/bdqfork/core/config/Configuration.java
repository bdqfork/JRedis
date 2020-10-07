package com.github.bdqfork.core.config;

/**
 * 管理配置
 *
 * @author bdq
 * @since 2020/09/21
 */
public class Configuration {
    public static final String DEFAULT_CONFIG_FILE_PATH = "jredis.conf";
    public static final Integer DEFAULT_CONFIG_DATABASES_NUMBER = 16;
    public static final String DEFAULT_CONFIG_SERIALIZER = "jdk";
    public static final String DEFAULT_CONFIG_BACKUP_STRATEGY = "aof";

    private Integer databaseNumber;

    private String serializer;

    private String backupStrategy;

    private String username;

    private String password;

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
}
