package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.CopyType;

import org.xutils.db.Selector;
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
            Selector selector = XunshiApplication.getDbUtils().selector(CopyType.class).where(CopyType.SELECTED_ABLE, "=", "Y").and(CopyType.DLT, "=", 0);
            List<CopyType> list = selector.findAll();
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
