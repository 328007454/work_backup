package com.cnksi.sjjc.view.gztz;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.common.view.UnderLineLinearLayout;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/3/6 17:07
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SingleRadioGroup extends UnderLineLinearLayout {

    private RadioButton buttonA, buttonB;
    private TextView tvName;

    public SingleRadioGroup(Context context) {
        this(context, null);
    }

    public SingleRadioGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleRadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        setDrawUnderLine(true);
        setLineColor(getResources().getColor(R.color.line_color));
        LayoutInflater.from(context).inflate(R.layout.item_single_radio, this, true);
        buttonA = findViewById(R.id.bt_a);
        buttonB = findViewById(R.id.bt_b);
        tvName = findViewById(R.id.tv_name);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SingleRadioGroup);
        if (attributes != null) {
            String s = attributes.getString(R.styleable.SingleRadioGroup_select_str);
            String aStr = "是", bStr = "否";
            if (!TextUtils.isEmpty(s)) {
                String[] strs = s.split("\\|");
                if (strs.length >= 2) {
                    aStr = strs[0];
                    bStr = strs[1];
                }
            }
            buttonB.setText(bStr);
            buttonA.setText(aStr);
            String title = attributes.getString(R.styleable.SingleRadioGroup_title_str);
            tvName.setText(title);
        }
    }

    public String getValueStr() {
        if (buttonA.isChecked()) {
            return buttonA.getText().toString();
        }
        if (buttonB.isChecked()) {
            return buttonB.getText().toString();
        }
        return null;
    }

    public void setValueStr(String str) {
        if (TextUtils.equals(buttonA.getText().toString(), str)) {
            buttonA.setChecked(true);
        }
        if (TextUtils.equals(buttonB.getText().toString(), str)) {
            buttonB.setChecked(true);
        }
    }

    public void setOnCheckChangeListener(CompoundButton.OnCheckedChangeListener listener, boolean isA) {
        if (isA) {
            buttonA.setOnCheckedChangeListener(listener);
        } else {
            buttonB.setOnCheckedChangeListener(listener);
        }
    }

}
