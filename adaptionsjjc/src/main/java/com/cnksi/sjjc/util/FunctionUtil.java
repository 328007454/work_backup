package com.cnksi.sjjc.util;

import android.content.Context;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * @author lyndon
 * @version 1.0
 * @date 2016/7/7
 */
public class FunctionUtil extends FunctionUtils {


    /**
     * @param context
     * @return
     */
    public static String getCurrentImageName(Context context) {
        //获取图片目录
        String picPrefix = PreferencesUtils.get(context, Config.PICTURE_PREFIX, "");
        //生成图片名称
        SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat6, Locale.CHINA);
        String uuidStr = UUID.randomUUID().toString().replace(CoreConfig.DASH_SEPARATOR, "");
        if (uuidStr.length() > 8) {
            uuidStr = uuidStr.substring(0, 8);
        }
        String imageName = String.valueOf(formatter.format(new Date()) + uuidStr + CoreConfig.IMAGE_JPG_POSTFIX);
        return picPrefix + imageName;
    }

    public static String getCurrentImageName(Context context, String imageName) {
        //获取图片目录
        String picPrefix = PreferencesUtils.get(context, Config.PICTURE_PREFIX, "");
        return picPrefix + imageName;
    }

}