package com.cnksi.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.cnksi.common.R;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.view.ChartDialog;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年8月23日 上午11:39:57 TODO
 */
public class ShowCopyHistroyDialogUtils {








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
            List<DbModel> resultModel =CopyResultService.getInstance().findDbModelAll(sqlInfo);
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
            List<ChartDialog.LineSet> yValues = new ArrayList<>();
            if (!isNeedTemperature) {
                yValues.add(new ChartDialog.LineSet(item.description.replace("(" + item.unit + ")", ""), resultValues));
                ChartDialog.getInstance().showLineChartDialog(context, title, "时间", "数值(" + item.unit + ")", xValues,
                        yValues, Arrays.asList(R.color.xs_global_base_color));
            } else {
                yValues.add(new ChartDialog.LineSet("压力", resultValues));
                yValues.add(new ChartDialog.LineSet("温度", tempValues));
                ChartDialog.getInstance().showLineChartDialog(context, title, "时间", "数值(MPa/℃)", xValues, yValues,
                        Arrays.asList(R.color.xs_global_base_color, R.color.xs_light_red));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
