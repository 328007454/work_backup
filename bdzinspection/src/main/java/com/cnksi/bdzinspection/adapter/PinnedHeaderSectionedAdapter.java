package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.view.SectionedBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

@SuppressWarnings("deprecation")
public abstract class PinnedHeaderSectionedAdapter<E, T> extends SectionedBaseAdapter {

    private HashMap<E, ArrayList<T>> groupHashMap;
    private LinkedList<E> groupList;
    protected LayoutInflater mInflater;
    protected Context mContext;

    protected int width = 0;
    protected int height = 0;

    public PinnedHeaderSectionedAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    /**
     * 初始化图片加载器
     *
     * @param context
     */
    public void initBitmapUtils(Context context) {
    }

    /**
     * @param scaleDown 缩放
     */
    public void setScaleDownBitmapSize(int scaleDown) {
    }

    /**
     * 缩放至指定宽高
     *
     * @param width
     * @param height
     */
    public void setBitmapSize(int width, int height) {
    }

    public void setGroupList(Set<E> set) {
        if (groupList == null) {
            groupList = new LinkedList<E>();
        } else {
            groupList.clear();
        }
        for (Iterator<E> iter = set.iterator(); iter.hasNext(); ) {
            groupList.add(iter.next());
        }
    }

    public void setGroupList(LinkedList<E> groupList) {
        this.groupList = groupList;
    }

    public void setGroupMap(HashMap<E, ArrayList<T>> groupHashMap) {
        this.groupHashMap = groupHashMap;
        this.notifyDataSetChanged();
    }

    @Override
    public T getItem(int section, int position) {
        return groupHashMap.get(groupList.get(section)).get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return groupHashMap == null ? 0 : groupHashMap.keySet().size();
    }

    @Override
    public int getCountForSection(int section) {
        try {
            return groupHashMap.get(groupList.get(section)).size();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public E getSection(int section) {
        if (groupList != null && groupList.size() > section) {
            return groupList.get(section);
        } else {
            return null;
        }
    }

    @Override
    public abstract View getItemView(int section, int position, View convertView, ViewGroup parent);

    @Override
    public abstract View getSectionHeaderView(int section, View convertView, ViewGroup parent);

}
