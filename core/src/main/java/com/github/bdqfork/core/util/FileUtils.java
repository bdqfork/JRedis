package com.github.bdqfork.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.Properties;

/**
 * 文件读取工具类
 *
 * @author Trey
 * @since 2020/10/3
 */

public class FileUtils {

    public static Properties loadConfiguration(String filePath) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new NoSuchFileException("Cannot find the file under \"" + filePath +"\"");
        }
        properties.load(inputStream);
        return properties;
    }

}
