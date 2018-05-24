package com.cnksi.sjjc.view.gztz;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.view.UnderLineLinearLayout;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/6 19:34
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SelectTimeGroup extends UnderLineLinearLayout {

    protected TextView tvName, tvValue;

    public SelectTimeGroup(Context context) {
        this(context, null);
    }

    public SelectTimeGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectTimeGroup(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setDrawUnderLine(true);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_time, this, true);
        tvValue = (TextView) findViewById(R.id.tv_value);
        tvName = (TextView) findViewById(R.id.tv_name);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SelectTimeGroup);
        int type = 0;
        if (attributes != null) {
            type = attributes.getInt(R.styleable.SelectTimeGroup_select_type, 0);
            String name = attributes.getString(R.styleable.SelectTimeGroup_title_str);
            tvName.setText(name);
            String str = attributes.getString(R.styleable.SelectTimeGroup_select_hint_str);
            str = StringUtilsExt.nullTo(str, "请选择");
            tvValue.setHint(str);
        }
        switch (type) {
            case 0:
                tvValue.setOnClickListener(v -> DialogUtils.showDatePickerDialog((Activity) context, new DialogUtils.DialogItemClickListener() {
                    @Override
                    public void confirm(String result, int position) {
                        tvValue.setText(result);
                    }
                }));
                break;
            case 1:
                tvValue.setOnClickListener(v -> DialogUtils.showDatePickerDialog((Activity) context, true, new DialogUtils.DialogItemClickListener() {
                    @Override
                    public void confirm(String result, int position) {
                        tvValue.setText(result);
                    }
                }));
                break;
            case 2:
                tvValue.setOnClickListener(v -> DialogUtils.showTimePickerDialog((Activity) context, false, (result, position) -> tvValue.setText(result)));
                break;
            case 3:
                tvValue.setOnClickListener(v -> DialogUtils.showTimePickerDialog((Activity) context, true, (result, position) -> tvValue.setText(result)));
        }
    }

    public String getValueStr() {
        return tvValue.getText().toString();
    }

    public void setValueStr(String valueStr) {
        tvValue.setText(valueStr);
    }
}
