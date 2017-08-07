package com.blackirwin.parser;

/**
 * Created by blackirwin on 2017/7/20.
 */

public interface IPBParser {
    <T> T optValue(String key, T defaultValue);
}
