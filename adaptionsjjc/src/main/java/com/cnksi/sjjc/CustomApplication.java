package com.cnksi.sjjc;

import com.cnksi.common.CommonApplication;
import com.cnksi.common.Config;
import com.cnksi.common.utils.XZip;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/20
 */
public class CustomApplication extends CommonApplication {
    private static int DataVersion = 13;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void copyAssetsToSDCard() {
        if (PreferencesUtils.get("DataVersion", 0) < DataVersion) {
            ExecutorManager.executeTaskSerially(() -> {
                if (BuildConfig.HAS_WEB_ASSETS) {
                    FileUtils.deleteAllFiles(Config.WWWROOT_FOLDER);
                    if (FileUtils.copyAssetsToSDCard(CustomApplication.this, Config.WWWROOT_FOLDER, "src/main/assets-web/www")) {
                        try {
                            XZip.UnZipFolder(Config.WWWROOT_FOLDER + "www.zip", Config.WWWROOT_FOLDER);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    FileUtils.copyAssetsToSDCard(CustomApplication.this, Config.DATABASE_FOLDER, "database");
                }
                PreferencesUtils.put("DataVersion", DataVersion);
            });
        }
    }
}
