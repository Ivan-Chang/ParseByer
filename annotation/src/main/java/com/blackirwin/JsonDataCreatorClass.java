package com.blackirwin;

import com.blackirwin.annotations.ParseByJson;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import static com.blackirwin.RelativeConst.PARSE_CLASS_METHOD_NAME;
import static com.blackirwin.RelativeConst.PARSE_CLASS_SUFFIX;
import static com.blackirwin.RelativeConst.PARSE_CLASS_TEST_DATA_METHOD_NAME;
import static com.blackirwin.RelativeConst.PARSE_CLASS_TO_JSON;

/**
 * Created by blackirwin on 2017/6/21.
 */

public final class JsonDataCreatorClass<DataClass> {

    private String initError = "";
    private String lastMethodInvokeError = "";
    private Class<DataClass> jsonDataClass;
    private Constructor<DataClass> jsonDataClassConstructor;
    private Class<?> jsonDataCreatorClass;
    private Method parseJsonMethod;
    private Method parseByTestMethod;
    private Method parseToMethod;

    public JsonDataCreatorClass(Class<DataClass> dataClass){
        this.jsonDataClass = dataClass;
        try {
            this.jsonDataCreatorClass = Class.forName(
                    String.format("%s%s",jsonDataClass.getCanonicalName(), PARSE_CLASS_SUFFIX)
            );
            this.parseJsonMethod = this.jsonDataCreatorClass.getDeclaredMethod(
                    PARSE_CLASS_METHOD_NAME, this.jsonDataClass, String.class
            );
            this.parseByTestMethod = this.jsonDataCreatorClass.getDeclaredMethod(
                    PARSE_CLASS_TEST_DATA_METHOD_NAME
            );
            this.parseToMethod = this.jsonDataCreatorClass.getDeclaredMethod(
                    PARSE_CLASS_TO_JSON, this.jsonDataClass
            );
        } catch (ClassNotFoundException e) {
            this.initError = String.format(
                    Locale.getDefault(), "Sorry, I cannot find the class to parse json create class %s, " +
                            "are you sure you have add the annotation %s to the class",
                    this.jsonDataClass.getSimpleName(), ParseByJson.class.getSimpleName()
            );
        } catch (NoSuchMethodException e) {
            this.initError = String.format(
                    Locale.getDefault(), "Sorry, the class %s to parse json doesn't have the parse method, " +
                            "this will never happen unless the lib is wrong, please use the newest lib.",
                    this.jsonDataClass.getSimpleName()
            );
        }

        try {
            this.jsonDataClassConstructor = this.jsonDataClass.getConstructor();
        } catch (NoSuchMethodException e) {
            this.initError = String.format(
                    Locale.getDefault(), "Sorry, the class %s that you want to parse json to create must have" +
                            " default constructor.", this.jsonDataClass.getSimpleName()
            );
        }

        //There's no exception happened, make initError clean.
        this.initError = "";
    }

    public boolean dataIsRight(){
        return initError.isEmpty();
    }

    public String initError(){
        return initError;
    }

    public String lastMethodInvokeError(){
        return lastMethodInvokeError;
    }

    public DataClass parseBy(String jsonString){
        if (dataIsRight()){
            try {
                DataClass data = this.jsonDataClassConstructor.newInstance();
                boolean success = (boolean) this.parseJsonMethod.invoke(this.jsonDataCreatorClass, data, jsonString);
                if (success) {
                    this.lastMethodInvokeError = "";
                    return data;
                }
                this.lastMethodInvokeError = "the exception happened when parsing the json";
            } catch (InstantiationException e) {
                //This won't happen, because we have checked in ParseByJsonProcessor
                this.lastMethodInvokeError = String.format(
                        Locale.getDefault(),
                        "The bean class %s you want to create cannot be an abstract class.",
                        this.jsonDataClass.getSimpleName()
                );
            } catch (IllegalAccessException e) {
                this.lastMethodInvokeError = String.format(
                        Locale.getDefault(),
                        "The bean class %s you want to create must have a default constructor.",
                        this.jsonDataClass.getSimpleName()
                );
            } catch (InvocationTargetException e) {
                this.lastMethodInvokeError = String.format(
                        Locale.getDefault(),
                        "The default constructor of the bean class %s throws a exception, error is %s",
                        this.jsonDataClass.getSimpleName(), e.getMessage()
                );
            }
        }
        try {
            return this.jsonDataClassConstructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public DataClass parseBy(){
        try {
            return (DataClass) this.parseByTestMethod.invoke(this.jsonDataCreatorClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            return this.jsonDataClassConstructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String parseTo(DataClass data){
        try {
            return (String) this.parseToMethod.invoke(this.jsonDataCreatorClass, data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }
}
