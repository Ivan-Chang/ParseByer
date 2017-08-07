package com.blackirwin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.blackirwin.RelativeConst.PARSE_TEST_DOUBLE_MAX_VALUE;

/**
 * Created by blackirwin on 2017/7/7.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface ParseByTestDouble {
    double maxValue() default PARSE_TEST_DOUBLE_MAX_VALUE;
}
