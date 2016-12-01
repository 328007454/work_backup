package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.CopyType;

import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CopyTypeService {
    public static CopyTypeService mInstance;

    private CopyTypeService() {
    }

    public static CopyTypeService getInstance() {
        if (mInstance == null) {
            mInstance = new CopyTypeService();
        }
        return mInstance;
    }

    public Map<String, String> getAllCopyType() {
        Map<String, String> typeMap = new HashMap<>();
        try {
            List<CopyType> list = CustomApplication.getDbManager().selector(CopyType.class).where(CopyType.SELECTED_ABLE, "=", "Y").findAll();
            if (null != list && !list.isEmpty()) {
                for (CopyType type : list)
                    typeMap.put(type.key, type.name);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return typeMap;
    }

}
