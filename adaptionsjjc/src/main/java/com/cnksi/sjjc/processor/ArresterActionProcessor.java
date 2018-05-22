package com.cnksi.sjjc.processor;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.view.ChartDialog;
import com.cnksi.sjjc.activity.CopyAllValueActivity3;
import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.sjjc.bean.DeviceStandards;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Standards;
import com.cnksi.sjjc.service.CopyItemService;
import com.cnksi.sjjc.service.CopyResultService;
import com.cnksi.sjjc.service.DeviceService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public void showHistoryDialog(Activity activity, DbModel mStandardModel, DbModel mCurrentDevice, String currentBdzId) {
        List<DbModel> modelList = null;
        try {
            modelList = findStardHistoryRecord(mCurrentDevice.getString(Device.DEVICEID), mStandardModel.getString(Standards.STAID), currentBdzId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (modelList == null || modelList.size() < 1) {
            ToastUtils.showMessageLong( "当前设备没有历史抄录记录");
            return;
        }
        List<String> xLabe = new ArrayList<>();
        List<Float> yValues = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat fmt1 = new SimpleDateFormat("MM/dd");

        for (int i = 0, count = modelList.size(); i < count; i++) {
            DbModel model = modelList.get(i);
            try {
                xLabe.add(fmt1.format(fmt.parse(model.getString(DefectRecord.DISCOVERED_DATE))));
                yValues.add(model.getFloat(DefectRecord.VAL));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String unit = mStandardModel.getString(Standards.UNIT);
        unit = TextUtils.isEmpty(unit) ? "数值" : "数值(" + unit + ")";
        String line1 = "";
        if (mStandardModel.getString(Standards.DESCRIPTION).contains("电流"))
            line1 = "泄露电流";
        else
            line1 = "指示值";
        ChartDialog.getInstance().showLineChartDialog(activity, activity.getString(R.string.data_history_record_format_str,
                mStandardModel.getString(DeviceStandards.DESCRIPTION)), "时间", unit, xLabe, line1, yValues, R.color.global_base_color);
    }

    @Override
    public HashMap<String, Boolean> getCopyMap(String bdzId, String type) {
        HashMap<String, Boolean> copyMap = new HashMap<String, Boolean>();
        try {
            List<DbModel> dataList = DeviceService.getInstance().getCopyDeviceDbModels(reportId, bdzId, type, null);
            if (dataList != null)
                for (DbModel m : dataList) {
                    copyMap.put(m.getString(Device.DEVICEID), true);
                }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return copyMap;
    }

    @Override
    public void finishTask(String taskId, String remark) throws DbException {
        super.finishTask(taskId, remark);
        CustomApplication.getInstance().getDbManager().update(Report.class, WhereBuilder.b(Report.REPORTID, "=", reportId), new KeyValue(Report.JCQK, remark));
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
