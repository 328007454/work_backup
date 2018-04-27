package com.cnksi.inspe.utils;

import android.view.View;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/19 09:59
 */
public final class ScoreUtils {
    private View addView, minuxView;//增减View
    private int SCAN;//数据放大倍数
    private int value;//当前值
    private int max;//可用最大值
    private int unit;//增减单位值

    public ScoreUtils(View addView, View minuxView, int scan) {
        this.addView = addView;
        this.minuxView = minuxView;
        this.SCAN = scan;
    }

    //显示Value，不对valueF进行处理
    public void showValue(float max, float unit, float valueF) {
        this.max = (int) (max * SCAN);
        this.unit = (int) (unit * SCAN);
        this.value = (int) (valueF * SCAN);

        show(this.value);
    }

    //设置value，需要对value进行处理
    public void setValue(float max, float unit, float valueF) {
        this.max = (int) (max * SCAN);
        this.unit = (int) (unit * SCAN);
        show((int) (valueF * SCAN));
    }


    private void show(int nextValue) {
        boolean isRun = true;
        if (nextValue > max) {
            isRun = false;
            addView.setEnabled(false);
        } else if (nextValue == max) {
            addView.setEnabled(false);
        } else {
            addView.setEnabled(true);
        }

        if (nextValue < unit) {
            isRun = false;
            minuxView.setEnabled(false);
        } else if (nextValue == unit) {
            minuxView.setEnabled(false);
        } else {
            minuxView.setEnabled(true);
        }

        if (isRun) {
            value = nextValue;
        }
    }

    public void add() {
        show(this.value + unit);
    }

    public void minux() {
        show(this.value - unit);
    }

    /** 获取当前ValueINR */
    public int getValueInt() {
        return this.value;
    }

    /** 获取当前ValueFloat */
    public String getValueString() {
        return String.format("%.2f", value * 1.f / SCAN);
    }

}
