package com.cnksi.sjjc.View;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by wastrel on 2016/7/7.
 */
public class MyValueFormatter implements ValueFormatter {


    private DecimalFormat mFormat;

    public MyValueFormatter() {

        this.mFormat = new DecimalFormat("###,###,###,###.####");
    }
    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
         return this.mFormat.format(v);
    }
}
