package com.cnksi.bdzinspection.adapter.defectcontrol;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.LayoutAdapterDefectBinding;
import com.cnksi.common.Config;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.defect.utils.DefectUtils;
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
        LayoutAdapterDefectBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = LayoutAdapterDefectBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
            convertView = itemBinding.getRoot();
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        Object[] result = DefectUtils.convert2DefectDescBackground(mDefect);
        itemBinding.tvDefectContent.setText((CharSequence) result[0]);
        itemBinding.tvDefectContent.setBackgroundColor(Color.parseColor((String) result[1]));
        itemBinding.tvRecordPerson.setText("记录人员：" + mDefect.discoverer);
        itemBinding.tvDefectDevice.setText("设备:" + mDefect.devcie);
        itemBinding.tvDefectSpace.setText("间隔:" + mDefect.spname);
        itemBinding.tvDefectDiscoverTime.setText(mContext.getResources().getString(R.string.xs_defect_discover_time_format_str,
                DateUtils.getFormatterTime(mDefect.discovered_date)));
        itemBinding.tvDefect.setText(TextUtils.isEmpty(mDefect.description) ? "" : mDefect.description);
        itemBinding.tvDefectRemindTime.setText(DefectUtils.calculateRemindTime(mDefect));
        // 判读图片是否存在，不存在就不显示，或显示默认图片
        String[] defectPicArray = StringUtils.cleanString(mDefect.pics).split(Config.COMMA_SEPARATOR);
        if (defectPicArray != null && defectPicArray.length > 0
                && !TextUtils.isEmpty(StringUtils.cleanString(defectPicArray[0]))) {
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(defectPicArray[0]), 280);
            if (bitmap != null) {
                itemBinding.ivDefectImage.setImageBitmap(bitmap);
            }
        } else {
            itemBinding.ivDefectImage.setImageResource(R.mipmap.icon_nodefect);
        }

        itemBinding.ivDefectImage.setOnClickListener(view -> mOnAdapterViewClickListener.OnAdapterViewClick(view, mDefect));


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
