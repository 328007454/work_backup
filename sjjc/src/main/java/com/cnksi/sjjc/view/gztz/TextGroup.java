package com.cnksi.sjjc.view.gztz;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.common.view.UnderLineLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/3/6 18:02
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class TextGroup extends UnderLineLinearLayout {

    private EditText editText;
    private TextView tvName;

    public TextGroup(Context context) {
        this(context, null);
    }

    public TextGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setDrawUnderLine(true);
        setPadding(AutoUtils.getPercentHeightSize(30), 0, 0, 0);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_text_group, this, true);
        editText = findViewById(R.id.et_input);
        tvName = findViewById(R.id.tv_name);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TextGroup);
        if (attributes != null) {
            String string = attributes.getString(R.styleable.TextGroup_title_str);
            if (!TextUtils.isEmpty(string)) {
                tvName.setText(string);
                editText.setHint("请输入");
            }
            string = attributes.getString(R.styleable.TextGroup_default_str);
            editText.setText(StringUtils.NullToDefault(string));
        }
    }

    public String getValueStr() {
        return editText.getText().toString();
    }

    public void setValueStr(String s) {
        if (s == null) {
            return;
        }
        editText.setText(s);
    }

    public void setMustInput(boolean isMust) {
        String name = tvName.getText().toString();
        if (isMust) {
            if (!name.startsWith("*")) {
                tvName.setText("*" + name);
            }
        } else {
            if (name.startsWith("*")) {
                tvName.setText(name.substring(1));
            }
        }
    }
}
