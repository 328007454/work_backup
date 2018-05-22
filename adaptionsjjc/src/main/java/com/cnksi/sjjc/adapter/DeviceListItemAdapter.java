package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.common.model.Device;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备列表(DeviceListActivity) 的 设备ListAdapter
 *
 * @author Joe
 */
public class DeviceListItemAdapter extends SimpleBaseAdapter {

    private ArrayList<Integer> currentShowAnimationPositionList = null;

    public DeviceListItemAdapter(Context context) {
        super(context);
    }

    public DeviceListItemAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    private OnAdapterViewClickListener mOnAdapterViewClickListener;

    public interface OnAdapterViewClickListener {
        void OnAdapterViewClick(View view, DbModel mDevice);

        boolean OnAdapterLongViewClick(View view, DbModel mDevice);
    }

    public void setOnAdapterViewClickListener(OnAdapterViewClickListener mOnAdapterViewClickListener) {
        this.mOnAdapterViewClickListener = mOnAdapterViewClickListener;
    }

    private boolean isStartAnimation = false;
    private boolean isShowAnimation = false;

    private boolean isSearch = false;
    private String searchValue = "";

    /**
     * @param isStartAnimation             是否开始动画 true开始 false 消失
     * @param isShowAnimation              是否显示动画
     * @param currentShowAnimationPosition
     */
    public void setAnimtion(boolean isStartAnimation, boolean isShowAnimation,
                            ArrayList<Integer> currentShowAnimationPosition) {
        this.isStartAnimation = isStartAnimation;
        this.isShowAnimation = isShowAnimation;
        this.currentShowAnimationPositionList = currentShowAnimationPosition;
        this.notifyDataSetChanged();
    }

    public void setHighLight(String searchValue, boolean isSearch) {
        this.isSearch = isSearch;
        this.searchValue = searchValue;
    }

    private boolean isShowGpsTips = false;

    public void setGpsTips(boolean isShowGpsTips) {
        this.isShowGpsTips = isShowGpsTips;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DbModel device = (DbModel) getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.device_item, parent, false);
            convertView.setTag(holder);
            holder.mRlDeviceContainer = (RelativeLayout) convertView.findViewById(R.id.rl_device_container);
            holder.mTvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.ibt_copy_pen);
            holder.mTvCopyValue = (TextView) convertView.findViewById(R.id.tv_copy_data);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mRlDeviceContainer.setOnClickListener(view -> {
            if (mOnAdapterViewClickListener != null) {
                mOnAdapterViewClickListener.OnAdapterViewClick(view, device);
            }
        });
        holder.mImageView.setVisibility(View.GONE);
        holder.setCurrentDefect(device);

        holder.mTvDeviceName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.mTvDeviceName.setText(device.getString(Device.NAME));


        return convertView;
    }

    class ViewHolder {
        private DbModel device;
        public void setCurrentDefect(DbModel device) {
            this.device = device;
        }
        RelativeLayout mRlDeviceContainer;
        TextView mTvDeviceName;
        ImageView mImageView;
        TextView mTvCopyValue;
    }
}
