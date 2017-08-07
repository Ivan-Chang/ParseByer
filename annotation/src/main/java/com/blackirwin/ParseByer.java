package com.blackirwin;

import com.blackirwin.exceptions.InitCreatorClassException;
import com.blackirwin.exceptions.MethodInvokeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blackirwin on 2017/6/21.
 */

/**
 * 1. 直接反射，效率太低
 * 2. 要求必填一个id，进行工厂类，或者 使用者直接使用创建类，体验太差
 * 3. ………………
 * 4. 折中，保存放射的结果，提高效率
 */
public final class ParseByer {

    private static Map<String, JsonDataCreatorClass<?>> creatorCache = new HashMap<>();

    /**
     * This method will create dataClass's instance, and parse jsonString to fill the data.
     *
     * @param dataClass the class of you want to parse json to create and fill it
     * @param jsonString the json's content
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseBy(Class<T> dataClass, String jsonString)
            throws InitCreatorClassException, MethodInvokeException{
        JsonDataCreatorClass<T> creatorClass
                = (JsonDataCreatorClass<T>) creatorCache.get(dataClass.getSimpleName());
        if (creatorClass == null){
            creatorClass = new JsonDataCreatorClass<>(dataClass);

            if (!creatorClass.dataIsRight()){
                throw new InitCreatorClassException(creatorClass.initError());
            }
            creatorCache.put(dataClass.getSimpleName(), creatorClass);
        }

        T data = creatorClass.parseBy(jsonString);

        if (data == null){
            throw new MethodInvokeException(creatorClass.lastMethodInvokeError());
        }

        return data;
    }

    /**
     * This method will create dataClass's instance, and fill it with random or special data.
     *
     * @param dataClass the class of you want to parse json to create and fill it
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseBy(Class<T> dataClass)
            throws InitCreatorClassException, MethodInvokeException{
        JsonDataCreatorClass<T> creatorClass
                = (JsonDataCreatorClass<T>) creatorCache.get(dataClass.getSimpleName());

        if (creatorClass == null){
            creatorClass = new JsonDataCreatorClass<>(dataClass);

            if (!creatorClass.dataIsRight()){
                throw new InitCreatorClassException(creatorClass.initError());
            }
            creatorCache.put(dataClass.getSimpleName(), creatorClass);
        }
        return creatorClass.parseBy();
    }

    @SuppressWarnings("unchecked")
    public static <T> String parseTo(T data)
            throws InitCreatorClassException, MethodInvokeException{
        JsonDataCreatorClass<T> creatorClass
                = (JsonDataCreatorClass<T>) creatorCache.get(data.getClass().getSimpleName());

        if (creatorClass == null){
            creatorClass = new JsonDataCreatorClass<>((Class<T>)data.getClass());

            if (!creatorClass.dataIsRight()){
                throw new InitCreatorClassException(creatorClass.initError());
            }
            creatorCache.put(data.getClass().getSimpleName(), creatorClass);
        }
        return creatorClass.parseTo(data);
    }
}
