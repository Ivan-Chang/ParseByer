package com.blackirwin.creator;

import com.blackirwin.exceptions.RequiredValueTypeException;

/**
 * Created by blackirwin on 2017/7/24.
 */

public interface IBeanCreator {
    String stringForChainedKey(String chainedKey) throws RequiredValueTypeException;
    int intForChainedKey(String chainedKey) throws RequiredValueTypeException;
    long longForChainedKey(String chainedKey) throws RequiredValueTypeException;
    float floatForChainedKey(String chainedKey) throws RequiredValueTypeException;
    double doubleForChainedKey(String chainedKey) throws RequiredValueTypeException;
}
