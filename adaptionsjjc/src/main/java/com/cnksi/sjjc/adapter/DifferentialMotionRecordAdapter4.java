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
import com.cnksi.sjjc.databinding.InfraedThermometerItemBinding;

import java.util.List;

public class DifferentialMotionRecordAdapter4 extends BaseLinearBindingAdapter<InfraedThermometerItemBinding, CdbhclValue> {


    private List<CdbhclValue> valueList;


    public DifferentialMotionRecordAdapter4(Context context, List<CdbhclValue> data, int layoutId, LinearLayout linearLayout) {
        super(context, data, linearLayout, layoutId);
        this.valueList = data;
        notifyDataSetChanged();

    }


    @Override
    public void convert(InfraedThermometerItemBinding holder, CdbhclValue item, int position) {
        holder.tvDefectDegreeItem.setText(item.getDeviceName());
        final EditText editText = holder.etTestInstrument;
        editText.setHint("请输入大差流值");
        editText.setText(TextUtils.isEmpty(item.getValue()) ? "" : item.getValue());
        editText.addTextChangedListener(new MyTextWatcher(item.getId(), position));
    }

    class MyTextWatcher implements TextWatcher {
        private int position;

        public MyTextWatcher(String deviceId, int position) {
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


}
