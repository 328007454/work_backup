package com.cnksi.bdzinspection.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.xscore.xsutils.BitmapHelp;
import com.cnksi.xscore.xsview.SectionedBaseAdapter;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.task.Priority;

@SuppressWarnings("deprecation")
public abstract class PinnedHeaderSectionedAdapter<E, T> extends SectionedBaseAdapter {

	private HashMap<E, ArrayList<T>> groupHashMap;
	private LinkedList<E> groupList;
	protected LayoutInflater mInflater;
	protected Context mContext;

	protected BitmapUtils mBitmapUtils;
	protected BitmapDisplayConfig mBitmapConfig;
	protected BitmapSize bitmapSize;
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
		mBitmapUtils = BitmapHelp.getInstance().getBitmapUtils(context);
		mBitmapUtils.configDefaultLoadingImage(R.drawable.xs_ic_app);
		mBitmapUtils.configDefaultLoadFailedImage(R.drawable.xs_ic_app);
		mBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		mBitmapUtils.configMemoryCacheEnabled(true);
	}

	/**
	 * @param context
	 * @param widthScale
	 *            缩放比例
	 * @param heightScale
	 *            缩放比例
	 */
	public void setScaleDownBitmapSize(int scaleDown) {
		mBitmapConfig = new BitmapDisplayConfig();
		bitmapSize = new BitmapSize(width, height).scaleDown(scaleDown);
		mBitmapConfig.setBitmapMaxSize(bitmapSize);
		mBitmapConfig.setPriority(Priority.UI_TOP);
		mBitmapConfig.setLoadFailedDrawable(mContext.getResources().getDrawable(R.drawable.xs_ic_app));
	}

	/**
	 * 缩放至指定宽高
	 *
	 * @param width
	 * @param height
	 */
	public void setBitmapSize(int width, int height) {
		mBitmapConfig = new BitmapDisplayConfig();
		bitmapSize = new BitmapSize(width, height);
		mBitmapConfig.setBitmapMaxSize(bitmapSize);
		mBitmapConfig.setPriority(Priority.UI_TOP);
		mBitmapConfig.setLoadFailedDrawable(mContext.getResources().getDrawable(R.drawable.xs_ic_app));
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
