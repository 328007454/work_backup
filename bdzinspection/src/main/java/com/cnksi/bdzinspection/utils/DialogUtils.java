package com.cnksi.bdzinspection.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;

public class DialogUtils {

    private static Dialog dialog = null;

    /**
     * 创建dialog
     *
     * @param context
     * @param parent     父控件 一般为所在Activity或Fragment的根目录
     * @param resource   布局文件id
     * @param holder     ViewHolder
     * @param cancelable 是否可以点击dialog以外的地方dismissdialog
     * @return Dialog
     */
    public static Dialog createDialog(Context context, ViewGroup parent, int resource, Object holder, int width, int height, boolean cancelable) {
        dialog = new android.app.Dialog(context, R.style.XS_DialogStyle);
        dialog.setCanceledOnTouchOutside(cancelable);
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        dialog.setContentView(view);
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (width > 0) {
            lp.width = width;
        }
        if (height > 0) {
            lp.height = height;
        }
        mWindow.setAttributes(lp);
        // 添加动画
        mWindow.setWindowAnimations(R.style.XS_DialogAnim);
        return dialog;
    }

    /**
     * 创建dialog
     *
     * @param context
     * @param parent   父控件 一般为所在Activity或Fragment的根目录
     * @param resource 布局文件id
     * @param holder   ViewHolder
     * @return Dialog
     */
    public static Dialog createDialog(Context context, ViewGroup parent, int resource, Object holder, int width, int height) {
        return createDialog(context, parent, resource, holder, width, height, false);
    }

    public static Dialog createDialog(Context context, int resource, Object holder) {
        return createDialog(context, null, resource, holder, 0, 0);
    }

    /**
     * 提示dialog
     *
     * @param mRootContainer
     * @param dialogContent
     * @param mOnclickListener
     */
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, CharSequence dialogContent, OnViewClickListener mOnclickListener) {
        showSureTipsDialog(mActivity, mRootContainer, null, dialogContent, null, null, mOnclickListener);
    }

    /**
     * 提示dialog
     *
     * @param mRootContainer
     * @param dialogContent
     * @param sureText
     * @param cancelText
     * @param mOnclickListener
     */
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, String dialogContent, String sureText, String cancelText, OnViewClickListener mOnclickListener) {
        showSureTipsDialog(mActivity, mRootContainer, null, dialogContent, sureText, cancelText, mOnclickListener);
    }

    /**
     * 提示dialog
     *
     * @param mRootContainer
     * @param dialogTitle
     * @param dialogContent
     * @param sureText
     * @param cancelText
     * @param mOnclickListener
     */
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, String dialogTitle, CharSequence dialogContent, String sureText, String cancelText, OnViewClickListener mOnclickListener) {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        XsDialogTipsBinding tipsBinding = XsDialogTipsBinding.inflate(LayoutInflater.from(mActivity));
        createDialog(mActivity, tipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipsBinding.tvDialogTitle.setText(TextUtils.isEmpty(dialogTitle) ? mActivity.getString(R.string.xs_dialog_tips_str) : dialogTitle);
        tipsBinding.tvDialogContent.setText(dialogContent);
        tipsBinding.btnSure.setText(TextUtils.isEmpty(sureText) ? mActivity.getString(R.string.xs_yes_str) : sureText);
        tipsBinding.btnCancel.setText(TextUtils.isEmpty(cancelText) ? mActivity.getString(R.string.xs_no_str) : cancelText);
        dialog.show();
        tipsBinding.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
            dialog = null;
        });
        tipsBinding.btnSure.setOnClickListener(view -> {
            if (mOnclickListener != null) {
                mOnclickListener.onClick(view);
            }
        });
    }

    /**
     * @author yj
     * 通过databing来生成Dialog
     * 2016/8/10 11:40
     */
    public static Dialog createDialog(Context context, View view, int width, int height) {
        Dialog dialog = new Dialog(context, R.style.XS_DialogStyle);
       dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
        windowParams.width = width;
        windowParams.height = height;
        dialogWindow.setAttributes(windowParams);

        return dialog;
    }


    /**
     * 创建dialog
     *
     * @param context
     * @param parent     父控件 一般为所在Activity或Fragment的根目录
     * @param
     * @param holder     ViewHolder
     * @param cancelable 是否可以点击dialog以外的地方dismissdialog
     * @return Dialog
     * 不需要用xutils注解来注解控件
     */
    public static Dialog createDialog1(Context context, ViewGroup parent, ViewHolder holder, int width, int height, boolean cancelable) {
        dialog = new android.app.Dialog(context, R.style.XS_DialogStyle);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.setContentView(holder.getRootView());
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (width > 0) {
            lp.width = width;
        }
        if (height > 0) {
            lp.height = height;
        }
        mWindow.setAttributes(lp);
        // 添加动画
        mWindow.setWindowAnimations(R.style.XS_DialogAnim);
        return dialog;
    }


}
