package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Bdz;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务的数据查询
 *
 * @author terry
 */
public class BdzService extends BaseService {

    public static BdzService mInstance;

    private BdzService() {
    }

    public static BdzService getInstance() {
        if (mInstance == null) {
            mInstance = new BdzService();
        }
        return mInstance;
    }

    /**
     * 根据任务Task id 查询任务
     *
     * @param idValue
     * @return
     */
    public Bdz findById(Object idValue) {
        Bdz bdz = null;
        try {
            bdz = XunshiApplication.getDbUtils().findById(Bdz.class, idValue);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return bdz;
    }

    public Bdz findByPmsId(String pms_id) throws DbException {
        return XunshiApplication.getDbUtils().findFirst(Selector.from(Bdz.class).where("pms_id", "=", pms_id));
    }

    /**
     * 查询专项检查的所有变电站 及巡视的状态信息
     *
     * @param inspectionType
     * @param taskId
     * @return
     */
    public List<DbModel> getAllBdzAndInspectionStatus(String inspectionType, String taskId) {
        List<DbModel> mBdzModelList = null;
        try {
            String sql = "select b.*,r.reportid,r.inspection,r.persons,r.temperature,r.humidity,r.starttime,r.endtime,r.submittime,r.taskid from bdz b left join (select * from report where inspection='" + inspectionType + "' and taskid='" + taskId + "') r on b.bdzid=r.bdzid where b.dlt='0'";
            SqlInfo sqlInfo = new SqlInfo(sql);
            mBdzModelList = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mBdzModelList;

    }

    public List<String> findUpdloadPictureFolder() {
        List<String> folder = new ArrayList<String>();
        try {
            SqlInfo sqlInfo = new SqlInfo("select distinct(folder_name) from bdz");
            List<DbModel> folderList = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (null != folderList && !folderList.isEmpty()) {
                for (DbModel model : folderList)
                    folder.add(model.getString("folder_name"));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return folder;
    }

    public List<Bdz> findAllBdzByDp(String dept_id) {
        try {
            return XunshiApplication.getDbUtils().findAll(from(Bdz.class).and(Bdz.DEPTID, "=", dept_id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
