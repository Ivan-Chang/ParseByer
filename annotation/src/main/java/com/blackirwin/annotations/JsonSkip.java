package com.blackirwin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by blackirwin on 2017/6/22.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface JsonSkip {
    boolean skipParseBy() default true;
    boolean skipTestCreate() default true;
    boolean skipParseTo() default true;
}
