package com.cnksi.common.daoservice;

import com.cnksi.common.model.CopyType;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CopyTypeService extends BaseService<CopyType> {
    public static CopyTypeService mInstance;

    private CopyTypeService() {
        super(CopyType.class);
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
            List<CopyType> list = selector().and(CopyType.SELECTED_ABLE, "=", "Y").findAll();
            if (null != list && !list.isEmpty()) {
                for (CopyType type : list) {
                    typeMap.put(type.key, type.name);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return typeMap;
    }

    public List<CopyType> findAllCopyType() {
        try {
            List<CopyType> types = selector().and(CopyType.SELECTED_ABLE, "=", "Y").findAll();
            return types;
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
