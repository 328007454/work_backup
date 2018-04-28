package com.cnksi.workticket;

import android.os.Environment;

import com.cnksi.core.utils.SDCardUtils;

/**
 * Created by Mr.K on 2018/4/23.
 */

public class Config {

    /**
     * 手机存储目录
     */
    public static final String PHONE_DIRECTORY = Environment.getDataDirectory().getAbsolutePath();
    /**
     * 内置SD卡目录
     */
    public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 外置SD卡目录
     */
    public static final String EXTERNAL_SDCARD_DIRECTORY = null;
    public static final String BDZ_INSPECTION_FOLDER = (EXTERNAL_SDCARD_DIRECTORY != null ? EXTERNAL_SDCARD_DIRECTORY : ((SDCardUtils.isSDCardEnable() ? SDCARD_DIRECTORY : PHONE_DIRECTORY))) + "/BdzInspection/";
    public static final  String LOG = BDZ_INSPECTION_FOLDER+"log/";

}
