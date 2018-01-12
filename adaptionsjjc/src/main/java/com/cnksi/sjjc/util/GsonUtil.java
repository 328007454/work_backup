package com.cnksi.sjjc.util;

import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author kkk on 2017/12/12.
 */

public class GsonUtil {
    private static GsonUtil gsonUtil;

    public GsonUtil getGsonUtilInstance() {
        if (gsonUtil == null) {
            gsonUtil = new GsonUtil();
            return gsonUtil;
        } else {
            return gsonUtil;
        }
    }

    public static Object resolveJson(String json) {
        Gson gson = new Gson();
        Object object = gson.fromJson(json,new TypeToken<HwcwHotPart>(){}.getType());
        return object;
    }

}
