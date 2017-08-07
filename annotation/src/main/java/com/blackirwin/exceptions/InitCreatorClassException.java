package com.blackirwin.exceptions;

/**
 * Created by blackirwin on 2017/6/21.
 */

import com.blackirwin.ParseByer;

/**
 * This exception will throw when you call {@link ParseByer#parseBy(Class, String)},
 * we will create the {@link com.blackirwin.JsonDataCreatorClass} to cache the reflect info, and
 * error happens in processing of the create.
 */
public class InitCreatorClassException extends Exception {

    public InitCreatorClassException(){
        super();
    }

    public InitCreatorClassException(String msg){
        super(msg);
    }
}
