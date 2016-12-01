package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zhy.autolayout.utils.AutoUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by han on 2016/8/1.
 */
public abstract class SimpleBindingAdatpter<T> extends BaseAdapter {
    private Collection<T> list ;
    private Context context;
    private ViewDataBinding binding;
    private int layoutId;

    public SimpleBindingAdatpter(){}

    public SimpleBindingAdatpter(Context context, Collection<T> list, int layoutId){
        this.list = list;
        this.context = context;
        this.layoutId = layoutId;
    }
    public abstract void convert(ViewDataBinding dataBinding,T item,int position);

    @Override
    public int getCount() {
        return null==list?0:list.size();
    }

    @Override
    public T getItem(int i) {
        return null==list?null:  (T)Arrays.asList(list.toArray()).get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(null==view){
            binding = DataBindingUtil.inflate(LayoutInflater.from(context),layoutId,viewGroup,false);
            view = binding.getRoot();
            AutoUtils.auto(view);
            view.setTag(binding);
        }else {
            binding = (ViewDataBinding) view.getTag();
        }
        convert(binding,getItem(i),i);
        return view;
    }

    public void setList(Collection<T> list){
        this.list = list;
        notifyDataSetChanged();
    }

}
