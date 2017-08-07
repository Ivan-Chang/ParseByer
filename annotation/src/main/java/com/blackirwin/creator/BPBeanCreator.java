package com.blackirwin.creator;

import com.blackirwin.exceptions.RequiredValueTypeException;
import com.blackirwin.models.BPIntermediateInfo;

/**
 * Created by blackirwin on 2017/7/24.
 */

public class BPBeanCreator implements IBeanCreator {

    private BPIntermediateInfo info;
    public BPBeanCreator(BPIntermediateInfo info){
        this.info = info;
    }

    @Override
    public String stringForChainedKey(String chainedKey) throws RequiredValueTypeException {
        if (chainedKey == null){
            throw new RequiredValueTypeException("chainedKey is null");
        }
        String value = info.value(chainedKey);
        return value == null ? "" : value;
    }

    @Override
    public int intForChainedKey(String chainedKey) throws RequiredValueTypeException {
        if (chainedKey == null){
            throw new RequiredValueTypeException("chainedKey is null");
        }
        String value = info.value(chainedKey);
        if (value == null) {
            return 0;
        }
        else {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException e){
                throw new RequiredValueTypeException(e.getMessage());
            }
        }
    }

    @Override
    public long longForChainedKey(String chainedKey) throws RequiredValueTypeException {
        if (chainedKey == null){
            throw new RequiredValueTypeException("chainedKey is null");
        }
        String value = info.value(chainedKey);
        if (value == null) {
            return 0;
        }
        else {
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException e){
                throw new RequiredValueTypeException(e.getMessage());
            }
        }
    }

    @Override
    public float floatForChainedKey(String chainedKey) throws RequiredValueTypeException {
        if (chainedKey == null){
            throw new RequiredValueTypeException("chainedKey is null");
        }
        String value = info.value(chainedKey);
        if (value == null) {
            return 0;
        }
        else {
            try {
                return Float.parseFloat(value);
            }
            catch (NumberFormatException e){
                throw new RequiredValueTypeException(e.getMessage());
            }
        }
    }

    @Override
    public double doubleForChainedKey(String chainedKey) throws RequiredValueTypeException {
        if (chainedKey == null){
            throw new RequiredValueTypeException("chainedKey is null");
        }
        String value = info.value(chainedKey);
        if (value == null) {
            return 0;
        }
        else {
            try {
                return Double.parseDouble(value);
            }
            catch (NumberFormatException e){
                throw new RequiredValueTypeException(e.getMessage());
            }
        }
    }
}
