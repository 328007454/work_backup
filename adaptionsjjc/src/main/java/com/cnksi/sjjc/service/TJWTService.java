package com.cnksi.sjjc.service;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.sjjc.bean.TJWT;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * 查询图解五通图片
 */
public class TJWTService extends BaseService<TJWT> {
    private static TJWTService instance;

    private TJWTService() {
        super(TJWT.class);
    }

    public static TJWTService getInstance() {
        if (null == instance)
            instance = new TJWTService();
        return instance;
    }

    public List<TJWT> findAllTJWT(){
        try {
            return selector().orderBy(TJWT.SORT).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}