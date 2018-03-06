package com.cnksi.sjjc.view.gztz;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.view.UnderLineLinearLayout;

import org.xutils.common.util.KeyValue;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/6 19:05
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SelectGroup extends UnderLineLinearLayout {

    private TextView tvName, tvValue;
    private KeyValue keyValue;

    public SelectGroup(Context context) {
        this(context, null);
    }

    public SelectGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setDrawUnderLine(true);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_select_group, this, true);
        tvValue = (TextView) findViewById(R.id.tv_value);
        tvName = (TextView) findViewById(R.id.tv_name);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SelectGroup);
        if (attributes != null) {
            tvName.setText(attributes.getString(R.styleable.SelectGroup_title_str));
            tvValue.setHint(attributes.getString(R.styleable.SelectGroup_select_hint_str));
        }
    }

    public void setSelectOnClickListener(View.OnClickListener onClickListener) {
        tvValue.setOnClickListener(onClickListener);
    }

    public KeyValue getValue() {
        return keyValue;
    }

    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
        tvValue.setText(keyValue.getValueStr());
    }
}
