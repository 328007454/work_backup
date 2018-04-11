package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.model.BaseModel;
import com.cnksi.bdzinspection.model.OperateToolResult;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

public abstract class BaseService {

    /**
     * 保存或更新
     *
     * @param mObject
     * @return
     */
    public boolean saveOrUpdate(Object mObject) {
        boolean isSuccess = true;
        try {
            CustomApplication.getDbUtils().saveOrUpdate(mObject);
        } catch (DbException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 保存或更新
     *
     * @param mObject
     * @return
     */
    public boolean saveOrUpdateAll(List<? extends Object> entities) {
        boolean isSuccess = true;
        try {
            CustomApplication.getDbUtils().saveOrUpdateAll(entities);
        } catch (DbException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 更新
     *
     * @param mObject
     * @param updateColumnNames
     * @return
     */
    public boolean update(Object mObject, String... updateColumnNames) {
        boolean isSuccess = true;
        try {
            CustomApplication.getDbUtils().update(mObject, updateColumnNames);
        } catch (DbException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    public static Selector from(Class<?> clz) {
        return Selector.from(clz).where(BaseModel.DLT, "=", "0");
    }

}
