package com.cnksi.bdzinspection.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.CopyResultService;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.view.ChartDialog;
import com.cnksi.bdzinspection.view.ChartDialog.LineSet;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.DeviceStandards;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Standards;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年8月23日 上午11:39:57 TODO
 */
public class ShowHistroyDialogUtils {

    private static List<DbModel> findStardHistoryRecordHaveT(String deviceId, String standId, String bdzId)
            throws DbException {
        String sql = "SELECT dr.val,dr.discovered_date,rp.temperature from defect_record dr "
                + "LEFT JOIN report rp on dr.reportid=rp.reportid "
                + " where dr.dlt<>'1'  AND  rp.temperature is not null and dr.deviceid=? and dr.standid=? and dr.bdzid=? and dr.is_copy='Y' order by dr.discovered_date DESC limit 20";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("",deviceId));
        sqlInfo.addBindArg(new KeyValue("",standId));
        sqlInfo.addBindArg(new KeyValue("",bdzId));
        return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
    }

    private static List<DbModel> findStardHistoryRecord(String deviceId, String standId, String bdzId)
            throws DbException {
        String sql ="SELECT dr.val,dr.discovered_date from defect_record dr "
                + " where dr.dlt<>'1'  AND dr.deviceid=? and dr.standid=? and dr.bdzid=? and dr.is_copy='Y' order by dr.discovered_date desc limit 20";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("",deviceId));
        sqlInfo.addBindArg(new KeyValue("",standId));
        sqlInfo.addBindArg(new KeyValue("",bdzId));
        return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
    }

    private static void showHistoryDialog(Activity activity, DbModel mStandardModel, String deviceID,
                                          String currentBdzId) {
        List<DbModel> modelList = null;
        try {
            modelList = findStardHistoryRecord(deviceID, mStandardModel.getString(Standards.STAID), currentBdzId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (modelList == null || modelList.size() < 1) {
            ToastUtils.showMessage("当前设备没有历史抄录记录");
            return;
        }
        List<String> xLabe = new ArrayList<>();
        List<Float> yValues = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat fmt1 = new SimpleDateFormat("MM/dd");

        for (int i = modelList.size() - 1; i >= 0; i--) {
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
        String desc = mStandardModel.getString(DeviceStandards.DESCRIPTION);
        if (desc.endsWith("：") || desc.endsWith(":")) {
            desc = desc.substring(0, desc.length() - 2);
        }
        ChartDialog.getInstance().showLineChartDialog(activity,
                activity.getString(R.string.xs_data_history_record_format_str, desc), "时间", unit, xLabe, desc, yValues,
                R.color.xs_global_base_color);
    }

    /**
     * 压力检测历史曲线框
     *
     * @param activity
     * @param mStandardModel
     * @param currentBdzId
     */
    private static void showPressHistoryDialog(Activity activity, DbModel mStandardModel, String deviceID,
                                               String currentBdzId) {
        List<DbModel> modelList = null;
        try {
            modelList = findStardHistoryRecordHaveT(deviceID, mStandardModel.getString(Standards.STAID), currentBdzId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (modelList == null || modelList.size() < 1) {
            ToastUtils.showMessage("当前没有历史抄录记录");
            return;
        }
        List<String> xLabe = new ArrayList<>();
        List<Float> values = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat fmt1 = new SimpleDateFormat("MM/dd");
        List<Float> temps = new ArrayList<>();
        for (int i = modelList.size() - 1; i >= 0; i--) {
            DbModel model = modelList.get(i);
            try {
                xLabe.add(fmt1.format(fmt.parse(model.getString(DefectRecord.DISCOVERED_DATE))));
                values.add(model.getFloat(DefectRecord.VAL));
                temps.add(model.getFloat(Report.TEMPERATURE));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<ChartDialog.LineSet> yList = new ArrayList<>();
        yList.add(new ChartDialog.LineSet("压力", values));
        yList.add(new ChartDialog.LineSet("温度", temps));

        ChartDialog.getInstance().showLineChartDialog(activity,
                activity.getString(R.string.xs_data_history_record_format_str,
                        mStandardModel.getString(DeviceStandards.DESCRIPTION)),
                "时间", "数值(MPa/℃)", xLabe, yList, Arrays.asList(R.color.xs_global_base_color, R.color.xs_light_red));
    }

    public static void ShowHistory(Activity mActivity, DbModel mStandardModel, String deviceID, String bdzId) {
        String desc = mStandardModel.getString(Standards.DESCRIPTION);
        if (desc.contains("SF6气体压力")) {
            showPressHistoryDialog(mActivity, mStandardModel, deviceID, bdzId);
        } else {
            showHistoryDialog(mActivity, mStandardModel, deviceID, bdzId);
        }
    }

    public static void showHistory(Context context, CopyItem item) {
        CopyResultService.getInstance().clearNULLData();
        String sql = "SELECT case WHEN result.val  ISNULL THEN 0 ELSE  result.val END val,"
                + " case WHEN result.val_a  ISNULL THEN 0 ELSE  result.val_a END A,"
                + " case WHEN result.val_b  ISNULL THEN 0 ELSE  result.val_b END B,"
                + " case WHEN result.val_c  ISNULL THEN 0 ELSE  result.val_c END C,"
                + " case WHEN result.val_o  ISNULL THEN 0 ELSE  result.val_o END O,"
                + " case WHEN report.temperature ISNULL or report.temperature='' THEN 0 ELSE report.temperature END temperature,"
                + "	case WHEN result.update_time ISNULL THEN result.create_time ELSE result.update_time END time"
                + " FROM"
                + " copy_result result LEFT JOIN report report ON result.reportid = report.reportid WHERE result.bdzid = ? "
                + " AND report.reportid NOT IN(SELECT DISTINCT(reportid) FROM report rp WHERE rp.taskid in (SELECT DISTINCT(taskid) FROM task tk WHERE tk.STATUS='undo'))"
                + " AND result.deviceid = ? AND result.item_id=?" + " ORDER BY time";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("",item.bdzid));
        sqlInfo.addBindArg(new KeyValue("",item.deviceid));
        sqlInfo.addBindArg(new KeyValue("",item.id));
        try {
            List<DbModel> resultModel = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (resultModel == null || resultModel.size() < 1) {
                ToastUtils.showMessage("当前没有历史抄录记录");
                return;
            }
            String title = context.getString(R.string.xs_data_history_record_format_str,
                    item.description.replace("(" + item.unit + ")", ""));
            List<String> xValues = new ArrayList<>();
            List<Float> resultValues = new ArrayList<>();
            List<Float> tempValues = new ArrayList<>();
            boolean isNeedTemperature = item.type_key.contains("sf6yl");
            String key = null;
            if ("Y".equals(item.val)) {
                key = "val";
            }
            if ("Y".equals(item.val_a)) {
                key = "A";
            }
            if ("Y".equals(item.val_b)) {
                key = "B";
            }
            if ("Y".equals(item.val_c)) {
                key = "C";
            }
            if ("Y".equals(item.val_o)) {
                key = "O";
            }
            for (DbModel model : resultModel) {
                String val = model.getString(key);
                String temperature = model.getString("temperature");
                if (!TextUtils.isEmpty(val) && !("-1".equalsIgnoreCase(val)) && !"0".equals(temperature)) {
                    resultValues.add(Float.valueOf(val));
                    tempValues.add(Float.valueOf(temperature));
                    xValues.add(DateUtils.getFormatterTime(model.getString("time"), "MM/dd"));
                }
            }
            List<LineSet> yValues = new ArrayList<>();

            if (!isNeedTemperature) {
                yValues.add(new LineSet(item.description.replace("(" + item.unit + ")", ""), resultValues));
                ChartDialog.getInstance().showLineChartDialog(context, title, "时间", "数值(" + item.unit + ")", xValues,
                        yValues, Arrays.asList(R.color.xs_global_base_color));
            } else {
                yValues.add(new LineSet("压力", resultValues));
                yValues.add(new LineSet("温度", tempValues));
                ChartDialog.getInstance().showLineChartDialog(context, title, "时间", "数值(MPa/℃)", xValues, yValues,
                        Arrays.asList(R.color.xs_global_base_color, R.color.xs_light_red));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
