package com.cnksi.sjjc.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;


@SuppressWarnings("deprecation")
public abstract class BaseMapListExpandableAdapter<E, T> extends BaseExpandableListAdapter {

	protected HashMap<E, ArrayList<T>> groupHashMap;
	protected LinkedList<E> groupList;
	protected LayoutInflater mInflater;
	protected Context mContext;


	protected int width = 0;
	protected int height = 0;

	public BaseMapListExpandableAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
	}



	@Override
	public T getChild(int groupPosition, int childPosition) {

		return (groupHashMap == null || groupHashMap.isEmpty() || groupList == null || groupList.isEmpty()) ? null : groupHashMap.get(groupList.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return childPosition;
	}

	@Override
	public abstract View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

	@Override
	public int getChildrenCount(int groupPosition) {

		return (groupHashMap == null || groupList == null) ? 0 : groupHashMap.get(groupList.get(groupPosition)).size();
	}

	/**
	 * 得到某个Group下的所有数据
	 * 
	 * @param groupPosition
	 * @return
	 */
	public ArrayList<T> getChildList(int groupPosition) {

		return (groupHashMap == null || groupList == null) ? null : groupHashMap.get(groupList.get(groupPosition));
	}

	@Override
	public E getGroup(int groupPosition) {

		return (groupList == null || groupPosition > groupList.size() - 1) ? null : groupList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {

		return groupList == null ? 0 : groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public abstract View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

	public HashMap<E, ArrayList<T>> getGroupMap() {
		return groupHashMap;
	}

	public void setGroupMap(HashMap<E, ArrayList<T>> groupHashMap) {
		this.groupHashMap = groupHashMap;
		this.notifyDataSetChanged();
	}

	public LinkedList<E> getGroupList() {
		return groupList;
	}

	public void setGroupList(LinkedList<E> groupList) {
		this.groupList = groupList;
	}

	public void setGroupList(Set<E> set) {
		if (groupList == null) {
			groupList = new LinkedList<E>();
		} else {
			groupList.clear();
		}
		for (Iterator<E> iter = set.iterator(); iter.hasNext();) {
			groupList.add(iter.next());
		}
	}

}
