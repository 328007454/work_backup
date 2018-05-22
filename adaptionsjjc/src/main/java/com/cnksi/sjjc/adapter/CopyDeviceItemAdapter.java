//package com.cnksi.sjjc.adapter;
//
//import android.content.Context;
//import android.support.v4.content.ContextCompat;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.cnksi.sjjc.R;
//import com.cnksi.common.model.Device;
//
//import org.xutils.db.table.DbModel;
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
//import java.util.List;
//import java.util.Map;
//
//
///**
// * 集中抄录数据的Adapter
// *
// * @author terry
// *
// */
//public class CopyDeviceItemAdapter extends SimpleBaseAdapter {
//
//	public CopyDeviceItemAdapter(Context context, List<? extends Object> dataList) {
//		super(context, dataList);
//	}
//
//	private Map<String,Boolean> copyedMap;
//
//	public void setCopyedMap(Map<String,Boolean> copyedMap) {
//		this.copyedMap = copyedMap;
//		this.notifyDataSetChanged();
//	}
//
//	private int currentSelectedPosition = 0;
//
//	public void setCurrentSelectedPosition(int currentSelectedPosition) {
//		this.currentSelectedPosition = currentSelectedPosition;
//		this.notifyDataSetInvalidated();
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		DbModel device = (DbModel) getItem(position);
//
//		ViewHolder holder = null;
//		if (convertView == null) {
//			holder = new ViewHolder();
//			convertView = mInflater.inflate(R.layout.device_item, parent, false);
//			x.view().inject(holder, convertView);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//
//		holder.mTvDeviceName.setText(device.getString(Device.NAME));
//
//		if (currentSelectedPosition == position) {
////			mContext.getResources().getColor(android.R.color.white)
//			holder.mRlDeviceContainer.setBackgroundResource(R.drawable.copy_all_value_item_selected_background);
//			holder.mTvDeviceName.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
//			holder.mTvDeviceName.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_white_unfinish, 0, 0, 0);
//		} else {
//			holder.mRlDeviceContainer
//					.setBackgroundResource(R.drawable.copy_all_value_item_unselected_background_selector);
////			mContext.getResources().getColor(R.color.green_color)
//			holder.mTvDeviceName.setTextColor(ContextCompat.getColor(mContext,R.color.green_color));
//			holder.mTvDeviceName.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_green_unfinish, 0, 0, 0);
//		}
//
//		// add lxy
//		if (null != copyedMap) {
//			Boolean b=copyedMap.get(device.getString(Device.DEVICEID));
//			if (null!=b&&b==true) {
//				holder.mTvDeviceName.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_green_finish, 0, 0, 0);// 显示抄录数据笔
//				convertView.setBackgroundResource(R.drawable.device_copyed);
//			}
//		}
//
//		return convertView;
//	}
//
//	class ViewHolder {
//
//		@ViewInject(R.id.rl_device_container)
//		RelativeLayout mRlDeviceContainer;
//
//		@ViewInject(R.id.tv_device_name)
//		TextView mTvDeviceName;
//	}
//
//}
