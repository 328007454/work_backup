package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsAudioAdapterBinding;
import com.cnksi.common.Config;
import com.cnksi.bdzinspection.utils.MediaRecorderUtils;
import com.cnksi.core.utils.ToastUtils;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年3月29日 下午8:37:47 TODO
 */
public class AudioAdapter extends SimpleBaseAdapter {

    /**
     * @param context
     * @param dataList
     */
    public AudioAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        XsAudioAdapterBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = XsAudioAdapterBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());

        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        final String path = Config.AUDIO_FOLDER + getItem(position);
        itemBinding.tvAudio.setText(MediaRecorderUtils.getInstance().getAudioDuration(mContext, path));
        itemBinding.tvAudio.setOnClickListener(view -> {
            if (MediaRecorderUtils.getInstance().isPlaying()) {
                MediaRecorderUtils.getInstance().stopPlayAudio();
            } else {
                MediaRecorderUtils.getInstance().startPlayAudio(path, mp -> {
                    ToastUtils.showMessage("播放完毕...");
                    MediaRecorderUtils.getInstance().setPlaying(false);
                });
            }
        });

        return itemBinding.getRoot();
    }

}
