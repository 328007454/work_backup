package com.cnksi.sjjc.processor;

import android.app.Activity;
import android.content.Intent;

import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Report;
import com.cnksi.sjjc.activity.CopyAllValueActivity3;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wastrel on 2016/6/14.
 */
public class ArresterActionProcessor extends CopyDataInterface {
    public ArresterActionProcessor(String reportId) {
        super(reportId);
    }

    @Override
    public String getCopyType() {
        return "blqdzcs_dzcs','blqdzcs_xldlz";
    }

    @Override
    public List<DbModel> findAllDeviceHasCopyValue(String currentFunctionModel, String bdzId) throws DbException {

        String selector = "and deviceid in (SELECT DISTINCT(deviceid) FROM copy_item WHERE type_key in('" + getCopyType() + "') and dlt = '0' )";
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
    public String getFinishString() {
        return "完成记录";
    }



    @Override
    public HashMap<String, Boolean> getCopyMap(String bdzId, String type) {
        HashMap<String, Boolean> copyMap = new HashMap<String, Boolean>();
        try {
            List<DbModel> dataList = DeviceService.getInstance().getCopyDeviceDbModels(reportId, bdzId, type, null);
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

    @Override
    public void finishTask(String taskId, String remark) throws DbException {
        super.finishTask(taskId, remark);
        ReportService.getInstance().update(Report.class, WhereBuilder.b(Report.REPORTID, "=", reportId), new KeyValue(Report.JCQK, remark));
    }

    @Override
    public Boolean isHasWeather() {
        return false;
    }

    @Override
    public Boolean isHasCheckResult() {
        return true;
    }
}
