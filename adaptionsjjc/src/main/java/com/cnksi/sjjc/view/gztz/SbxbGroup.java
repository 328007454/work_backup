package com.cnksi.sjjc.view.gztz;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.view.UnderLineLinearLayout;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 17:28
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SbxbGroup extends UnderLineLinearLayout {

    private CheckBox a, b, c, o;

    public SbxbGroup(Context context) {
        this(context, null);
    }

    public SbxbGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SbxbGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setDrawUnderLine(true);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_sbxb, this, true);
        a = findViewById(R.id.rb_a);
        b = findViewById(R.id.rb_b);
        c = findViewById(R.id.rb_c);
        o = findViewById(R.id.rb_o);
    }

    public String getValueStr() {
        StringBuilder builder = new StringBuilder();
        if (a.isChecked()) {
            builder.append("A,");
        }
        if (b.isChecked()) {
            builder.append("B,");
        }
        if (c.isChecked()) {
            builder.append("C,");
        }
        if (o.isChecked()) {
            builder.append("O,");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public void setValueStr(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        String[] strings = s.split(",");
        for (String k : strings) {
            if ("A".equals(k)) {
                a.setChecked(true);
            }
            if ("B".equals(k)) {
                b.setChecked(true);
            }
            if ("C".equals(k)) {
                c.setChecked(true);
            }
            if ("O".equals(k)) {
                o.setChecked(true);
            }
        }
    }
}
