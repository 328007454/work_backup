package com.cnksi.sjjc.processor;

import android.app.Activity;
import android.content.Intent;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.activity.CopyBaseDataActivity;
import com.cnksi.sjjc.bean.Standards;
import com.cnksi.sjjc.service.CopyItemService;
import com.cnksi.sjjc.service.CopyResultService;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.TaskService;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wastrel on 2016/6/14.
 */
public class PressWaterProcessor extends CopyDataInterface {
    public PressWaterProcessor(String reportId) {
        super(reportId);
    }

    public String filter = "液压%压力";

    @Override
    public String getCopyType() {
        return "sf6yl_dqyl";
    }

    @Override
    public List<DbModel> findAllDeviceHasCopyValue(String currentFunctionModel, String bdzId) throws DbException {
        String selector = "";
        if (Config.NEW_COPY) {
            selector = "and deviceid in (SELECT DISTINCT(deviceid) FROM copy_item WHERE type_key='" + getCopyType() + "' )";
            return DeviceService.getInstance().findDeviceHasCopyValueBySelector(selector, bdzId);
        } else {
            selector = "and dtid in(select dtid from device_type WHERE name like 'SF6断路%气动%') ";
            return DeviceService.getInstance().findDeviceHasCopyValueBySelector(currentFunctionModel, selector, bdzId);
        }

    }

    @Override
    public void gotoInspcetion(Activity activity) {
        activity.startActivity(new Intent(activity, CopyBaseDataActivity.class));
    }

    @Override
    public String getCopyResult(String bdzId) {

        if (!Config.NEW_COPY) {
            long copyCount = TaskService.getInstance().queryCopyData(reportId);
            long totalCount = TaskService.getInstance().queryCopyDataTatalPress(bdzId, "and dt.name LIKE 'SF6断路器%液压%'  ", filter);
            return copyCount + "/" + totalCount;
        } else {
            long copyCount = CopyResultService.getInstance().getReportCopyCount(reportId);
            long totalCount = CopyItemService.getInstance().getCopyItemCount(bdzId, getCopyType());
            return copyCount + "/" + totalCount;
        }
    }

    @Override
    public HashMap<String, Boolean> getCopyMap(String bdzId, String type) {
        return getPressCopyMap(bdzId, type, filter);
    }

    @Override
    public String getFinishString() {
        return "完成检查";
    }

    @Override
    public void showHistoryDialog(Activity activity, DbModel mStandardModel, DbModel mCurrentDevice, String currentBdzId) {
        showPressHistoryDialog(activity, mStandardModel, mCurrentDevice, currentBdzId);
    }

    @Override
    public List<DbModel> findCopyStandardListByDevicePartId(String deviceId, String duid) {
        List<DbModel> modelList = super.findCopyStandardListByDevicePartId(deviceId, duid);
        if (modelList != null) {
            Iterator<DbModel> iterator = modelList.iterator();
            while (iterator.hasNext()) {
                String str = iterator.next().getString(Standards.DESCRIPTION);
                if (str == null || !str.contains("压力") || !str.contains("液压")) {
                    iterator.remove();
                }
            }
        }
        return modelList;
    }
}
