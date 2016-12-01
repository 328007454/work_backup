package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.ReportCdbhcl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifferentialMotionRecordAdapter3 extends BaseLinearLayoutAdapter<CopyItem> {

    //根据设备id，以及变电站id
    private Map<String, ReportCdbhcl> recordMap;

    private List<CopyItem> copyItems;
    private ReportCdbhcl report;
    private String value;

    public void setRecordList(Map<String, ReportCdbhcl> recordMap) {
        this.recordMap = recordMap;
        if (recordMap == null) {
            this.recordMap = new HashMap<String, ReportCdbhcl>();
        }
        notifyDataSetChanged();
    }

    public DifferentialMotionRecordAdapter3(Context context, List<CopyItem> data, int layoutId, LinearLayout linearLayout) {
        super(context, data ,linearLayout,layoutId);
        this.copyItems = (List<CopyItem>) data;
    }

    @Override
    public void convert(ViewHolder holder, final CopyItem item, final int position) {
        holder.getView(R.id.view_line).setVisibility(View.VISIBLE);
        holder.setText(R.id.tv_defect_degree_item, item.device_name);
        final EditText editText = holder.getView(R.id.et_test_instrument);
        editText.setHint("请输入大差流值");
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        if (null != recordMap && !recordMap.isEmpty()) {
            ReportCdbhcl report = recordMap.get(item.deviceid);
            value = (report == null) ? "" : recordMap.get(item.deviceid).dclz;
            editText.setText(value);
        }
        editText.addTextChangedListener(new MyTextWatcher(item.deviceid));
    }

    class MyTextWatcher implements TextWatcher {
        private EditText mEditText;
        private String deviceId;

        public MyTextWatcher(String deviceId) {
            this.deviceId = deviceId;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (null != recordMap && !recordMap.isEmpty()) {
                report = recordMap.get(deviceId);
                if (report == null) {
                    report = new ReportCdbhcl();
                    report.dclz = s.toString();
                } else {
                    report.dclz = s.toString();
                }
                recordMap.put(deviceId, report);
            } else {
                ReportCdbhcl report = new ReportCdbhcl();
                report.dclz = s.toString();
                recordMap.put(deviceId, report);
            }
        }
    }


//    /**
//     * 保存数据根据，当每个edittext失去焦点后保存数据到数据库
//     */
//    public void saveCopyValue() {
//        ReportCdbhcl reportCdbhcl = null;
//        if (null != recordMap && !recordMap.isEmpty()) {
//            reportCdbhcl = recordMap.get(item.deviceid);
//        }
//        if (null == reportCdbhcl) {
//            String reportId = PreferencesUtils.getString(context, Config.CURRENT_REPORT_ID, "");
//            String bdzName = PreferencesUtils.getString(context, Config.CURRENT_BDZ_NAME, "");
//            reportCdbhcl = new ReportCdbhcl(reportId, mCurrentDevice.bdzid, bdzName, mCurrentDevice.deviceid, mCurrentDevice.name, DateUtils.getCurrentLongTime());
//        }
//        reportCdbhcl.dclz = mCurrentEditText.getText().toString();
//        try {
//            ReportCdbhclService.getIntance().saveOrUpdate(reportCdbhcl);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }

//    }

    public Map<String, ReportCdbhcl> getMap() {

        return recordMap;
    }

}
