package com.cnksi.sjjc.view.gztz;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cnksi.sjjc.databinding.GztzItemAddSubBinding;
import com.cnksi.sjjc.util.CalcUtils;


/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/8/24 19:57
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class InputCountView extends LinearLayout {
    public InputCountView(Context context) {
        this(context, null);
    }

    private float mMax = Integer.MAX_VALUE;
    private float mMin = Integer.MIN_VALUE;
    GztzItemAddSubBinding binding;
    private int unableColor = Color.parseColor("#e1e4e4");
    private int enableColor = Color.parseColor("#1bbc9b");

    public InputCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputCountView(Context context, @Nullable final AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = GztzItemAddSubBinding.inflate(LayoutInflater.from(getContext()), this, true);
        binding.btAdd.setOnClickListener(v -> add(1));
        binding.btSub.setOnClickListener(v -> add(-1));
    }

    public Float getResult() {
        try {
            return Float.valueOf(binding.etValue.getText().toString());
        } catch (Exception e) {
            return null;
        }
    }

    public void setMax(float mMax) {
        if (mMax < mMin) {
            throw new RuntimeException("max value can't <= min value");
        }
        this.mMax = mMax;
    }

    public void setMin(float mMin) {
        if (mMin > mMax) {
            throw new RuntimeException("min value can't >= max value");
        }
        this.mMin = mMin;
    }

    public void add(float t) {
        String s = binding.etValue.getText().toString();
        float rs = CalcUtils.String2Float(s);
        rs += t;
        rs = Math.max(rs, mMin);
        rs = Math.min(rs, mMax);
        binding.etValue.setText(rs + "");
        binding.btAdd.setTextColor(mMax == rs ? unableColor : enableColor);
        binding.btSub.setTextColor(mMin == rs ? unableColor : enableColor);
    }

    public void setFirst(float t) {
        binding.etValue.setText(t + "");
        binding.btAdd.setTextColor(mMax == t ? unableColor : enableColor);
        binding.btSub.setTextColor(mMin == t ? unableColor : enableColor);
    }

}
