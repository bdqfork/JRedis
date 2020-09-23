package com.github.bdqfork.core.config;

/**
 * 管理配置
 *
 * @author bdq
 * @since 2020/09/21
 */
public class Configuration {
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
