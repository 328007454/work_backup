package com.cnksi.sjjc.processor;

import android.app.Activity;
import android.content.Context;

import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.ShowCopyHistroyDialogUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.DevicePart;
import com.cnksi.sjjc.bean.Lookup;
import com.cnksi.sjjc.service.DevicePartService;
import com.cnksi.sjjc.service.StandardService;
import com.cnksi.sjjc.util.DateUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wastrel on 2016/6/14.
 */
public abstract class CopyDataInterface {
    public String reportId;


    public CopyDataInterface(String reportId) {
        this.reportId = reportId;
    }


    /**
     * 查询所有抄录设备
     *
     * @param currentFunctionModel
     * @param bdzId
     * @return
     * @throws DbException
     */
    public abstract List<DbModel> findAllDeviceHasCopyValue(String currentFunctionModel, String bdzId) throws DbException;

    /**
     * 开始巡检
     *
     * @param activity
     */
    public abstract void gotoInspection(Activity activity);

    /**
     * 获得抄录结果  1/345格式
     */
    public abstract String getCopyResult(String bdzId);

    /**
     * 获得完成按钮的字符串
     *
     * @return
     */
    public abstract String getFinishString();


    /**
     * 查询部件
     *
     * @param mCurrentDevice
     * @return
     */
    public List<DevicePart> findDevicePart(DbModel mCurrentDevice) {
        // 1、从库中查询设备部件
        List<DevicePart> mDevicePartList = DevicePartService.getInstance()
                .getDevicePartListFromDb(mCurrentDevice.getString("dtid"));
        if (null == mDevicePartList) {
            mDevicePartList = new ArrayList<DevicePart>();
        }
        List<DevicePart> modelPartList = DevicePartService.getInstance()
                .getDevicePartList(mCurrentDevice.getString("deviceid"));
        if (null != modelPartList) {
            mDevicePartList.addAll(modelPartList);
        }
        return mDevicePartList;
    }

    /**
     * 根据设备id和部件id 查询标准
     *
     * @param deviceId
     * @param duid
     * @return
     */
    public List<DbModel> findCopyStandardListByDevicePartId(String deviceId, String duid) {
        return StandardService.getInstance()
                .findCopyStandardListByDevicePartId(deviceId,
                        duid, reportId);
    }

    /**
     * 获得已经抄录的设备
     *
     * @param bdzId
     * @param type
     * @return
     */
    public abstract HashMap<String, Boolean> getCopyMap(String bdzId, String type);

    /**
     * 完成任务
     *
     * @param taskId
     * @param remark
     * @throws DbException
     */
    public void finishTask(String taskId, String remark) throws DbException {
        CustomApplication.getInstance().getDbManager().update(Task.class, WhereBuilder.b(Task.TASKID, "=", taskId), new KeyValue(Task.STATUS, TaskStatus.done.name()));
        CustomApplication.getInstance().getDbManager().update(Report.class, WhereBuilder.b(Report.REPORTID, "=", reportId), new KeyValue(Report.ENDTIME, DateUtils.getCurrentLongTime()));
    }

    /**
     * 得到设备类型列表
     *
     * @return
     */
    public List<Lookup> findPmsDeviceType() {
        List<Lookup> lk = new ArrayList<>();
        lk.add(new Lookup(PMSDeviceType.one.name(), ""));
        return lk;
    }

    /**
     * 获取标准的历史纪录  返回
     *
     * @param deviceId
     * @param standId
     * @param bdzId
     * @return
     * @throws DbException
     */
    public List<DbModel> findStardHistoryRecord(String deviceId, String standId, String bdzId) throws DbException {
        SqlInfo sqlInfo = new SqlInfo("SELECT dr.val,dr.discovered_date,rp.temperature from defect_record dr " +
                "LEFT JOIN report rp on dr.reportid=rp.reportid " +
                " where dr.dlt<>'1'  AND dr.deviceid=? and dr.standid=? and dr.bdzid=? and dr.is_copy='Y' order by dr.discovered_date asc");
        sqlInfo.addBindArg(new KeyValue("", deviceId));
        sqlInfo.addBindArg(new KeyValue("", standId));
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        return CustomApplication.getInstance().getDbManager().findDbModelAll(sqlInfo);
    }

    /**
     * 是否有天气
     *
     * @return
     */
    public Boolean isHasWeather() {
        return true;
    }

    /**
     * 是否有检查结果
     *
     * @return
     */
    public Boolean isHasCheckResult() {
        return false;
    }


    ////以下函数均为子类调用为辅助函数  不参与processor调用

    /**
     * SF6压力检测的抄录Map获取
     *
     * @param bdzId
     * @param type
     * @return
     */
    protected HashMap<String, Boolean> getPressCopyMap(String bdzId, String type, String filter) {
        HashMap<String, Boolean> copyMap = new HashMap<String, Boolean>();
        try {
            List<DbModel> dataList = DeviceService.getInstance().getCopyDeviceDbModels(reportId, bdzId, type, filter);
            if (dataList != null) {
                for (DbModel m : dataList) {
                    copyMap.put(m.getString(Device.DEVICEID), true);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return copyMap;
    }


    public static void showHistory(Context context, CopyItem item) {
        ShowCopyHistroyDialogUtils.showHistory(context, item);
    }

    public abstract String getCopyType();
}
