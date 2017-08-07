package com.blackirwin.utils;

import java.util.Random;

/**
 * Created by blackirwin on 2017/6/27.
 */

public class RandomDataUtils {

    public static String createString(int length){
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            stringBuilder.append(base.charAt(number));
        }
        return stringBuilder.toString();
    }

    public static int createInt(int maxValue){
        return new Random(System.currentTimeMillis()).nextInt() % maxValue;
    }

    public static double createDouble(double maxValue){
        return new Random(System.currentTimeMillis()).nextDouble() % maxValue;
    }

    public static long createLong(long maxValue){
        return new Random(System.currentTimeMillis()).nextLong() % maxValue;
    }

    public static boolean createBoolean(){
        return new Random(System.currentTimeMillis()).nextBoolean();
    }

    private RandomDataUtils(){}
}
