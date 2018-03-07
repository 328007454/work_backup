package com.cnksi.sjjc.util;

import com.alibaba.fastjson.JSONObject;
import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;


/**
 * @author kkk on 2017/12/12.
 */

public class GsonUtil {
    private static GsonUtil gsonUtil;

    public static Object resolveJson(String json) {

        Object object = JSONObject.parseObject(json, HwcwHotPart.class);
        return object;
    }

    public GsonUtil getGsonUtilInstance() {
        if (gsonUtil == null) {
            gsonUtil = new GsonUtil();
            return gsonUtil;
        } else {
            return gsonUtil;
        }
    }

}
