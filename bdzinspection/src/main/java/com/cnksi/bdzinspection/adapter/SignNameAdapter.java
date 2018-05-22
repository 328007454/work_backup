package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsItemSignBinding;
import com.cnksi.bdzinspection.model.ReportSignname;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.ScreenUtils;
import com.cnksi.core.utils.BitmapUtils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年8月10日 上午9:07:17 TODO
 */
public class SignNameAdapter extends SimpleBaseAdapter {

    AdapterClickListener listener;
    int MASK = 0x0000;
    private int totoalUserCount;

    /**
     * @param context
     * @param dataList
     */
    public SignNameAdapter(Context context, List<? extends Object> dataList, AdapterClickListener listener, int MASK) {
        super(context, dataList);
        this.listener = listener;
        this.MASK = MASK;
        initBitmapUtils(mContext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        XsItemSignBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsItemSignBinding.inflate(LayoutInflater.from(mContext));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        ReportSignname bean = (ReportSignname) getItem(position);
        itemBinding.name.setText(bean.getName());
        if (TextUtils.isEmpty(bean.getSignName())) {
           itemBinding.tvCzrSign.setVisibility(View.VISIBLE);
            itemBinding.llContainerSign.setVisibility(View.VISIBLE);
            itemBinding.signImg.setVisibility(View.GONE);
        } else {
           itemBinding.tvCzrSign.setVisibility(View.GONE);
            itemBinding.signImg.setVisibility(View.VISIBLE);
            itemBinding.llContainerSign.setVisibility(View.GONE);
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.CUSTOMER_PICTURES_FOLDER + bean.getSignName(), ScreenUtils.getScreenWidth(mContext));
            if (bitmap !=null){
                itemBinding.signImg.setImageBitmap(bitmap);
            }
        }
        if (position < totoalUserCount) {
           itemBinding.del.setVisibility(View.GONE);
        } else {
           itemBinding.del.setVisibility(View.VISIBLE);
        }

        itemBinding.del.setOnClickListener(v -> {
            listener.OnAdapterClick(v, position, MASK | 0);
        });

        itemBinding.tvCzrSign.setOnClickListener(v -> {
            listener.OnAdapterClick(v, position, MASK | 1);
        });

        itemBinding.signImgPic.setOnClickListener(v -> {
            listener.OnAdapterClick(v, position, MASK | 1);
        });

        itemBinding.llContainerSign.setOnClickListener(view -> {
            listener.OnAdapterClick(view, position, MASK | 1);
        });

        itemBinding.signImg.setOnClickListener(view -> {
            listener.OnAdapterClick(view, position, MASK | 2);
        });


        return itemBinding.getRoot();
    }

    public void setUserCount(int user) {
        totoalUserCount = user;
    }

}
