package com.cnksi.bdzinspection.adapter.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.cnksi.bdzinspection.adapter.DataWrap;

import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/5/18 17:08
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public abstract class BaseExpandListAdapter<T1, T2> extends BaseExpandableListAdapter {

    protected List<DataWrap<T1, T2>> dataList;

    @LayoutRes
    protected int groupLayoutId, childLayoutId;

    protected Context context;
    private LayoutInflater inflater;

    public BaseExpandListAdapter(Context context, List<DataWrap<T1, T2>> dataList, @LayoutRes int groupLayoutId, @LayoutRes int childLayoutId) {
        this.dataList = dataList;
        this.groupLayoutId = groupLayoutId;
        this.childLayoutId = childLayoutId;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<DataWrap<T1, T2>> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dataList.get(groupPosition).getChildCount();
    }

    @Override
    public T1 getGroup(int groupPosition) {
        return dataList.get(groupPosition).getObj();
    }

    @Override
    public T2 getChild(int groupPosition, int childPosition) {
        return dataList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition << 8 | childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewDataBinding binding = convertView == null ?
                DataBindingUtil.inflate(inflater, groupLayoutId, parent, false)
                : DataBindingUtil.findBinding(convertView);
        bindGroupView(binding, getGroup(groupPosition), isExpanded, groupPosition);
        return binding.getRoot();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewDataBinding binding = convertView == null ?
                DataBindingUtil.inflate(inflater, childLayoutId, parent, false)
                : DataBindingUtil.findBinding(convertView);
        bindChildView(binding, getChild(groupPosition, childPosition), groupPosition, childPosition, isLastChild);
        return binding.getRoot();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public abstract void bindGroupView(ViewDataBinding binding, T1 item, boolean isExpanded, int groupPosition);

    public abstract void bindChildView(ViewDataBinding binding, T2 item, int groupPosition, int childPosition, boolean isLastChild);
}
