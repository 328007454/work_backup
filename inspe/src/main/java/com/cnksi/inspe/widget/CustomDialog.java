package com.cnksi.inspe.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.inspe.R;

/**
 * Created by Mr.K on 2018/4/17.
 */

public class CustomDialog {

    /**
     *
     * 尽量采用databinding 传递view过来  可以参看布局layout:R.layout.dialog_listview_layout
     *
     * @param context
     * @param view
     * @param width
     * @param height
     * @return
     */
    public static Dialog createDialog(Context context, View view, int width, int height) {
        Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
        if (width == 0) {
            windowParams.width =   ScreenUtils.getScreenWidth(context) * 7 / 9;
        } else {
            windowParams.width = width;
        }
        if (height == 0) {
            windowParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            windowParams.height = height;
        }
        dialogWindow.setAttributes(windowParams);

        return dialog;
    }

}
