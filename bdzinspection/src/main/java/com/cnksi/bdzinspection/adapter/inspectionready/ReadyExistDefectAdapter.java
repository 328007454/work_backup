package com.cnksi.bdzinspection.adapter.inspectionready;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.PinnedHeaderSectionedAdapter;
import com.cnksi.bdzinspection.databinding.XsGroupItemBinding;
import com.cnksi.bdzinspection.databinding.XsReadyExistDefectItemBinding;
import com.cnksi.bdzinspection.utils.DefectUtils;
import com.cnksi.common.Config;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

public class ReadyExistDefectAdapter extends PinnedHeaderSectionedAdapter<String, DefectRecord> {

    private OnAdapterViewClickListener mOnAdapterViewClickListener;

    public ReadyExistDefectAdapter(Context context) {
        super(context);
        initBitmapUtils(context);
        setScaleDownBitmapSize(2);
    }

    public void setOnAdapterViewClickListener(OnAdapterViewClickListener mOnAdapterViewClickListener) {
        this.mOnAdapterViewClickListener = mOnAdapterViewClickListener;
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        DefectRecord mDefect = getItem(section, position);
        XsReadyExistDefectItemBinding itemBinding = null;

        if (convertView == null) {
            itemBinding = XsReadyExistDefectItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.llContainer.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(150));
        itemBinding.ivTips.setImageResource(DefectUtils.convert2ConnerMark(mDefect.defectlevel));
        itemBinding.tvDefectDescription.setText(DefectUtils.convert2DefectDesc(mDefect));
        itemBinding.tvFounderPerson.setText("记录人员：" + mDefect.discoverer);
        itemBinding.tvDefectDevice.setText("设备:" + mDefect.devcie);
        itemBinding.tvDefectSpace.setText("间隔:" + mDefect.spname);
        itemBinding.tvDefectDiscoverTime.setText(mContext.getResources().getString(R.string.xs_defect_discover_time_format_str,
                DateUtils.getFormatterTime(mDefect.discovered_date)));

        itemBinding.tvDefectRemindTime.setText(DefectUtils.calculateRemindTime(mDefect));

        // 判读图片是否存在，不存在就不显示，或显示默认图片
        String[] defectPicArray = StringUtils.cleanString(mDefect.pics).split(Config.COMMA_SEPARATOR);
        if (defectPicArray != null && defectPicArray.length > 0
                && !TextUtils.isEmpty(StringUtils.cleanString(defectPicArray[0]))) {
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(  Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(defectPicArray[0]), 280);
            if (bitmap!=null){
                itemBinding.ivDefectImage.setImageBitmap(bitmap);
            }
        } else {
            itemBinding.ivDefectImage.setImageResource(R.mipmap.icon_nodefect);
        }

        itemBinding.ivDefectImage.setOnClickListener(view -> mOnAdapterViewClickListener.OnAdapterViewClick(view, mDefect));
        return itemBinding.getRoot();
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        String group = getSection(section);
        if (group == null) {
            group = "";
        }
        XsGroupItemBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = XsGroupItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());

        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.imgOpen.setVisibility(View.GONE);
        itemBinding.tvGroupItem.setTextColor(DefectUtils.getDefectColor(group));
        itemBinding.tvGroupItem.setText(group + "(" + String.valueOf(getCountForSection(section)) + ")");

        return itemBinding.getRoot();
    }

    public interface OnAdapterViewClickListener {
        void OnAdapterViewClick(View view, DefectRecord mDefect);
    }

}
