package com.cnksi.bdzinspection.utils;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/8/10 13:31
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public enum TQEnum {
    晴("01"),
    阴("02"),
    多云("03"),
    小雨("04"),
    中雨("05"),
    大雨("06"),
    暴雨("07"),
    雷("08"),
    霜冻("10"),
    小雪("11"),
    中雪("12"),
    大雪("13"),
    雨夹雪("14"),
    雾("15"),
    浓雾("16"),
    冰冻("17"),
    雹灾("18"),
    大风("19"),
    热带风暴("20"),
    强热带风暴("21"),
    龙卷风("22"),
    飑线风("23"),
    台风("24"),
    沙尘暴("25"),
    扬尘("26"),
    洪水("27"),
    泥石流("28"),
    地震("29"),
    暴雪("31"),
    冻雨("32"),
    霾("33"),
    雾霾("34"),
    其他("99");

    String code;

    TQEnum(String code) {
        this.code = code;
    }

    public static String findCode(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        for (TQEnum tqEnum : values()) {
            if (tqEnum.name().equals(s)) {
                return tqEnum.code;
            }
        }
        return 其他.code;
    }

    public static String[] getAllName() {
        ArrayList<String> rs = new ArrayList<>();
        for (TQEnum tqEnum : values()) {
            rs.add(tqEnum.name());
        }
        return rs.toArray(new String[1]);
    }


}
