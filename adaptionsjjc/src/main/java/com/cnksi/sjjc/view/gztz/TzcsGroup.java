package com.cnksi.sjjc.view.gztz;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.DialogModifyLjtzcsBinding;
import com.cnksi.sjjc.util.CalcUtils;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.view.UnderLineLinearLayout;

import java.util.HashMap;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/8 15:20
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class TzcsGroup extends UnderLineLinearLayout {

    private TextView a, b, c, o;
    private int intA = 0, intB = 0, intC = 0, intO = 0;
    private int[] visbles = new int[]{0, 0, 0, 0};

    public TzcsGroup(Context context) {
        this(context, null);
    }

    public TzcsGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TzcsGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setDrawUnderLine(true);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_tzcs, this, true);
        a = (TextView) findViewById(R.id.ljtzcs_a);
        b = (TextView) findViewById(R.id.ljtzcs_b);
        c = (TextView) findViewById(R.id.ljtzcs_c);
        o = (TextView) findViewById(R.id.ljtzcs_o);
        findViewById(R.id.ljtzcs_modify).setOnClickListener(v -> showModifyDialog());
        bindValue();
    }

    DialogModifyLjtzcsBinding viewHolder = null;
    Dialog dialog = null;

    private void showModifyDialog() {
        if (dialog == null) {
            int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
            viewHolder = DialogModifyLjtzcsBinding.inflate(LayoutInflater.from(getContext()));
            dialog = DialogUtils.creatDialog(getContext(), viewHolder.getRoot(), dialogWidth, -2);
            viewHolder.btnCancel.setOnClickListener(v -> dialog.dismiss());
            viewHolder.btnSure.setOnClickListener(v -> {
                intA = CalcUtils.toInt(viewHolder.gxtzcsA.getText().toString(), 0);
                intB = CalcUtils.toInt(viewHolder.gxtzcsB.getText().toString(), 0);
                intC = CalcUtils.toInt(viewHolder.gxtzcsC.getText().toString(), 0);
                intO = CalcUtils.toInt(viewHolder.gxtzcsO.getText().toString(), 0);
                bindValue();
                dialog.dismiss();
            });
            viewHolder.llA.setVisibility(visbles[0] == 1 ? VISIBLE : GONE);
            viewHolder.llB.setVisibility(visbles[1] == 1 ? VISIBLE : GONE);
            viewHolder.llC.setVisibility(visbles[2] == 1 ? VISIBLE : GONE);
            viewHolder.llO.setVisibility(visbles[3] == 1 ? VISIBLE : GONE);
        }
        viewHolder.gxtzcsA.setText(getIntStr(a));
        viewHolder.gxtzcsC.setText(getIntStr(c));
        viewHolder.gxtzcsB.setText(getIntStr(b));
        viewHolder.gxtzcsO.setText(getIntStr(o));
        dialog.show();
    }

    public void setXb(int[] visbles) {
        this.visbles = visbles;
        a.setVisibility(visbles[0] == 1 ? VISIBLE : GONE);
        b.setVisibility(visbles[1] == 1 ? VISIBLE : GONE);
        c.setVisibility(visbles[2] == 1 ? VISIBLE : GONE);
        o.setVisibility(visbles[3] == 1 ? VISIBLE : GONE);
    }

    private void bindValue() {
        a.setText("A:" + intA);
        b.setText("B:" + intB);
        c.setText("C:" + intC);
        o.setText("O:" + intO);
    }

    public void setValuesStr(String json) {
        if (TextUtils.isEmpty(json)) return;
        JSONObject jo = JSON.parseObject(json);
        intA = jo.getIntValue("A");
        intB = jo.getIntValue("B");
        intC = jo.getIntValue("C");
        intO = jo.getIntValue("O");
        bindValue();
    }

    public void addA(int x) {
        a.setText("A:" + (intA + x));
    }

    public void addB(int x) {
        b.setText("B:" + (intB + x));
    }

    public void addC(int x) {
        c.setText("C:" + (intC + x));
    }

    public void addO(int x) {
        o.setText("O:" + (intO + x));
    }

    private String getIntStr(TextView s) {
        return s.getText().toString().split(":")[1];
    }

    public String getValuesStr() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("A", CalcUtils.toInt(a.getText().toString().replace("A:",""), 0));
        map.put("B", CalcUtils.toInt(b.getText().toString().replace("B:",""), 0));
        map.put("C", CalcUtils.toInt(c.getText().toString().replace("C:",""), 0));
        map.put("O", CalcUtils.toInt(o.getText().toString().replace("O:",""), 0));
        return JSON.toJSONString(map);
    }
}
