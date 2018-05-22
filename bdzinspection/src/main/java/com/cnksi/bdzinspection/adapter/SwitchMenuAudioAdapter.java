package com.cnksi.bdzinspection.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.bdzinspection.R;
import com.cnksi.common.Config;
import com.cnksi.bdzinspection.utils.MediaRecorderUtils;
import com.cnksi.core.utils.ToastUtils;

import java.util.List;

/**
 * Created by kkk on 2018/2/27.
 */

public class SwitchMenuAudioAdapter extends BaseQuickAdapter<String, SwitchMenuAudioAdapter.AudioViewHolder> {


    public SwitchMenuAudioAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(AudioViewHolder helper, String item) {
        ViewDataBinding binding = helper.getBinding();
        final String path = Config.AUDIO_FOLDER + item;
        helper.getView(R.id.tv_audio).setOnClickListener(view -> {
            if (MediaRecorderUtils.getInstance().isPlaying()) {
                MediaRecorderUtils.getInstance().stopPlayAudio();
            } else {
                MediaRecorderUtils.getInstance().startPlayAudio(path, mp -> {
                    ToastUtils.showMessage("播放完毕...");
                    MediaRecorderUtils.getInstance().setPlaying(false);
                });
            }
        });
        helper.setText(R.id.tv_audio, MediaRecorderUtils.getInstance().getAudioDuration(mContext, path));
        helper.getView(R.id.img_delete_audio).setOnClickListener(view -> {
            if (mItemClickListener != null && mData != null && !mData.isEmpty()) {
                mItemClickListener.onClick(view, item, mData.indexOf(item));
            }
        });
    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        if (binding == null) {
            return super.getItemView(layoutResId, parent);
        }
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support);
        return view;
    }

    public static class AudioViewHolder extends BaseViewHolder {

        public AudioViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }


    private ItemClickListener mItemClickListener;

    public void setItemClicListener(ItemClickListener itemClicListener) {
        this.mItemClickListener = itemClicListener;
    }
}
