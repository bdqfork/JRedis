package com.github.bdqfork.core.util;

/**
 * @author bdq
 * @since 2020/09/23
 */
public class SystemUtils {
    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
}
