package com.cnksi.bdzinspection.adapter.defectcontrol;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogContentChildItemBinding;
import com.cnksi.bdzinspection.model.Lookup;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

/**
 * 选择缺陷原因Adapter
 *
 * @author Oliver
 */
public class DefectReasonDialogAdapter extends SimpleBaseAdapter {

    public DefectReasonDialogAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    private int currentSelectedPosition = -1;

    /**
     * 当前选择的项目
     *
     * @param
     */
    public void setCurrentSelectedPosition(int currentSelectedPosition) {
        this.currentSelectedPosition = currentSelectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lookup mDefectReason = (Lookup) getItem(position);
        XsDialogContentChildItemBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.xs_dialog_content_child_item, parent, false);
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.llContainer.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(120));
        if (position == currentSelectedPosition) {
            convertView.setBackgroundResource(0);
        } else {

        }
        itemBinding.tvChildItem.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
        itemBinding.tvChildItem.setText(mDefectReason.v);

        return itemBinding.getRoot();
    }

}
