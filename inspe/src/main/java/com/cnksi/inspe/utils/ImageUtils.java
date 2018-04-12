package com.cnksi.inspe.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/12 16:12
 */
public final class ImageUtils {
    private ImageUtils() {

    }

    /**
     * 显示大图片
     *
     * @param context       Context
     * @param position      显示图片开始位置
     * @param mImageUrlList 图片地址(绝对地址)
     * @param isShowDelete  显示删除
     * @param isDeleteFile  显示删除确认
     * @return
     */
    public static boolean showImageDetails(Context context, int position, ArrayList<String> mImageUrlList,
                                           boolean isShowDelete, boolean isDeleteFile) {
        try {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(context.getPackageName(), "com.cnksi.sjjc.activity.ImageDetailsActivity");
            intent.setComponent(componentName);
            intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
            intent.putExtra(Config.CANCEL_IMAGEURL_LIST, false);//不显示照片名称
            if (mImageUrlList != null) {
                intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
            }
            intent.putExtra(Config.IS_DELETE_FILE, isDeleteFile);
            intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
            context.startActivity(intent);
            //context.startActivityForResult(intent, CANCEL_RESULT_LOAD_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
