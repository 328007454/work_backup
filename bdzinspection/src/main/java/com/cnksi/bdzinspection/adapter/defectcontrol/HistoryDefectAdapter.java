package com.cnksi.bdzinspection.adapter.defectcontrol;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsHistoryDefectItemBinding;
import com.cnksi.bdzinspection.utils.DefectUtils;
import com.cnksi.common.Config;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;

import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 缺陷管控--> 当前设备存在缺陷
 *
 * @author Joe
 */
public class HistoryDefectAdapter extends SimpleBaseAdapter {

    private String currentFunctionModel = "";
    private boolean isParticularInspection = false;
    private boolean isTrackHistory = false;
    /**
     * 当前选中的缺陷的id
     */
    private String defectRecordId = "";
    private OnAdapterViewClickListener mOnAdapterViewClickListener;

    public HistoryDefectAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
        initBitmapUtils(context);
        setScaleDownBitmapSize(3);
    }

    public void setInspectionType(boolean isParticularInspection) {
        this.isParticularInspection = isParticularInspection;
    }

    public void setOnAdapterViewClickListener(OnAdapterViewClickListener mOnAdapterViewClickListener) {
        this.mOnAdapterViewClickListener = mOnAdapterViewClickListener;
    }

    public void setCurrentClickedPosition(String defectRecordId) {
        this.defectRecordId = defectRecordId;
        if (!TextUtils.isEmpty(defectRecordId)) {
            this.notifyDataSetChanged();
        }
    }

    public void setCurrentFunctionModel(String currentModel) {
        this.currentFunctionModel = currentModel;
    }

    public void setTrackHistory(boolean isTrackHistory) {
        this.isTrackHistory = isTrackHistory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DefectRecord mDefect = (DefectRecord) getItem(position);
        XsHistoryDefectItemBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = XsHistoryDefectItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
            convertView = itemBinding.getRoot();
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        if (isTrackHistory) {
            itemBinding.llFunctionContainer.setVisibility(View.GONE);
            itemBinding.tvTrackerPerson.setVisibility(View.GONE);
        } else {
            itemBinding.llFunctionContainer.setVisibility(View.VISIBLE);
            itemBinding.tvRecordPerson.setVisibility(View.GONE);
        }

        itemBinding.tvRecordPerson.setText("记录人员:" + mDefect.discoverer);

        itemBinding.tvDefectDevice.setText("设备:" + (TextUtils.isEmpty(mDefect.devcie) ? "" : mDefect.devcie));
        itemBinding.tvDefectSpace.setText("间隔:" + mDefect.spname);
        itemBinding.ivTips.setImageResource(DefectUtils.convert2ConnerMark(mDefect.defectlevel));
        itemBinding.tvDefectDescription.setText(DefectUtils.convert2DefectDesc(mDefect));

        //add by yangjun 特殊类巡检不需要显示部件名字
        if (isParticularInspection) {
            itemBinding.tvDefectDescription.setText(mDefect.description);
        }
        itemBinding.tvDefectDiscoverTime.setText(mContext.getResources().getString(R.string.xs_defect_discover_time_format_str, DateUtils.getFormatterTime(mDefect.discovered_date)));

        // 判读图片是否存在，不存在就不显示，或显示默认图片
        String[] defectPicArray = StringUtils.cleanString(mDefect.pics).split(Config.COMMA_SEPARATOR);
        if (defectPicArray != null && defectPicArray.length > 0 && !TextUtils.isEmpty(StringUtils.cleanString(defectPicArray[0]))) {
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(defectPicArray[0]), 280);
            if (bitmap != null) {
                itemBinding.ivDefectImage.setImageBitmap(bitmap);
            }
        } else {
            itemBinding.ivDefectImage.setImageResource(R.mipmap.icon_nodefect);
        }

        if (Config.TRACK_DEFECT_MODEL.equalsIgnoreCase(currentFunctionModel)) {
            // 跟踪模式下
            itemBinding.tvEliminateDefect.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(defectRecordId)) {
                if (defectRecordId.equalsIgnoreCase(mDefect.defectid)) {
                    convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                    itemBinding.tvTrackDefect.setBackgroundResource(R.drawable.xs_gray_button_background);
                    itemBinding.tvTrackDefect.setEnabled(false);
                    itemBinding.tvTrackDefect.setTextColor(mContext.getResources().getColor(R.color.xs_white));
                } else {
                    convertView.setBackgroundResource(R.drawable.xs_history_defect_item_background_selector);
                    itemBinding.tvTrackDefect.setBackgroundResource(R.drawable.xs_grass_green_button_background_selector);
                    itemBinding.tvTrackDefect.setEnabled(true);
                    itemBinding.tvTrackDefect.setTextColor(mContext.getResources().getColor(R.color.xs_white));
                }
            }
        } else if (Config.ELIMINATE_DEFECT_MODEL.equalsIgnoreCase(currentFunctionModel)) {
            // 消除缺陷模式下
            itemBinding.tvTrackDefect.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(defectRecordId)) {
                if (defectRecordId.equalsIgnoreCase(mDefect.defectid)) {
                    convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                    itemBinding.tvEliminateDefect.setBackgroundResource(R.drawable.xs_gray_button_background);
                    itemBinding.tvEliminateDefect.setEnabled(false);
                    itemBinding.tvEliminateDefect.setTextColor(mContext.getResources().getColor(R.color.xs_white));
                } else {
                    convertView.setBackgroundResource(R.drawable.xs_history_defect_item_background_selector);
                    itemBinding.tvEliminateDefect.setBackgroundResource(R.drawable.xs_red_button_background_selector);
                    itemBinding.tvEliminateDefect.setEnabled(true);
                    itemBinding.tvEliminateDefect.setTextColor(mContext.getResources().getColor(R.color.xs_white));
                }
            }
        }


        itemBinding.tvReportDefect.setOnClickListener(view -> onClick(view, mDefect));
        itemBinding.tvEliminateDefect.setOnClickListener(view -> onClick(view, mDefect));
        itemBinding.ivDefectImage.setOnClickListener(view -> onClick(view, mDefect));
        itemBinding.tvTrackDefect.setOnClickListener(view -> onClick(view, mDefect));

        return itemBinding.getRoot();
    }

    private void onClick(View view, DefectRecord mDefect) {
        if (mOnAdapterViewClickListener != null) {
            mOnAdapterViewClickListener.OnAdapterViewClick(view, mDefect);
        }

    }

    public interface OnAdapterViewClickListener {
        void OnAdapterViewClick(View view, DefectRecord mDefect);
    }


}
