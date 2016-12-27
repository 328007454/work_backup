package com.cnksi.core.json;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONBuilder<T> {
    protected String root;

    public JSONBuilder() {
        root = "";
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public abstract T build(JSONObject jsonObject) throws JSONException;

    @SuppressWarnings("rawtypes")
    public abstract T build(JSONObject jsonObject, Class c) throws JSONException;
}
