package com.github.bdqfork.core.util;

import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Trey
 * @since 2020/10/3
 */

public class FileUtilsTest {

    @Test
    public void loadConfigurationTest() throws IOException {
        Properties properties = FileUtils.loadConfiguration("jredis.conf");
        assert "16".equals(properties.getProperty("databaseNumber"));
    }
}
