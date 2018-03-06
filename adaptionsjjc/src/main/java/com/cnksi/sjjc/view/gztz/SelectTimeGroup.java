package com.cnksi.sjjc.view.gztz;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.cnksi.core.view.CustomerDialog;
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

    private TextView tvName, tvValue;
    public SelectTimeGroup(Context context) {
        this(context, null);
    }

    public SelectTimeGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectTimeGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setDrawUnderLine(true);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_time, this, true);
        tvValue = (TextView) findViewById(R.id.tv_value);
        tvName = (TextView) findViewById(R.id.tv_name);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SelectTimeGroup);
        if (attributes != null) {
            tvName.setText(attributes.getString(R.styleable.SelectTimeGroup_title_str));
            tvValue.setHint(attributes.getString(R.styleable.SelectTimeGroup_select_hint_str));
        }
        CustomerDialog.showDatePickerDialog()
    }
}
