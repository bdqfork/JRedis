package com.github.bdqfork.server.ops;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2020/11/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ops {
    String value();
}
