package com.cnksi.bdzinspection.utils;

import android.text.TextUtils;


import org.xutils.db.table.DbModel;

import java.util.HashMap;
import java.util.List;

/**
 * 用于设备列表左右滑。
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/27 17:01
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class NextDeviceUtils {
    private static final NextDeviceUtils instance = new NextDeviceUtils();
    HashMap<String, List<DbModel>> devices = new HashMap<>();
    private int lastIndex = -1;

    public static NextDeviceUtils getInstance() {
        return instance;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public void put(String key, List<DbModel> models) {
        devices.put(key, models);
    }

    public DbModel getLeft(String key, String device) {
        List<DbModel> models = devices.get(key);
        if (models == null) return null;
        if (lastIndex >= 0) {
            if (lastIndex > 0) {
                return models.get(--lastIndex);
            } else return null;
        }
        for (int i = 0; i < models.size(); i++) {
            DbModel model = models.get(i);
            if (TextUtils.equals(model.getString("deviceId"), device)) {
                if (i > 0) {
                    lastIndex = --i;
                    return models.get(lastIndex);
                } else {
                    lastIndex = 0;
                    return null;
                }
            }
        }
        return null;
    }


    public DbModel getRight(String key, String device) {
        List<DbModel> models = devices.get(key);
        if (models == null) return null;
        if (lastIndex >= 0) {
            if (lastIndex < models.size() - 1) {
                return models.get(++lastIndex);
            } else return null;
        }
        for (int i = 0; i < models.size(); i++) {
            DbModel model = models.get(i);
            if (TextUtils.equals(model.getString("deviceId"), device)) {
                if (i < models.size() - 1) {
                    lastIndex = ++i;
                    return models.get(lastIndex);
                }
            }
        }
        return null;
    }

}
