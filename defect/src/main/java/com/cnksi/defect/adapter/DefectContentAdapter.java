package com.cnksi.defect.adapter;

import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;

import com.cnksi.common.Config;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.adapter.BaseAdapter;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.activity.DefectControlActivity;
import com.cnksi.defect.databinding.AdapterDefectItemBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Mr.K  on 2017/4/25.
 */

public class DefectContentAdapter extends BaseAdapter<DefectRecord> {
    DefectControlActivity activity;
    private ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DefectContentAdapter(DefectControlActivity context, Collection data) {
        super(context, data, R.layout.adapter_defect_item);
        activity = context;
    }

    public static CharSequence calculateRemindTime(DefectRecord defectRecord) {
        if (defectRecord == null || TextUtils.isEmpty(defectRecord.discovered_date)) {
            return "";
        }
        int day;
        if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(defectRecord.defectlevel) || Config.CRISIS_LEVEL.equals(defectRecord.defectlevel)) {
            day = 1;
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(defectRecord.defectlevel) || Config.SERIOUS_LEVEL.equals(defectRecord.defectlevel)) {
            day = 30;
        } else {
            day = 365;
        }
        String d = DateUtils.getAfterTime(defectRecord.discovered_date, day, "yyyy-MM-dd");
        String s = "到期时间：" + d;
        if (d.compareTo(DateUtils.getAfterTime(3)) > 0) {
            return s;
        } else {
            return StringUtils.changeTextColor(s, Color.RED);
        }
    }

    /**
     * 转换缺陷等级为 一般 严重 危机
     *
     * @param item
     * @return
     */
    public CharSequence convert2DefectLevel(DefectRecord item) {
        CharSequence result;
        if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(item.defectlevel)) {
            result = StringUtils.changePartTextColor("[" + Config.GENERAL_LEVEL + "]" + item.description, Color.parseColor("#F1B55B"), 0, 6);
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(item.defectlevel)) {
            result = StringUtils.changePartTextColor("[" + Config.SERIOUS_LEVEL + "]" + item.description, Color.parseColor("#F18815"), 0, 6);
        } else {
            result = StringUtils.changePartTextColor("[" + Config.CRISIS_LEVEL + "]" + item.description, Color.RED, 0, 6);
        }
        return result;
    }

    @Override
    public void convert(ViewDataBinding dataBinding, DefectRecord item, int position) {
        AdapterDefectItemBinding itemBinding = (AdapterDefectItemBinding) dataBinding;
        itemBinding.tvDefectContent.setText(convert2DefectLevel(item));
        itemBinding.tvDefectDevice.setText("设备：" + (TextUtils.isEmpty(item.devcie) ? "" : item.devcie));
        final ArrayList<String> listPicDis = StringUtils.stringToList(item.pics);
        if (listPicDis.size() > 0 && !TextUtils.isEmpty(listPicDis.get(0))) {
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(listPicDis.get(0)), 280);
            if (bitmap != null) {
                itemBinding.ivDefectImage.setImageBitmap(bitmap);
            }
            itemBinding.ivDefectImage.setOnClickListener(v -> {

                        ImageDetailsActivity.with(activity).setPosition(0).setImageUrlList(StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER))
                                .setDeleteFile(false).setShowDelete(false).start();
                    }
            );
        } else {
            itemBinding.ivDefectImage.setScaleType(ImageView.ScaleType.CENTER);
            itemBinding.ivDefectImage.setImageResource(R.mipmap.icon_nodefect);
            itemBinding.ivDefectImage.setOnClickListener(null);
        }

        itemBinding.tvDefectSpace.setText("间隔：" + (TextUtils.isEmpty(item.spname) ? "" : item.spname));
        itemBinding.tvRecordPerson.setText("记录人员：" + (TextUtils.isEmpty(item.discoverer) ? "" : item.discoverer));
        itemBinding.tvDefectDiscoverTime.setText("时间：" + (TextUtils.isEmpty(item.discovered_date) ? "" : (DateUtils.getFormatterTime(item.insertTime, DateUtils.yyyy_MM_dd))));
        itemBinding.tvDefectRemindTime.setText(calculateRemindTime(item));
        itemBinding.getRoot().setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.itemClick(v, item, position);
            }
        });

    }

    public void setList(List<DefectRecord> defectRecords) {
        realDatas = defectRecords;
        notifyDataSetChanged();
    }
}
