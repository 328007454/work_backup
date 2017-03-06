package com.cnksi.sjjc.util;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.datepicker.WheelMain;
import com.cnksi.sjjc.R;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Calendar;

public class DialogUtils {

    private static Dialog dialog = null;
    private static TipsViewHolder mTipsHolder;

    /**
     * 创建dialog
     *
     * @param context
     * @param holder     ViewHolder
     * @param cancelable 是否可以点击dialog以外的地方dismissdialog
     * @return Dialog
     */
    public static Dialog createDialog(Context context, ViewHolder holder, int width, int height, boolean cancelable) {
        return createDialog(context, holder.getRootView(), width, height, cancelable);
    }


    /**
     * 创建dialog
     *
     * @param context
     * @param holder     ViewHolder
     * @param cancelable 是否可以点击dialog以外的地方dismissdialog
     * @return Dialog
     */
    public static Dialog createDialog(Context context, ViewDataBinding holder, boolean cancelable) {
        return createDialog(context, holder.getRoot(), 0, 0, cancelable);
    }

    public static Dialog createDialog(Context context, View v, int width, int height, boolean cancelable) {
        dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.setContentView(v);
        Window mWindow = dialog.getWindow();
        if (width > 0 && height > 0) {

            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.height = height;
            lp.width = width;
            mWindow.setAttributes(lp);
        }
        // 添加动画
        mWindow.setWindowAnimations(R.style.DialogAnim);
        return dialog;
    }


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
        dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(cancelable);
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        x.view().inject(holder, view);
        dialog.setContentView(view);
        Window mWindow = dialog.getWindow();
        if (width != 0 || height != 0) {

            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.height = height;
            lp.width = width;
            mWindow.setAttributes(lp);
        }

        // 添加动画
        mWindow.setWindowAnimations(R.style.DialogAnim);
        return dialog;
    }

    /**
     * 创建dialog
     *
     * @param context
     * @param title
     * @param text
     * @param btnText    按键文本 数组必须大于2
     * @param listeners  点击事件 数组必须大于2
     * @param cancelable 是否可以点击dialog以外的地方dismissdialog
     * @return Dialog
     */
    public static Dialog createTipsDialog(Context context, String title, String text, String[] btnText, final View.OnClickListener[] listeners, boolean cancelable) {
        final Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(cancelable);
        View view = LayoutInflater.from(context).inflate(R.layout.tips_dialog, null, false);
        ((TextView) view.findViewById(R.id.tv_dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.tv_text)).setText(text);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnSure = (Button) view.findViewById(R.id.btn_confirm);
        btnCancel.setText("取消");
        btnSure.setText("确定");
        if (btnText != null) {
            if (btnText.length == 1) {
                btnSure.setText(btnText[0]);
            }
            if (btnText.length > 1) {
                btnCancel.setText(btnText[0]);
                btnSure.setText(btnText[1]);
            }
        }

        if (listeners != null) {
            if (listeners.length == 1) {
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listeners[0].onClick(v);
                        dialog.dismiss();
                    }
                });
            }
            if (listeners.length > 1) {
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listeners[0].onClick(v);
                        dialog.dismiss();
                    }
                });
                btnSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listeners[1].onClick(v);
                        dialog.dismiss();
                    }
                });
            }
        } else {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.setContentView(view);
        Window mWindow = dialog.getWindow();


        // 添加动画
        mWindow.setWindowAnimations(R.style.DialogAnim);
        return dialog;
    }

    public static Dialog createTipsDialog(Context context, String text, View.OnClickListener listener, boolean cancelable) {
        return createTipsDialog(context, "提示", text, new String[]{"取消", "确定"}, new View.OnClickListener[]{listener}, cancelable);
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
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, String dialogContent, OnViewClickListener mOnclickListener) {
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
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, String dialogTitle, String dialogContent, String sureText, String cancelText, OnViewClickListener mOnclickListener) {
        int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
        createDialog(mActivity, mRootContainer, R.layout.dialog_tips, mTipsHolder = new TipsViewHolder(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        mTipsHolder.mOnclickListener = mOnclickListener;
        mTipsHolder.mTvDialogTile.setText(TextUtils.isEmpty(dialogTitle) ? mActivity.getString(R.string.dialog_tips_str) : dialogTitle);
        mTipsHolder.mTvDialogContent.setText(dialogContent);
        mTipsHolder.mBtnSure.setText(TextUtils.isEmpty(sureText) ? mActivity.getString(R.string.yes_str) : sureText);
        mTipsHolder.mBtnCancel.setText(TextUtils.isEmpty(cancelText) ? mActivity.getString(R.string.no_str) : cancelText);
        dialog.show();
    }

    public static Dialog showDatePickerDialog(Activity context, final DialogItemClickListener dialogClickListener) {
        return showDatePickerDialog(context, false, dialogClickListener);
    }

    /**
     * 选时间
     *
     * @param context
     * @param dialogClickListener
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Dialog showDatePickerDialog(Activity context, boolean hasSelectTime, final DialogItemClickListener dialogClickListener) {
        if (context != null && (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN ? !context.isDestroyed() : true)) {
            View timepickerview = LayoutInflater.from(context).inflate(R.layout.date_picker_layout, new ViewGroup(context) {
                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {

                }
            }, false);
            final WheelMain wheelMain = new WheelMain(context, timepickerview, hasSelectTime);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            wheelMain.initDateTimePicker(year, month, day, hour, min);
            final Dialog datePickerDialog = new Dialog(context, R.style.DialogStyle);
            View view = LayoutInflater.from(context).inflate(R.layout.date_picker_layout_dialog, new ViewGroup(context) {
                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {

                }
            }, false);
            LinearLayout mLLDateContainer = (LinearLayout) view.findViewById(R.id.ll_date_container);
            mLLDateContainer.addView(timepickerview);

            view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.dismiss();
                    dialogClickListener.confirm(wheelMain.getTime(), 0);
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    datePickerDialog.dismiss();
                }
            });
            // 设置对话框布局
            datePickerDialog.setContentView(view);

            Window mWindow = datePickerDialog.getWindow();
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.width = ScreenUtils.getScreenWidth(context);
            mWindow.setGravity(Gravity.BOTTOM);
            // 添加动画
            mWindow.setWindowAnimations(R.style.DialogAnim);
            mWindow.setAttributes(lp);
            // 点击对话框外部关闭对话框
            datePickerDialog.setCanceledOnTouchOutside(true);
            // 显示对话框
            datePickerDialog.show();

            return datePickerDialog;
        } else {
            return null;
        }
    }


    public interface DialogItemClickListener {
        void confirm(String result, int position);
    }

    static class TipsViewHolder {

        OnViewClickListener mOnclickListener;

        @ViewInject(R.id.tv_dialog_title)
        private TextView mTvDialogTile;

        @ViewInject(R.id.tv_dialog_content)
        private TextView mTvDialogContent;

        @ViewInject(R.id.btn_sure)
        private Button mBtnSure;
        @ViewInject(R.id.btn_cancel)
        private Button mBtnCancel;

        @Event({R.id.btn_sure, R.id.btn_cancel})
        private void OnViewClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sure:
                    if (mOnclickListener != null) {
                        mOnclickListener.onClick(view);
                    }
                case R.id.btn_cancel:
                    dialog.dismiss();
                    dialog = null;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @author yj
     * 通过databing来生成Dialog
     * 2016/8/10 11:40
     */
    public static Dialog creatDialog(Context context, View view, int width, int height) {
        Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
        windowParams.width = width;
        windowParams.height = height;
        dialogWindow.setAttributes(windowParams);

        return dialog;
    }

}
