package com.cnksi.bdzinspection.daoservice;

import java.util.List;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.OperateItem;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

/**
 * 操作项目
 *
 * @author terry
 */
public class OperateItemService extends BaseService {

    public static OperateItemService mInstance;

    private OperateItemService() {
    }

    public static OperateItemService getInstance() {
        if (mInstance == null) {
            mInstance = new OperateItemService();
        }
        return mInstance;
    }

    /**
     * 根据操作任务查询相关的操作项目
     *
     * @param taskId
     * @return
     */
    public List<OperateItem> findAllOperateItemByTaskId(String taskId) {
        List<OperateItem> result = null;
        try {
            result = XunshiApplication.getDbUtils().selector(OperateItem.class).where(OperateItem.TID, "=", taskId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询操作项目条数
     *
     * @param taskId
     * @return
     */
    public String getCountByOperateTick(String taskId) {
        long count = 0;
        try {
            count = XunshiApplication.getDbUtils().selector(OperateItem.class).where(OperateItem.TID, "=", taskId).count();
        } catch (DbException e) {
            e.printStackTrace();
            count = 0;
        }
        return String.valueOf(count);
    }

}
