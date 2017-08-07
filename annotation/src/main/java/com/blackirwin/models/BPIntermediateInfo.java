package com.blackirwin.models;

import java.util.HashMap;

/**
 * Created by blackirwin on 2017/7/24.
 */

public class BPIntermediateInfo {

    private HashMap<String, String> mCache = new HashMap<>();

    public String value(String key){
        return mCache.get(key);
    }
}
