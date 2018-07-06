package com.cnksi.common.enmu;

import android.text.TextUtils;

/**
 * Created by Mr.K on 2018/7/6.
 */

public enum WeekTime {

    today("今日"), week("本周"), month("本月"), layoff("逾期");
    public final String value;

    WeekTime(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static  String getTyeName(String value) {
        String type = WeekTime.today.name();

        if (TextUtils.equals(value, WeekTime.today.value)) {
            return WeekTime.today.name();
        } else if (TextUtils.equals(value, WeekTime.week.value)) {
            return WeekTime.week.name();
        } else if (TextUtils.equals(value, WeekTime.month.value)) {
            return WeekTime.month.name();
        } else if (TextUtils.equals(value, WeekTime.layoff.value)) {
            return WeekTime.layoff.name();
        }
        return type;
    }
}
