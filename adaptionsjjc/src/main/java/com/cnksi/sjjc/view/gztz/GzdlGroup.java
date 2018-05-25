package com.cnksi.sjjc.view.gztz;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.cnksi.common.utils.CalcUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.inter.SimpleTextWatcher;
import com.cnksi.common.view.UnderLineLinearLayout;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/8 16:25
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GzdlGroup extends UnderLineLinearLayout {

    private EditText eddl;
    private InputCountView countView;
    private float old = 0;
    private boolean isFirstIn = true;

    public GzdlGroup(Context context) {
        this(context, null);
    }

    public GzdlGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GzdlGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDrawUnderLine(true);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_gzdl, this, true);
        eddl = findViewById(R.id.et_gzdl);
        countView = findViewById(R.id.ljz);
        countView.setMin(0.0f);
        eddl.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                float newF = CalcUtils.String2Float(s.toString());
                countView.add(newF - old);
                old = newF;
            }
        });
    }

    public void setLjz(float ljz) {
        countView.setFirst(ljz);
    }

    public void setGzdl(float gzdl) {
        eddl.setText(String.valueOf(gzdl));
    }

    public String getGzdl() {
        return eddl.getText().toString();
    }

    public String getLjz() {
        return countView.getResult() == null ? "" : countView.getResult() + "";
    }
}
