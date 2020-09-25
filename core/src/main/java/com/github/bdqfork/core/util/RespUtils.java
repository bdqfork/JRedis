package com.github.bdqfork.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bdq
 * @since 2020/09/25
 */
public class RespUtils {

    public static List<String> parserArray(String commands) {
        if (StringUtils.isEmpty(commands)) {
            return Collections.emptyList();
        }
        char[] chs = commands.toCharArray();
        if (chs[0] != '*') {
            throw new IllegalStateException("illegal command array with first character " + chs[0] + " !");
        }
        int size = chs[1] - '0';
        List<String> results = new ArrayList<>(size);

        for (int i = 2; i < chs.length; i++) {
            char c = chs[i];
            if (c == '\r' || c == '\n') {
                continue;
            }
            if (c == '+') {
                i++;
                StringBuilder builder = new StringBuilder();
                while (chs[i] != '\r') {
                    builder.append(chs[i]);
                    i++;
                }
                results.add(builder.toString());
                continue;
            }
            if (c == '$') {
                while (chs[i] != '\r') {
                    i++;
                }
                i+=2;
                StringBuilder builder = new StringBuilder();
                while (chs[i] != '\r') {
                    builder.append(chs[i]);
                    i++;
                }
                results.add(builder.toString());
            }
        }

        return results;
    }
}
