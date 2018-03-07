package com.cnksi.sjjc.service.gztz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjlSjly;
import com.cnksi.sjjc.service.BaseService;

import org.xutils.common.util.KeyValue;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 11:45
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GZTZSjlyService extends BaseService<SbjcGztzjlSjly> {
    final static GZTZSjlyService instance = new GZTZSjlyService();

    protected GZTZSjlyService() {
        super(SbjcGztzjlSjly.class);
    }

    public static GZTZSjlyService getInstance() {
        return instance;
    }

    public List<KeyValue> findByType(String type) {
        List<KeyValue> keyValues = new ArrayList<>();
        SbjcGztzjlSjly data = null;
        try {
            data = selector().and(SbjcGztzjlSjly.TYPE, "=", type).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (data == null) return keyValues;
        JSONArray array = JSON.parseObject(data.jsonV).getJSONArray("values");
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            String v = object.getString("text");
            String k = object.getString("value");
            keyValues.add(new KeyValue(k, v));
        }
        return keyValues;
    }
}
