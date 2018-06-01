package com.cnksi.nari.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/7/21 17:29
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class JsonUtil {
    public static final Object parse(String str) {
        return JSON.parse(str);
    }

    public static final JSONArray parseArray(String str) {
        return JSON.parseArray(str);
    }

    public static final <T> List<T> parseArray(String str, Class<T> cls) {
        return JSON.parseArray(str, (Class) cls);
    }

    public static final List<Object> parseArray(String str, Type[] typeArr) {
        return JSON.parseArray(str, typeArr);
    }

    public static Map<String, Object> parseMap(String str) {
        Map<String, Object> hashMap = new HashMap();
        try {
            JSONObject jSONObject = new JSONObject(str);
            Iterator keys = jSONObject.keys();
            while (keys.hasNext()) {
                String obj = keys.next().toString();
                Object obj2 = jSONObject.get(obj);
                if (obj2 instanceof JSONArray) {
                    org.json.JSONArray jSONArray = (org.json.JSONArray) obj2;
                    List arrayList = new ArrayList(jSONArray.length());
                    for (int i = 0; i < jSONArray.length(); i++) {
                        arrayList.add(parseMap(jSONArray.opt(i).toString()));
                    }
                    hashMap.put(obj, arrayList);
                } else {
                    hashMap.put(obj, obj2);
                }
            }
            return hashMap;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static final com.alibaba.fastjson.JSONObject parseObject(String str) {
        return JSON.parseObject(str);
    }

    public static final <T> T parseObject(String str, Class<T> cls) {
        return JSON.parseObject(str, cls, new Feature[0]);
    }

    public static final String parseString(String str, String str2) {
        return JSON.parseObject(str).get(str2).toString();
    }

    public static final byte[] toJSONBytes(Object obj, SerializerFeature... serializerFeatureArr) {
        return JSON.toJSONBytes(obj, serializerFeatureArr);
    }

    public static final String toJSONString(Object obj) {
        return JSON.toJSONString(obj, new SerializerFeature[0]);
    }

    public static final String toJSONString(String str, String str2) {
        com.alibaba.fastjson.JSONObject jSONObject = new com.alibaba.fastjson.JSONObject();
        jSONObject.put(str, str2);
        return jSONObject.toString();
    }
}