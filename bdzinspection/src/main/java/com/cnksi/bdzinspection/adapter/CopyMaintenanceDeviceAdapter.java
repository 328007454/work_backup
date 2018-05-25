package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseAdapter;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.common.model.CopyItem;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CopyMaintenanceDeviceAdapter extends BaseAdapter<CopyItem> {

	private int currentSelectedPosition;

	private ItemClickListener<CopyItem> itemClickListener;

	private HashMap<String ,String> copyHashMap = new HashMap<>();

	public CopyMaintenanceDeviceAdapter(Context context, Collection<CopyItem> data, int layoutId) {
		super(context, data, layoutId);
	}

	public void setCurrentSelectedPosition(int currentSelectedPosition) {
		this.currentSelectedPosition = currentSelectedPosition;
		this.notifyDataSetChanged();
	}

	public void setItemClickListener(ItemClickListener<CopyItem> itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	@Override
	public void convert(ViewHolder holder, final CopyItem item, final int position) {
		TextView txtDevice = holder.getView(R.id.tv_device_name);
		ImageView ibCopy = holder.getView(R.id.ibt_copy_pen);
		holder.getView(R.id.tv_device_defect_count).setVisibility(View.GONE);
		txtDevice.setText(item.device_name);
		RelativeLayout relativeLayout = holder.getView(R.id.rl_device_container);
		relativeLayout.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(105));
		relativeLayout.setMinimumWidth(AutoUtils.getPercentWidthSizeBigger(270));
		if (currentSelectedPosition == position) {
			txtDevice.setTextColor(context.getResources().getColor(R.color.xs_white));
			ibCopy.setImageResource(R.drawable.ic_white_unfinish);
			holder.getView(R.id.rl_device_container)
					.setBackgroundResource(R.drawable.xs_copy_all_value_item_selected_background);
		} else {
			txtDevice.setTextColor(context.getResources().getColor(R.color.xs_green_color));
			ibCopy.setImageResource(R.drawable.ic_green_unfinish);
			holder.getView(R.id.rl_device_container)
					.setBackgroundResource(R.drawable.xs_copy_all_value_item_unselected_background_selector);
		}
		// 有一项抄录变绿
		if(null!=copyHashMap&& !TextUtils.isEmpty(copyHashMap.get(item.id))){
			ibCopy.setImageResource(R.drawable.ic_green_finish);
		}

		holder.getRootView().setOnClickListener(v -> itemClickListener.onItemClick(v, item, position));
	}

	public void pre() {
		if (currentSelectedPosition > 0) {
            itemClickListener.onItemClick(null, new ArrayList<>(data).get(currentSelectedPosition - 1),
                    currentSelectedPosition - 1);
        }
	}

	public void next() {
		if (currentSelectedPosition < data.size() - 1) {
            itemClickListener.onItemClick(null, new ArrayList<>(data).get(currentSelectedPosition + 1),
                    currentSelectedPosition + 1);
        }
	}

	public boolean isLast() {
		if (data.isEmpty()) {
            return true;
        }
		return currentSelectedPosition == data.size() - 1;
	}

	public boolean isFirst() {
		return currentSelectedPosition == 0;
	}

	public void setCopyDevice(HashMap<String, String> copyHashSet) {
		this.copyHashMap= copyHashSet;
	}
}
