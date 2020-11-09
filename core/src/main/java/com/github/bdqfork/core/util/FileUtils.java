package com.github.bdqfork.core.util;

import java.io.*;
import java.util.Properties;

/**
 * 文件读取工具类
 *
 * @author Trey
 * @since 2020/10/3
 */

public class FileUtils {

    public static Properties loadPropertiesFile(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

}
