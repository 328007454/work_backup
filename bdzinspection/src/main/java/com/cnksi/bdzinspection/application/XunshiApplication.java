package com.cnksi.bdzinspection.application;

import android.app.Application;
import android.content.Context;

import com.cnksi.bdzinspection.utils.DisplayUtil;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.common.CommonApplication;
import com.cnksi.common.Config;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.nari.utils.NariDataManager;

public class XunshiApplication {


    public static CommonApplication getInstance() {
        return CommonApplication.getInstance();
    }


    public void init(Application application, Context context) {


        Config.COPY_MAX_DISTANCE = PreferencesUtils.get(Config.COPY_DISTANCE_KEY, Config.COPY_MAX_DISTANCE);
        NariDataManager.init(Config.NARI_BASEFOLDER);
        PreferencesUtils.init(context);
        DisplayUtil.getInstance().init(context);
        TTSUtils.init(context);

    }



    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }


}