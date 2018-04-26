package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.util.BitmapUtil;

import org.xutils.x;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhou on 2016/6/8.
 */
public class ExamineProcessAdapter extends BaseAdapter<PreventionRecord> {

    private String[] locations = new String[]{"主控室", "高压室", "一次设备", "保护室", "电缆层", "二次设备", "其它地点"};

    private ItemClickListener<PreventionRecord> itemClickListener;

    private List<HoleRecord> exitHoleRecords;

    public void setExitHoleRecords(List<HoleRecord> exitHoleRecords) {
        this.exitHoleRecords = exitHoleRecords;
        notifyDataSetChanged();
    }

    public ExamineProcessAdapter(Context context, Collection<PreventionRecord> data, int layoutId) {
        super(context, data, layoutId);
    }

    public void setItemClickListener(ItemClickListener<PreventionRecord> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ViewHolder holder, final PreventionRecord record, final int position) {
        ArrayList<String> listPic = null;
        switch (position) {
            case 0:
                listPic = StringUtils.stringToList(record.main_controll_images == null ? "" : record.main_controll_images);
                break;
            case 1:
                listPic = StringUtils.stringToList(record.hyperbaric_images == null ? "" : record.hyperbaric_images);
                break;
            case 2:
                listPic = StringUtils.stringToList(record.one_device_images == null ? "" : record.one_device_images);
                break;
            case 3:
                listPic = StringUtils.stringToList(record.protect_images == null ? "" : record.protect_images);
                break;
            case 4:
                listPic = StringUtils.stringToList(record.cable_images == null ? "" : record.cable_images);
                break;
            case 5:
                listPic = StringUtils.stringToList(record.second_device_images == null ? "" : record.second_device_images);
                break;
            case 6:
                listPic = StringUtils.stringToList(record.other_images == null ? "" : record.other_images);
                break;
            default:
                break;
        }

        holder.setText(R.id.tv_examine_area, locations[position]);
        checkExitHole(locations[position], holder);

        TextView tvPicNum = holder.getView(R.id.tv_hole_num);
        ImageView ivShowPic = holder.getView(R.id.iv_show_pic);

        tvPicNum.setVisibility(View.GONE);
        ivShowPic.setVisibility(View.GONE);

        if (null != listPic && listPic.size() > 0) {


            if (listPic.size() > 1) {
                tvPicNum.setVisibility(View.VISIBLE);
                tvPicNum.setText("" + listPic.size());
            }
            ivShowPic.setVisibility(View.VISIBLE);
            String picName = listPic.get(0);
            Bitmap bmPicture = BitmapUtil.getOptimizedBitmap(Config.RESULT_PICTURES_FOLDER + picName);
            if (bmPicture != null) {
                ivShowPic.setImageBitmap(bmPicture);
            }
        }


        holder.getView(R.id.iv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != itemClickListener) {
                    view.setTag(locations[position]);
                    itemClickListener.itemClick(view, record, position);
                }
            }
        });
        ivShowPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != itemClickListener)
                    itemClickListener.itemClick(view, record, position);
            }
        });
    }

    public void checkExitHole(String location, ViewHolder holder) {

        holder.setVisibility(R.id.iv_sign_find_hole, View.GONE);
        ((TextView) holder.getView(R.id.tv_examine_area)).setTextColor(Color.BLACK);

        if (null != exitHoleRecords && !exitHoleRecords.isEmpty()) {
            for (HoleRecord record : exitHoleRecords) {
                if (location.equals(record.location)) {
                    holder.setVisibility(R.id.iv_sign_find_hole, View.VISIBLE);
//                    context.getResources().getColor(R.color.yellow_color)
                    ((TextView) holder.getView(R.id.tv_examine_area)).setTextColor(ContextCompat.getColor(context, R.color.yellow_color));
                    break;
                }
            }
        }
    }


}
