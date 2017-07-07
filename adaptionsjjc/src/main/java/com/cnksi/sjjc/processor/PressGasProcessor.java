package com.cnksi.sjjc.processor;

import android.app.Activity;
import android.content.Intent;

import com.cnksi.sjjc.activity.CopyAllValueActivity3;
import com.cnksi.sjjc.bean.Standards;
import com.cnksi.sjjc.service.CopyItemService;
import com.cnksi.sjjc.service.CopyResultService;
import com.cnksi.sjjc.service.DeviceService;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wastrel on 2016/6/14.
 */
public class PressGasProcessor extends CopyDataInterface {
    public PressGasProcessor(String reportId) {
        super(reportId);
    }

    public static final String filter = "气动%压力";

    @Override
    public String getCopyType() {
        return "sf6yl_kqyl";
    }

    @Override
    public List<DbModel> findAllDeviceHasCopyValue(String currentFunctionModel, String bdzId) throws DbException {
        String selector = "and deviceid in (SELECT DISTINCT(deviceid) FROM copy_item WHERE type_key='" + getCopyType() + "' and dlt = '0' )";
        return DeviceService.getInstance().findDeviceHasCopyValueBySelector(selector, bdzId);
    }


    @Override
    public void gotoInspection(Activity activity) {
        activity.startActivity(new Intent(activity, CopyAllValueActivity3.class));
    }

    @Override
    public String getCopyResult(String bdzId) {
        long copyCount = CopyResultService.getInstance().getReportCopyCount(reportId);
        long totalCount = CopyItemService.getInstance().getCopyItemCount(bdzId, getCopyType());
        return copyCount + "/" + totalCount;
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
                if (str == null || !str.contains("压力") || !str.contains("气动"))

                {
                    iterator.remove();
                }
            }
        }
        return modelList;
    }


}
