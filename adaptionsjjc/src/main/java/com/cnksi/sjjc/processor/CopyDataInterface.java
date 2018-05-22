package com.cnksi.sjjc.processor;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.cnksi.common.model.Device;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.sjjc.bean.DevicePart;
import com.cnksi.sjjc.bean.DeviceStandards;
import com.cnksi.sjjc.bean.Lookup;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Standards;
import com.cnksi.common.model.Task;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.service.DevicePartService;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.StandardService;
import com.cnksi.sjjc.util.DateUtils;
import com.cnksi.sjjc.view.ChartDialog;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
     * 显示历史纪录对话框
     *
     * @param activity
     * @param mStandardModel
     * @param mCurrentDevice
     * @param currentBdzId
     */
    public abstract void showHistoryDialog(Activity activity, DbModel mStandardModel, DbModel mCurrentDevice, String currentBdzId);


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
        if (null == mDevicePartList)
            mDevicePartList = new ArrayList<DevicePart>();
        List<DevicePart> modelPartList = DevicePartService.getInstance()
                .getDevicePartList(mCurrentDevice.getString("deviceid"));
        if (null != modelPartList)
            mDevicePartList.addAll(modelPartList);
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
        CustomApplication.getInstance().getDbManager().update(Task.class, WhereBuilder.b(Task.TASKID, "=", taskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
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
            if (dataList != null)
                for (DbModel m : dataList) {
                    copyMap.put(m.getString(Device.DEVICEID), true);
                }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return copyMap;
    }

    /**
     * 压力检测历史曲线框
     *
     * @param activity
     * @param mStandardModel
     * @param mCurrentDevice
     * @param currentBdzId
     */
    protected void showPressHistoryDialog(Activity activity, DbModel mStandardModel, DbModel mCurrentDevice, String currentBdzId) {
        List<DbModel> modelList = null;
        try {
            modelList = findStardHistoryRecord(mCurrentDevice.getString(Device.DEVICEID), mStandardModel.getString(Standards.STAID), currentBdzId);
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
        for (int i = 0, count = modelList.size(); i < count; i++) {
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

        ChartDialog.getInstance().showLineChartDialog(activity, activity.getString(R.string.data_history_record_format_str,
                mStandardModel.getString(DeviceStandards.DESCRIPTION)), "时间", "数值(MPa/℃)", xLabe, yList, Arrays.asList(R.color.global_base_color, R.color.light_red));
    }


    public static void showHistory(Context context, CopyItem item) {
        boolean isSf6=item.type_key.contains("sf6yl");
        String sql = "SELECT case WHEN result.val  ISNULL THEN 0 ELSE  result.val END val,"
                + " case WHEN result.val_a  ISNULL THEN 0 ELSE  result.val_a END A,"
                + " case WHEN result.val_b  ISNULL THEN 0 ELSE  result.val_b END B,"
                + " case WHEN result.val_c  ISNULL THEN 0 ELSE  result.val_c END C,"
                + " case WHEN result.val_o  ISNULL THEN 0 ELSE  result.val_o END O,"
                + " case WHEN report.temperature ISNULL  or report.temperature='' THEN 0 ELSE report.temperature END temperature,"
                + "	case WHEN result.update_time ISNULL THEN result.create_time ELSE result.update_time END time"
                + " FROM"
                + " copy_result result LEFT JOIN report report ON result.reportid = report.reportid WHERE result.bdzid = ?"
                + " AND report.reportid NOT IN(SELECT DISTINCT(reportid) FROM report rp WHERE rp.taskid in (SELECT DISTINCT(taskid) FROM task tk WHERE tk.STATUS='undo'))"
                + " AND result.deviceid = ? AND result.item_id=?"
                + " ORDER BY time";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", item.bdzid));
        sqlInfo.addBindArg(new KeyValue("", item.deviceid));
        sqlInfo.addBindArg(new KeyValue("", item.id));
        try {
            List<DbModel> resultModel = CustomApplication.getInstance().getDbManager().findDbModelAll(sqlInfo);

            if (resultModel == null || resultModel.size() < 1) {
                ToastUtils.showMessage("当前没有历史抄录记录");
                return;
            }
            String title = context.getString(R.string.data_history_record_format_str, item.description.replace("(" + item.unit + ")", ""));
            List<String> xValues = new ArrayList<>();
            List<Float> resultValues = new ArrayList<>();
            List<Float> tempValues = new ArrayList<>();
            String val="";
            for (DbModel model : resultModel) {
                if ("Y".equals(item.val))
                    val=model.getString("val");
                if ("Y".equals(item.val_a))
                    val=model.getString("A");
                if ("Y".equals(item.val_b))
                    val=model.getString("B");
                if ("Y".equals(item.val_c))
                    val=model.getString("C");
                if ("Y".equals(item.val_o))
                    val=model.getString("O");
                if (!TextUtils.isEmpty(val)&&!("-1".equalsIgnoreCase(val))){
                    resultValues.add(Float.valueOf(val));
                    xValues.add(DateUtils.formatDateTime(model.getString("time"), "MM/dd"));
                    if (isSf6) {
                        tempValues.add(model.getFloat("temperature"));
                    }
                }
            }

            if (resultValues.size()<1)
            {
                ToastUtils.showMessage("当前没有历史抄录记录");
                return;
            }
            List<ChartDialog.LineSet> yValues = new ArrayList<>();


            if (!isSf6) {
                yValues.add(new ChartDialog.LineSet(item.description.replace("(" + item.unit + ")", ""), resultValues));
                ChartDialog.getInstance().showLineChartDialog(context, title, "时间", "数值(" + item.unit + ")", xValues, yValues, Arrays.asList(R.color.global_base_color));
            } else {
                yValues.add(new ChartDialog.LineSet("压力", resultValues));
                yValues.add(new ChartDialog.LineSet("温度", tempValues));
                ChartDialog.getInstance().showLineChartDialog(context, title, "时间", "数值(MPa/℃)", xValues, yValues, Arrays.asList(R.color.global_base_color, R.color.light_red));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public abstract String getCopyType();
}
