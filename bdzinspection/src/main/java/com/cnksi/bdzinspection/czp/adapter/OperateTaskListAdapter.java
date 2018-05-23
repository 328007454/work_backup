package com.cnksi.bdzinspection.czp.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsOperateTaskItemBinding;
import com.cnksi.bdzinspection.emnu.OperateTaskStatus;
import com.cnksi.bdzinspection.model.OperateTick;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * 操作任务Adapter
 * 
 * 
 */
public class OperateTaskListAdapter extends SimpleBaseAdapter {

	public OperateTaskListAdapter(Context context, List<? extends Object> dataList) {
		super(context, dataList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DbModel item = (DbModel) getItem(position);
		XsOperateTaskItemBinding itemBinding;
		if (convertView == null) {
			itemBinding = XsOperateTaskItemBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}
		
		itemBinding.tvContent.setText(item.getString(OperateTick.TASK));
		// TODO:区分状态颜色
		String statuskey = item.getString(OperateTick.STATUS);
		String statusValue = "";
		int statusColor = -1;
		if (OperateTaskStatus.yzt.name().equalsIgnoreCase(statuskey)) {
			// 已暂停
			statusValue = OperateTaskStatus.yzt.toString();
			statusColor = mContext.getResources().getColor(R.color.xs_orange_color);
		} else if (OperateTaskStatus.ywc.name().equalsIgnoreCase(statuskey)) {
			// 已完成
			statusValue = OperateTaskStatus.ywc.toString();
			statusColor = mContext.getResources().getColor(R.color.xs_grass_green_unpressed_color);
		} else if (OperateTaskStatus.wwc.name().equalsIgnoreCase(statuskey)) {
			// 未完成
			statusValue = OperateTaskStatus.wwc.toString();
			statusColor = mContext.getResources().getColor(R.color.xs_yellow_color);
		} else if (OperateTaskStatus.ytz.name().equalsIgnoreCase(statuskey)) {
			// 已停止
			statusValue = OperateTaskStatus.ytz.toString();
			statusColor = mContext.getResources().getColor(R.color.xs_red_color);
		} else {
			// 待审核
			statusValue = OperateTaskStatus.dsh.toString();
			statusColor = mContext.getResources().getColor(R.color.xs_global_base_color);
		}
		itemBinding.tvStatus.setText(statusValue);
		itemBinding.tvStatus.setTextColor(statusColor);
		itemBinding.tvCount.setText(mContext.getString(R.string.xs_czp_all_operate_item_format_str, item.getString("item_count")));

		return itemBinding.getRoot();
	}
}
