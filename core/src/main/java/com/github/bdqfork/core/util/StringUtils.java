package com.github.bdqfork.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bdq
 * @since 2020/09/25
 */
public class StringUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.equals("");
    }

    /**
     * 从长字符串中取出两个字符串中的子字符串，并从原字符串中删除已经解析的部分
     * @param startString 起始字符串
     * @param finalString 结束字符串
     * @param str
     * @return
     */
    public static String getSubstringBetweenStrings(String startString, String finalString, StringBuffer str) {
        String subString = str.substring(str.indexOf(startString) + 1, str.indexOf(finalString));
        str.delete(0, startString.length() + finalString.length() + subString.length());
        return subString;
    }
}
