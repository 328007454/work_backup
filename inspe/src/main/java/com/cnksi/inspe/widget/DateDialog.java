package com.cnksi.inspe.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;

import com.cnksi.inspe.R;
import com.cnksi.inspe.databinding.WidgetDatedialogBinding;
import com.cnksi.inspe.utils.DateFormat;

/**
 * 日期选择对话框
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/23 15:47
 */

public class DateDialog extends AlertDialog implements View.OnClickListener {

    //    WidgetD dataBinding;
    private Context context;

    public DateDialog(Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    public DateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public DateDialog(Context context, int themeResId) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    private WidgetDatedialogBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.widget_datedialog, null, false);
        setContentView(dataBinding.getRoot());
        setCanceledOnTouchOutside(false);

        dataBinding.cancleBtn.setOnClickListener(this);
        dataBinding.okBtn.setOnClickListener(this);
        if (date != null) {
            dataBinding.datePicker.updateDate(date[0], date[1], date[2]);
        }

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancleBtn) {
            dismiss();

        } else if (i == R.id.okBtn) {
            dismiss();
            if (onDialogListener != null) {
                onDialogListener.onDateChanged(DateFormat.dbdateToLong(String.format("%04d-%02d-%02d %02d:%02d:%02d", dataBinding.datePicker.getYear(), dataBinding.datePicker.getMonth() + 1, dataBinding.datePicker.getDayOfMonth(), 0, 0, 0)), dataBinding.datePicker.getYear(), dataBinding.datePicker.getMonth() + 1, dataBinding.datePicker.getDayOfMonth());
            }

        }
    }

    private OnDialogListener onDialogListener;

    public void setOnDialogListener(OnDialogListener listener) {
        this.onDialogListener = listener;
    }

    int[] date = null;

    /**
     * @param year
     * @param month 【1-12】
     * @param day
     */
    public void setDate(int year, int month, int day) {
        if (dataBinding != null) {
            dataBinding.datePicker.updateDate(year, month - 1, day);
        } else {
            date = new int[]{year, month - 1, day};
        }
    }

    public interface OnDialogListener {
        void onDateChanged(long date, int year, int month, int day);
    }
}
