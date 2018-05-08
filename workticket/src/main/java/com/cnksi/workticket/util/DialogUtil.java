package com.cnksi.workticket.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.datepicker.WheelMain;
import com.cnksi.workticket.R;
import com.cnksi.workticket.adapter.BdzAdapter;
import com.cnksi.workticket.databinding.DialogBdzListBinding;

import org.xutils.db.table.DbModel;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Mr.K on 2018/5/4.
 */

public class DialogUtil {


    /**
     * 变电站对话框
     */

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Object item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void initBdzDialog(Context context, List<DbModel> models, OnItemClickListener onItemClickListener) {
        if (models.isEmpty()) {
            ToastUtils.showMessage("没有变电站数据，请重新进入");
            return;
        }
        DialogBdzListBinding listBinding = DialogBdzListBinding.inflate(LayoutInflater.from(context));
        Dialog bdzDialog = createDialog(context, listBinding.getRoot(), ScreenUtils.getScreenWidth(context) * 7 / 9, LinearLayout.LayoutParams.WRAP_CONTENT);
        BdzAdapter bdzAdapter = new BdzAdapter(R.layout.ticket_textview, models);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        listBinding.recyBdz.setLayoutManager(linearLayoutManager);
        listBinding.recyBdz.setAdapter(bdzAdapter);
        bdzDialog.show();
        bdzAdapter.setItemClickListener((v, item, position) -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemClick(v, item, position);
            }
            bdzDialog.dismiss();
        });
    }


    /**
     * @author yj
     * 通过databing来生成Dialog
     * 2016/8/10 11:40
     */

    public  Dialog createDialog(Context context, View view, int width, int height) {
        Dialog dialog = new Dialog(context, R.style.Ticket_DialogStyle);
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

    public Dialog showDatePickerDialog(Activity context, final DialogItemClickListener dialogClickListener) {
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
    public Dialog showDatePickerDialog(Activity context, boolean hasSelectTime, final DialogItemClickListener dialogClickListener) {
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
}

