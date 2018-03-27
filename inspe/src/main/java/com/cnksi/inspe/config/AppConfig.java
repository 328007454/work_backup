package com.cnksi.inspe.config;

import com.cnksi.core.application.CoreApplication;
import com.cnksi.inspe.BuildConfig;

import org.xutils.x;

/**
 * App配置类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 12:42
 */
public class AppConfig extends CoreApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);

        if (BuildConfig.DEBUG) {
            x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能
        }
    }
}
