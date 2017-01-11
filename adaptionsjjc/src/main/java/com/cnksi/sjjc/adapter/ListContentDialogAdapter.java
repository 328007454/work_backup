package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.cnksi.sjjc.R;

import java.util.Collection;

public class ListContentDialogAdapter extends BaseAdapter<String> {

    public static final String LIST_STRING_TYPE = "list_string_type";
    public static final String LIST_DEVICEPART_TYPPE = "list_devicepart_type";
    public static final String LIST_BATTERY_TYPE = "list_battery_type";
    public static final String LIST_SPACING_TYPE = "list_spacing_type";

    private String listDataType = LIST_STRING_TYPE;

    public ListContentDialogAdapter(Context context, Collection<String> data, int layoutId) {
        super(context, data, layoutId);
    }

    public void setListStringDataType(String listDataType) {
        this.listDataType = listDataType;
    }
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		ViewHolder holder = null;
//		if (convertView == null) {
//			holder = new ViewHolder();
//			convertView = mInflater.inflate(R.layout.dialog_content_child_item, parent, false);
//			ViewUtils.inject(holder, convertView);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//		if (LIST_BATTERY_TYPE.equalsIgnoreCase(listDataType)) {
//			Battery battery = (Battery) getItem(position);
//			holder.mTvDefectContent.setTextColor(mContext.getResources().getColor(R.color.green_color));
//			holder.mTvDefectContent.setText(battery.name);
//		} else if (LIST_DEVICEPART_TYPPE.equalsIgnoreCase(listDataType)) {
//			DevicePart part = (DevicePart) getItem(position);
//			holder.mTvDefectContent.setTextColor(mContext.getResources().getColor(R.color.green_color));
//			holder.mTvDefectContent.setText(part.name);
//		} else if (LIST_SPACING_TYPE.equalsIgnoreCase(listDataType)) {
//			Spacing spacing = (Spacing) getItem(position);
//			holder.mTvDefectContent.setTextColor(mContext.getResources().getColor(R.color.green_color));
//			holder.mTvDefectContent.setText(spacing.name);
//		} else {
//			holder.mTvDefectContent.setTextColor(mContext.getResources().getColor(R.color.green_color));
//			holder.mTvDefectContent.setText(getItem(position).toString());
//		}
//
//		return convertView;
//	}


    @Override
    public void convert(ViewHolder holder, String item, int position) {
//		if (LIST_BATTERY_TYPE.equalsIgnoreCase(listDataType)) {
//			Battery battery = (Battery) getItem(position);
//			holder.mTvDefectContent.setTextColor(mContext.getResources().getColor(R.color.green_color));
//			holder.mTvDefectContent.setText(battery.name);
//		} else if (LIST_DEVICEPART_TYPPE.equalsIgnoreCase(listDataType)) {
//			DevicePart part = (DevicePart) getItem(position);
//			holder.mTvDefectContent.setTextColor(mContext.getResources().getColor(R.color.green_color));
//			holder.mTvDefectContent.setText(part.name);
//		} else if (LIST_SPACING_TYPE.equalsIgnoreCase(listDataType)) {
//			Spacing spacing = (Spacing) getItem(position);
//			holder.mTvDefectContent.setTextColor(mContext.getResources().getColor(R.color.green_color));
//			holder.mTvDefectContent.setText(spacing.name);
//		} else {
        TextView tv = holder.getView(R.id.tv_child_item);
//        context.getResources().getColor(R.color.green_color)
        tv.setTextColor(ContextCompat.getColor(context,R.color.green_color));
        tv.setText(getItem(position).toString());
//		}
    }
}
