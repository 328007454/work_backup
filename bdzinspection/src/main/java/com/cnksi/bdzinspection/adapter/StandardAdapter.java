package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsItemStandardBinding;
import com.cnksi.common.model.Standards;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年4月22日 下午1:13:51 TODO
 */
public class StandardAdapter extends SimpleBaseAdapter {


    /**
     * @param context
     * @param dataList
     */
    public StandardAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DbModel mInspectionStandard = (DbModel) getItem(position);
        XsItemStandardBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsItemStandardBinding.inflate(LayoutInflater.from(mContext));
            AutoUtils.autoSize(itemBinding.getRoot());
            
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.llContainer.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(140));
        itemBinding.tvStandardName.setTextColor(mContext.getResources().getColor(R.color.xs_light_black));
        itemBinding.tvStandardName.setTextColor(getColor(isMarkStandard(mInspectionStandard) ? R.color.xs_global_gray_text_color : R.color.color_3c4950));
        itemBinding.tvStandardName.setText(String.valueOf(position + 1) + "、" + mInspectionStandard.getString(Standards.DESCRIPTION));
        return itemBinding.getRoot();
    }

    private boolean isMarkStandard(DbModel model) {
        return "Y".equals(model.getString("isMark"));
    }

}
