package com.blackirwin.exceptions;

/**
 * Created by blackirwin on 2017/7/24.
 */

public class RequiredValueTypeException extends Exception {
    public RequiredValueTypeException(){
        super();
    }

    public RequiredValueTypeException(String msg){
        super(msg);
    }
}
