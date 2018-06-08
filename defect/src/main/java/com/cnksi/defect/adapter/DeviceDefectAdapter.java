package com.cnksi.defect.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.Config;
import com.cnksi.common.listener.ItemClickOrLongClickListener;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.utils.DefectUtils;

import java.util.List;

/**
 * @author Mr.K  on 2018/5/30.
 * @decrption 设备缺陷展示
 */

public class DeviceDefectAdapter extends BaseQuickAdapter<DefectRecord, BaseViewHolder> {

    /**
     * 当前缺陷页面模式
     */
    private String currentPageModel;

    public DeviceDefectAdapter(int layoutResId, @Nullable List<DefectRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, DefectRecord item) {
        ImageView defectImage = holder.getView(R.id.iv_defect_image);
        TextView txtDescription = holder.getView(R.id.tv_defect_description);
        TextView txtDiscoverTime = holder.getView(R.id.tv_defect_discover_time);
        TextView txtRecordPerson = holder.getView(R.id.tv_record_person);
        TextView txtDefectSpace = holder.getView(R.id.tv_defect_space);
        TextView txtDefectDevice = holder.getView(R.id.tv_defect_device);
        TextView txtStatus = holder.getView(R.id.txt_defect_status);
        ImageView ivTips = holder.getView(R.id.iv_tips);

        txtRecordPerson.setText("记录人员:" + item.discoverer);

        txtDefectDevice.setText("设备:" + (TextUtils.isEmpty(item.devcie) ? "" : item.devcie));
        txtDefectSpace.setText("间隔:" + item.spname);
        ivTips.setImageResource(DefectUtils.convert2ConnerMark(item.defectlevel));
        txtDescription.setText(DefectUtils.convert2DefectDesc(item));
        txtDiscoverTime.setText(mContext.getResources().getString(R.string.xs_defect_discover_time_format_str, DateUtils.getFormatterTime(item.discovered_date)));

        // 判读图片是否存在，不存在就不显示，或显示默认图片
        String[] defectPicArray = StringUtils.cleanString(item.pics).split(Config.COMMA_SEPARATOR);
        if (defectPicArray != null && defectPicArray.length > 0 && !TextUtils.isEmpty(StringUtils.cleanString(defectPicArray[0]))) {
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(defectPicArray[0]), 280);
            if (bitmap != null) {
                defectImage.setImageBitmap(bitmap);
            }
        } else {
            defectImage.setImageResource(R.mipmap.icon_nodefect);
        }
        txtStatus.setVisibility(View.GONE);
        if (TextUtils.equals(currentPageModel, Config.TRACK_DEFECT_MODEL)) {
            if (!TextUtils.isEmpty(defectId)) {
                if (defectId.equalsIgnoreCase(item.defectid)) {
                    txtStatus.setVisibility(View.VISIBLE);
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.xs_history_defect_item_background_selector);
                }
            }
        } else {
            if (!TextUtils.isEmpty(defectId)) {
                if (defectId.equalsIgnoreCase(item.defectid)) {
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatus.setText("正在验收...");
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.xs_history_defect_item_background_selector);
                }
            }
        }

        holder.itemView.setOnClickListener(v -> {
            defectId = item.defectid;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onClick(v, item, holder.getAdapterPosition());

            }
        });
    }

    private String defectId;

    public void setCurrentModel(String currentPageModel) {
        this.currentPageModel = currentPageModel;
    }

    private ItemClickOrLongClickListener listener;

    public void setItemClickListener(ItemClickOrLongClickListener<DefectRecord> listener) {
        this.listener = listener;
    }

    public void clearSelectStatus() {
        defectId = null;
    }

    public void setList(List<DefectRecord> defectRecords) {
        mData = defectRecords;
        notifyDataSetChanged();
    }
}
