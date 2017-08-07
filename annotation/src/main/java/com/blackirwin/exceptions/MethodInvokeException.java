package com.blackirwin.exceptions;

/**
 * Created by blackirwin on 2017/6/21.
 */

/**
 * This exception will throw, when we invoke the creator's
 * {@link com.blackirwin.RelativeConst#PARSE_CLASS_METHOD_NAME} method.
 */
public class MethodInvokeException extends Exception {

    public MethodInvokeException(){
        super();
    }

    public MethodInvokeException(String msg){
        super(msg);
    }
}
