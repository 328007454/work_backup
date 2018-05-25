package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.common.Config;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.holder.RecyclerViewHolder;
import com.cnksi.sjjc.util.CoreConfig;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by han on 2017/3/27.
 */

public class DefectAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<DefectRecord> data;
    private ItemClickListener clickListener;
    private Context context;

    private int layoutId;

    public DefectAdapter(Context context, List<DefectRecord> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    public void setItemClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(context, parent, layoutId, false);
        AutoUtils.autoSize(viewHolder.getRootView());
        return new RecyclerViewHolder(viewHolder.getRootView());
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        ImageView defectImage = holder.getView(R.id.iv_defect_image);
        final DefectRecord defectRecord = data.get(position);
        String[] defectPicArray = StringUtils.cleanString(defectRecord.pics).split(CoreConfig.COMMA_SEPARATOR);

        if (defectPicArray != null && defectPicArray.length > 0
                && !TextUtils.isEmpty(StringUtils.cleanString(defectPicArray[0]))) {
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(defectPicArray[0]), 280);
            if (bitmap != null) {
                defectImage.setImageBitmap(bitmap);
            }
        } else {
            defectImage.setScaleType(ImageView.ScaleType.CENTER);
            defectImage.setImageResource(R.mipmap.icon_nodefect);
        }
        ((TextView) holder.getView(R.id.tv_device_name)).setText(defectRecord.devcie);
        ((TextView) holder.getView(R.id.tv_defect_discover_time)).setText(DateUtils.getFormatterTime(defectRecord.discovered_date));
        defectImage.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.itemClick(view, defectRecord, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setList(ArrayList<DefectRecord> defectRecords) {
        this.data = defectRecords;
        notifyDataSetChanged();
    }
}
