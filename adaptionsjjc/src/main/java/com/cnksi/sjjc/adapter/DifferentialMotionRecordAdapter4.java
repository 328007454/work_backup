package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.CdbhclValue;
import com.cnksi.sjjc.bean.ReportCdbhcl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifferentialMotionRecordAdapter4 extends BaseLinearLayoutAdapter<CdbhclValue> {

    //根据设备id，以及变电站id
    private Map<String, ReportCdbhcl> recordMap;

    private List<CdbhclValue> valueList;
    private ReportCdbhcl report;
    private String value;


    public void setRecordList(Map<String, ReportCdbhcl> recordMap) {
        this.recordMap = recordMap;
        if (recordMap == null) {
            this.recordMap = new HashMap<String, ReportCdbhcl>();
        }
        notifyDataSetChanged();
    }

    public DifferentialMotionRecordAdapter4(Context context, List<CdbhclValue> data, int layoutId, LinearLayout linearLayout) {
        super(context, data, linearLayout, layoutId);
        this.valueList = data;
        notifyDataSetChanged();

    }

    @Override
    public void convert(ViewHolder holder, final CdbhclValue item, final int position) {
        holder.setText(R.id.tv_defect_degree_item, item.getDeviceName());
        final EditText editText = holder.getView(R.id.et_test_instrument);

        editText.setHint("请输入大差流值");
//        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.setText(TextUtils.isEmpty(item.getValue()) ? "" : item.getValue());
//        if (null != recordMap && !recordMap.isEmpty()) {
//            ReportCdbhcl report = recordMap.get(item.deviceid);
//            value = (report == null) ? "" : recordMap.get(item.deviceid).dclz;
//            editText.setText(value);
//        }
        editText.addTextChangedListener(new MyTextWatcher(item.getId(), position));
    }

    class MyTextWatcher implements TextWatcher {
        private EditText mEditText;
        private String deviceId;
        private int position;

        public MyTextWatcher(String deviceId, int position) {
            this.deviceId = deviceId;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = StringUtilsExt.getDecimalPoint(s.toString());
            valueList.get(position).setValue(value);
        }
    }


    public Map<String, ReportCdbhcl> getMap() {

        return recordMap;
    }

}
