package com.huaa.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/8/19 16:16
 */

public class GsonUtil {

    private static Gson gson = new Gson();

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

}
