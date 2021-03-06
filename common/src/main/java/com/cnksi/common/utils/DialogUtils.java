package com.cnksi.common.utils;


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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.common.R;
import com.cnksi.common.databinding.DialogTipsBinding;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.datepicker.NumericWheelAdapter;
import com.cnksi.core.view.datepicker.WheelMain;
import com.cnksi.core.view.datepicker.WheelView;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.Calendar;

/**
 * @author kkk
 */
public class DialogUtils {

    public static Dialog dialog = null;

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
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (width != 0) {
            lp.width = width;
        }
        if (height != 0) {
            lp.height = height;
        }
        mWindow.setAttributes(lp);
        // 添加动画
        mWindow.setWindowAnimations(R.style.DialogAnim);
        return dialog;
    }

    public static Dialog createDialog(Context context, View v, int width, int height) {
        return createDialog(context, v, width, height, true);
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
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSure = view.findViewById(R.id.btn_confirm);
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
                btnCancel.setOnClickListener(v -> dialog.dismiss());
                btnSure.setOnClickListener(v -> {
                    listeners[0].onClick(v);
                    dialog.dismiss();
                });
            }
            if (listeners.length > 1) {
                btnCancel.setOnClickListener(v -> {
                    listeners[0].onClick(v);
                    dialog.dismiss();
                });
                btnSure.setOnClickListener(v -> {
                    listeners[1].onClick(v);
                    dialog.dismiss();
                });
            }
        } else {
            btnCancel.setOnClickListener(v -> dialog.dismiss());
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
     * 提示dialog
     *
     * @param mRootContainer
     * @param dialogContent
     * @param mOnclickListener
     */
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, CharSequence dialogContent,  View.OnClickListener mOnclickListener) {
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
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, String dialogContent, String sureText, String cancelText,  View.OnClickListener mOnclickListener) {
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
    public static void showSureTipsDialog(Activity mActivity, ViewGroup mRootContainer, String dialogTitle, CharSequence dialogContent, String sureText, String cancelText, View.OnClickListener mOnclickListener) {
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        DialogTipsBinding tipsBinding = DialogTipsBinding.inflate(mActivity.getLayoutInflater());
        dialog = createDialog(mActivity, tipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        tipsBinding.tvDialogTitle.setText(TextUtils.isEmpty(dialogTitle) ? mActivity.getString(R.string.dialog_tips_str) : dialogTitle);
        tipsBinding.tvDialogContent.setText(dialogContent);
        tipsBinding.btnSure.setText(TextUtils.isEmpty(sureText) ? mActivity.getString(R.string.yes_str) : sureText);
        tipsBinding.btnCancel.setText(TextUtils.isEmpty(cancelText) ? mActivity.getString(R.string.no_str) : cancelText);
        dialog.show();
        tipsBinding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            dialog = null;
        });
        tipsBinding.btnSure.setOnClickListener(v -> {
            if (mOnclickListener != null) {
                mOnclickListener.onClick(v);
                dialog.dismiss();
            }
        });

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
        if (context != null && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN || !context.isDestroyed())) {
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
            LinearLayout mLLDateContainer = view.findViewById(R.id.ll_date_container);
            mLLDateContainer.addView(timepickerview);

            view.findViewById(R.id.confirm).setOnClickListener(v -> {
                datePickerDialog.dismiss();
                dialogClickListener.confirm(wheelMain.getTime(), 0);
            });
            view.findViewById(R.id.cancel).setOnClickListener(v -> datePickerDialog.dismiss());
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


    /**
     * 选时间
     *
     * @param context
     * @param dialogClickListener
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Dialog showTimePickerDialog(Activity context, final boolean hasMills, final DialogItemClickListener dialogClickListener) {
        if (context != null && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN || !context.isDestroyed())) {
            View container = LayoutInflater.from(context).inflate(R.layout.date_picker_layout_dialog, null, false);
            LinearLayout mLLDateContainer = container.findViewById(R.id.ll_date_container);
            View view = LayoutInflater.from(context).inflate(R.layout.time_picker_layout, mLLDateContainer, true);
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            final WheelView wv_hours = view.findViewById(R.id.hour);
            final WheelView wv_mins = view.findViewById(R.id.min);
            final WheelView wv_second = view.findViewById(R.id.second);
            final EditText wm = view.findViewById(R.id.et_mills);
            if (!hasMills) {
                view.findViewById(R.id.ll_mills).setVisibility(View.GONE);
            }
            wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
            wv_hours.setCyclic(true);// 可循环滚动
            wv_hours.setLabel("时");// 添加文字
            wv_hours.setCurrentItem(hour);

            wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
            wv_mins.setCyclic(true);// 可循环滚动
            wv_mins.setLabel("分");// 添加文字
            wv_mins.setCurrentItem(min);

            wv_second.setAdapter(new NumericWheelAdapter(0, 59));
            wv_second.setCyclic(true);// 可循环滚动
            wv_second.setLabel("秒");// 添加文字
            wv_second.setCurrentItem(min);
            wm.setText("000");
            int textSize = AutoUtils.getPercentHeightSizeBigger(54);
            wv_second.TEXT_SIZE = textSize;
            wv_hours.TEXT_SIZE = textSize;
            wv_mins.TEXT_SIZE = textSize;
            final Dialog datePickerDialog = new Dialog(context, R.style.DialogStyle);


            container.findViewById(R.id.confirm).setOnClickListener(v -> {
                datePickerDialog.dismiss();
                String mimls = StringUtilsExt.nullTo(wm.getText().toString(), "0");
                String rs = String.format("%02d", wv_hours.getCurrentItem()) + ":" + String.format("%02d", wv_mins.getCurrentItem()) + ":" + String.format("%02d", wv_second.getCurrentItem());
                if (hasMills) {
                    rs = rs + " " + String.format("%03d", Integer.parseInt(mimls));
                }
                dialogClickListener.confirm(rs, 0);
            });
            container.findViewById(R.id.cancel).setOnClickListener(v -> datePickerDialog.dismiss());
            // 设置对话框布局
            datePickerDialog.setContentView(container);
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

    public interface DialogItemClickListener {
        void confirm(String result, int position);
    }


    public static void setDialogNull() {
        if (dialog != null) {
            dialog = null;
        }
    }


}
