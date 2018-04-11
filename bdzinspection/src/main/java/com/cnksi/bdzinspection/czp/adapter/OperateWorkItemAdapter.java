package com.cnksi.bdzinspection.czp.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsOperateWorkItemBinding;
import com.cnksi.bdzinspection.model.OperateItem;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

/**
 * 危险点
 *
 * @author terry
 */
public class OperateWorkItemAdapter extends SimpleBaseAdapter {

    public int currentOperateItemPosition = -1;
    public boolean isCheckedOperate = false;

    public OperateWorkItemAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    public void setCurrentOperateItemPosition(int currentOperateItemPosition) {
        this.currentOperateItemPosition = currentOperateItemPosition;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OperateItem item = (OperateItem) getItem(position);
        XsOperateWorkItemBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsOperateWorkItemBinding.inflate(LayoutInflater.from(mContext));
            AutoUtils.autoSize(itemBinding.getRoot());

        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.tvNumber.setText(String.valueOf(position + 1));
        itemBinding.tvContent.setText(item.content);
        itemBinding.tvCompleteTime.setText(mContext.getString(R.string.xs_operate_complete_time_format_str, item.time_end));
        if (isCheckedOperate) {
            itemBinding.tvContent.setTextColor(mContext.getResources().getColor(R.color.xs_global_text_color));
            itemBinding.tvNumber.setBackgroundResource(R.drawable.xs_gray_circle_background);
        } else {
            // TODO:进行逻辑的处理 显示 颜色控制
            itemBinding.tvCompleteTime.setVisibility(TextUtils.isEmpty(item.time_end) ? View.GONE : View.VISIBLE);
            if (currentOperateItemPosition == position) {
                // 正在操作项目
                itemBinding.tvContent.setTextColor(mContext.getResources().getColor(R.color.xs_red_color));
                itemBinding.tvNumber.setBackgroundResource(R.drawable.xs_red_circle_background_selector);
                itemBinding.tvTimeSpend.setVisibility(View.INVISIBLE);
            } else if (!TextUtils.isEmpty(item.spend_time)) {
                // 操作完成项目
                itemBinding.tvContent.setTextColor(mContext.getResources().getColor(R.color.xs_grass_green_unpressed_color));
                itemBinding.tvNumber.setBackgroundResource(R.drawable.xs_grass_green_circle_background);
                itemBinding.tvTimeSpend.setVisibility(View.VISIBLE);
                itemBinding.tvTimeSpend.setText(item.spend_time);
            } else {
                // 还未操作项目
                itemBinding.tvNumber.setBackgroundResource(R.drawable.xs_gray_circle_background);
                itemBinding.tvContent.setTextColor(mContext.getResources().getColor(R.color.xs_global_gray_text_color));
                itemBinding.tvTimeSpend.setVisibility(View.INVISIBLE);
            }
        }
        return itemBinding.getRoot();
    }
}
