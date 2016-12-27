package com.cnksi.core.json;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectBuilder extends JSONBuilder<Object> {

    @Override
    public Object build(JSONObject jsonObject) throws JSONException {
        
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Object build(JSONObject jsonObject, Class c) throws JSONException {
        Object obj = null;
        try {
            obj = c.newInstance();
            Field field[] = c.getFields();
            for (int i = 0; i < field.length; i++){
                Field f = field[i];
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isPrivate(f.getModifiers())) {
                    continue;
                }
                try {
                    f.set(obj, "" + jsonObject.get(f.getName()));
                } catch (Exception ex){
                    f.set(obj, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}
