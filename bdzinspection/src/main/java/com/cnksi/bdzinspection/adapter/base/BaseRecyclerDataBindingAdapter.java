package com.cnksi.bdzinspection.adapter.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.holder.ItemHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * RecyclerView的万能适配器，适配任何一个RecyclerView
 *
 */
public abstract class BaseRecyclerDataBindingAdapter<T> extends RecyclerView.Adapter<ItemHolder> {

	protected List<T> realDatas;
	protected final int mItemLayoutId;
	protected boolean isScrolling;
	protected Context mContext;
	private OnItemClickListener listener;

	public interface OnItemClickListener {
		void onAdapterItemClick(View view, Object data, int position);
	}

	public BaseRecyclerDataBindingAdapter(RecyclerView v, Collection<T> datas, int itemLayoutId) {
		if (datas == null) {
			realDatas = new ArrayList<>();
		} else if (datas instanceof List) {
			realDatas = (List<T>) datas;
		} else {
			realDatas = new ArrayList<>(datas);
		}
		mItemLayoutId = itemLayoutId;
		mContext = v.getContext();

	}

	/**
	 * Recycler适配器填充方法
	 *
	 * @param holder
	 *            viewholder
	 * @param item
	 *            javabean
	 * @param isScrolling
	 *            RecyclerView是否正在滚动
	 */
	public abstract void convert(ItemHolder holder, T item, int position, boolean isScrolling);

	@Override
	public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		ViewDataBinding dataBinding = DataBindingUtil.inflate(inflater,mItemLayoutId,parent,false);
//		View root = inflater.inflate(mItemLayoutId, parent, false);
		return new ItemHolder(dataBinding);
	}

	@Override
	public void onBindViewHolder(ItemHolder holder, int position) {
		convert(holder, realDatas.get(position), position, isScrolling);
		holder.getDataBinding().getRoot().setOnClickListener(getOnClickListener(position));
	}

	@Override
	public int getItemCount() {
		return realDatas.size();
	}

	public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
		listener = mOnItemClickListener;
	}

	public View.OnClickListener getOnClickListener(final int position) {
		return new View.OnClickListener() {
			@Override
			public void onClick(@Nullable View v) {
				if (listener != null && v != null) {
					listener.onAdapterItemClick(v, realDatas.get(position), position);
				}
			}
		};
	}

	public BaseRecyclerDataBindingAdapter<T> refresh(Collection<T> datas) {
		if (datas == null) {
			realDatas = new ArrayList<>();
		} else if (datas instanceof List) {
			realDatas = (List<T>) datas;
		} else {
			realDatas = new ArrayList<>(datas);
		}
		this.notifyDataSetChanged();
		return this;
	}

	public void setList(Collection<T> data){
		realDatas= (List<T>) data;
		notifyDataSetChanged();
	}

}
