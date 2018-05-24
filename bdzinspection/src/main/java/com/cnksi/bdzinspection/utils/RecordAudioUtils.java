package com.cnksi.bdzinspection.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.databinding.XsRecordAudioDialogBinding;
import com.cnksi.core.utils.ScreenUtils;

/**
 * 录音工具，显示录音对话框
 * Created by kkk on 2018/3/1.
 */

public class RecordAudioUtils {
    private Dialog mRecordAudioDialog;
    private Context mContext;

    private CallBack callBack;


    public RecordAudioUtils(Context context, ViewGroup viewGroup) {
        this.mContext = context;
        XsRecordAudioDialogBinding audioDialogBinding = XsRecordAudioDialogBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        int dialogWidth = ScreenUtils.getScreenWidth(context) / 2;
        mRecordAudioDialog = DialogUtils.createDialog(context, audioDialogBinding.getRoot(), dialogWidth, dialogWidth);
        audioDialogBinding.getRoot().setOnClickListener(view -> {
            mRecordAudioDialog.dismiss();
        });
        mRecordAudioDialog.setOnDismissListener(dialog -> {
                    MediaRecorderUtils.getInstance().stopRecordAudio();
                    callBack.onSuccess();
                }
        );
    }

    public void startRecord(String fileName, CallBack callBack) {
        this.callBack = callBack;
        MediaRecorderUtils.getInstance().startRecordAudio(fileName);
        mRecordAudioDialog.show();
    }

    public void onDestory() {
        if (mRecordAudioDialog.isShowing()) {
            mRecordAudioDialog.dismiss();
        }
        mRecordAudioDialog = null;
    }


    public String getAllRecordTime(String audioFile) {
        try {
            return String.valueOf(MediaRecorderUtils.getInstance().getDurationSuc(mContext, audioFile) + "”");
        } catch (Exception ex) {
            return String.valueOf(Math.rint(30));
        }
    }

    public interface CallBack {
        void onSuccess();
    }
}
