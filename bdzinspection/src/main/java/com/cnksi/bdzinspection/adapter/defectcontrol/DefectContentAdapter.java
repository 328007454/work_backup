package com.cnksi.bdzinspection.adapter.defectcontrol;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseMapListExpandableAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogContentChildItemBinding;
import com.cnksi.bdzinspection.databinding.XsGroupItemBinding;
import com.cnksi.bdzinspection.model.DefectDefine;
import com.cnksi.common.Config;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * 选择缺陷定义Dialog对话框
 *
 * @author Joe
 */
public class DefectContentAdapter extends BaseMapListExpandableAdapter<String, DefectDefine> {

    public DefectContentAdapter(Context context) {
        super(context);
    }

    private OnAdapterViewClickListener mOnAdapterViewClickListener;

    public interface OnAdapterViewClickListener {
        void OnAdapterViewClick(View view, DefectDefine define);
    }

    public void setOnAdapterViewClickListener(OnAdapterViewClickListener mOnAdapterViewClickListener) {
        this.mOnAdapterViewClickListener = mOnAdapterViewClickListener;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        DefectDefine child = getChild(groupPosition, childPosition);
        XsDialogContentChildItemBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.xs_dialog_content_child_item, parent, false);
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.llContainer.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(120));

        itemBinding.tvChildItem.setText(child.description);
        itemBinding.imgChildItemBt.setVisibility(View.VISIBLE);

        itemBinding.imgChildItemBt.setOnClickListener(view -> {
            if (mOnAdapterViewClickListener != null) {
                mOnAdapterViewClickListener.OnAdapterViewClick(view, child);
            }
        });
        
        return itemBinding.getRoot();
    }
    

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group = getGroup(groupPosition);
        XsGroupItemBinding itemBinding=null;
        if (convertView == null) {
            itemBinding = XsGroupItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.imgOpen.setVisibility(View.GONE);
        if (Config.SERIOUS_LEVEL.equalsIgnoreCase(group)) {
           itemBinding.tvGroupItem.setTextColor(mContext.getResources().getColor(R.color.xs_orange_color));
        } else if (Config.CRISIS_LEVEL.equalsIgnoreCase(group)) {
           itemBinding.tvGroupItem.setTextColor(mContext.getResources().getColor(R.color.xs_red_color));
        } else {
           itemBinding.tvGroupItem.setTextColor(mContext.getResources().getColor(R.color.xs_yellow_color));
        }

       itemBinding.tvGroupItem.setText(group);

        return itemBinding.getRoot();
    }

}
