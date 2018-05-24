package com.cnksi.bdzinspection.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.inter.DialogInputClickListener;

/**
 * Created by lyndon on 2016/9/2.
 */
public class DialogUtil {

    private static DialogUtil instance;

    private Dialog dialog;

    public static DialogUtil getInstance() {
        if (instance == null) {
            instance = new DialogUtil();
        }
        return instance;
    }

    public void dismissDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.hide();
        }
    }

//    /**
//     * 确认取消弹框
//     *
//     * @param context
//     * @param title           标题
//     * @param message         显示消息
//     * @param gravity         对齐方式
//     * @param ok              确定按钮文字
//     * @param cancel          取消按钮文字
//     * @param onClickListener 按钮点击事件
//     */
//    public void showOkCancelDialog(Context context, String title, String message, int gravity, String ok, String cancel, final DialogInterface.OnClickListener onClickListener) {
//        dismissDialog();
//        dialog = new Dialog(context, R.style.dialog);
//        ViewHolder holder = new ViewHolder(context, null, R.layout.dialog_two_button, false);
//        DialogTwoButtonLayout twoButtonLayout = (DialogTwoButtonLayout) holder.getDataBinding();
//        twoButtonLayout.setTitle(title);
//        twoButtonLayout.setMessage(Html.fromHtml(message).toString());
//        twoButtonLayout.setMessageGravity(gravity);
//        twoButtonLayout.setNo(cancel);
//        twoButtonLayout.setYes(ok);
//        twoButtonLayout.setShowButton(true);
//        View.OnClickListener dialogClick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.yes)
//                    onClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
//                else
//                    onClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
//            }
//        };
//
//        twoButtonLayout.yes.setOnClickListener(dialogClick);
//        twoButtonLayout.no.setOnClickListener(dialogClick);
//        dialog.setContentView(holder.getRootView(), new ViewGroup.LayoutParams(NumberUtil.convertFloatToInt(SystemUtil.getInstance().getWidth() - SystemUtil.getInstance().getScale() * 40), ViewGroup.LayoutParams.WRAP_CONTENT));
//        dialog.show();
////        new DialogTask(dialog).execute(holder.getRootView());
//    }
//
//    /**
//     * 确认取消弹框
//     *
//     * @param context
//     * @param title   标题
//     * @param message 显示消息
//     * @param gravity 对齐方式
//     */
//    public void showTiplDialog(Context context, String title, String message, int gravity) {
//        dismissDialog();
//        dialog = new Dialog(context, R.style.dialog);
//        ViewHolder holder = new ViewHolder(context, null, R.layout.dialog_two_button, false);
//        DialogTwoButtonLayout twoButtonLayout = (DialogTwoButtonLayout) holder.getDataBinding();
//        twoButtonLayout.setTitle(title);
//        twoButtonLayout.setMessage(Html.fromHtml(message).toString());
//        twoButtonLayout.setMessageGravity(gravity);
//        twoButtonLayout.setShowButton(false);
//        dialog.setContentView(holder.getRootView(), new ViewGroup.LayoutParams(NumberUtil.convertFloatToInt(SystemUtil.getInstance().getWidth() * 9 / 10), ViewGroup.LayoutParams.WRAP_CONTENT));
//        dialog.show();
//        new DialogTask(dialog).execute(holder.getRootView());
//    }

    /**
     * @param context
     * @param maxLength
     * @param inputType
     * @param title
     * @param hint
     * @param ok
     * @param cancel
     * @param onClickListener
     */
    public void showInputCancelDialog(Context context, int maxLength, int inputType, String title, String oldValue, String hint, String ok, String cancel, final DialogInputClickListener onClickListener) {
        dismissDialog();
        dialog = new Dialog(context, R.style.dialog);
        ViewHolder holder = new ViewHolder(context, null, R.layout.xs_dialog_input_1, false);
        final EditText editText = holder.getView(R.id.edit);
        TextView tvTitle = holder.getView(R.id.tv_dialog_title);
        editText.setHint(hint);
        editText.setText(oldValue);
        tvTitle.setText(title);
        if (maxLength != 0) {
            InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
            editText.setFilters(filters);
        }
        editText.setInputType(inputType);
        Button btnYes = holder.getView(R.id.btn_sure);
        Button btnCancel = holder.getView(R.id.btn_cancel);
        btnCancel.setText(cancel);
        btnYes.setText(ok);
        View.OnClickListener dialogClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_sure) {
                    onClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE, editText.getText().toString());
                } else {
                    onClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE, "");
                }
            }
        };

        btnYes.setOnClickListener(dialogClick);
        btnCancel.setOnClickListener(dialogClick);
        dialog.setContentView(holder.getRootView());
//        , new ViewGroup.LayoutParams(NumberUtil.convertFloatToInt(SystemUtil.getInstance().getWidth() - SystemUtil.getInstance().getScale() * 40), ViewGroup.LayoutParams.WRAP_CONTENT));
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        mWindow.setAttributes(lp);
        // 添加动画
        mWindow.setWindowAnimations(R.style.XS_DialogAnim);
        dialog.show();
    }

}
