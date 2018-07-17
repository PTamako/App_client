package com.example.lenovo.test;


import com.alibaba.fastjson.JSON;


public class JSONHelper {
    /**
     * Java对象变成JSON数据
     */
    public static String toJSON(Object o) {
        return JSON.toJSONString(o);
    }

    /**
     * JSON数据变成Java对象
     */
    public static <T> T fromJSON(String content, Class<T> clazz) {
        return JSON.parseObject(content, clazz);
    }
}
