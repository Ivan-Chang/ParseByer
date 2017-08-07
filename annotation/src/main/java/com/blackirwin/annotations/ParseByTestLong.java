package com.blackirwin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.blackirwin.RelativeConst.PARSE_TEST_LONG_MAX_VALUE;

/**
 * Created by blackirwin on 2017/7/7.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface ParseByTestLong {
    long maxValue() default PARSE_TEST_LONG_MAX_VALUE;
}
