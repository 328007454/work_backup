package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.core.view.FollowListView;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Spacing;

import org.xutils.db.table.DbModel;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;


public class DeviceExpandabelListAdapter extends BaseMapListExpandableAdapter<Spacing, DbModel>
        implements DeviceListItemAdapter.OnAdapterViewClickListener {

    private SparseArray<DeviceListItemAdapter> mAdapterMap = new SparseArray<DeviceListItemAdapter>();

    private OnAdapterViewClickListener mOnAdapterViewClickListener;

    public interface OnAdapterViewClickListener {

        void OnAdapterViewClick(View view, DbModel mDevice);

        void OnItemViewClick(AdapterView<?> parent, View view, DbModel mDevice, Spacing mCurrentSpacing);

        boolean onItemLongClick(AdapterView<?> parent, View view, DbModel mDevice, Spacing mCurrentSpacing);

        boolean OnGroupItemLongClick(Spacing mSpacing);

        void OnGroupItemClick(Spacing mSpacing, View v, int groupPosition);
    }

    public void setOnAdapterViewClickListener(OnAdapterViewClickListener mOnAdapterViewClickListener) {
        this.mOnAdapterViewClickListener = mOnAdapterViewClickListener;
    }

    private boolean isStartAnimation = false;
    private boolean isShowAnimation = false;

    public void setAnimation(boolean isStartAnimation, boolean isShowAnimation) {
        this.isStartAnimation = isStartAnimation;
        this.isShowAnimation = isShowAnimation;
        this.notifyDataSetChanged();
    }

    // 定位好的间隔位置和设备位置
    private SparseArray<ArrayList<Integer>> animationPositionArray = null;

    public void setCurrentShowAnimationPosition(SparseArray<ArrayList<Integer>> animationPositionArray) {
        this.animationPositionArray = animationPositionArray;
    }

    private boolean isShowGpsTips = false;

    public void setGpsTips(boolean isShowGpsTips) {
        this.isShowGpsTips = isShowGpsTips;
        this.notifyDataSetChanged();
    }

    public DeviceExpandabelListAdapter(Context context) {
        super(context);
    }

    @Override
    public void setGroupMap(HashMap<Spacing, ArrayList<DbModel>> groupMap) {
        mAdapterMap.clear();
        super.setGroupMap(groupMap);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public int getChildrenCountByGroup(int groupPosition) {
        return (groupHashMap == null || groupList == null) ? 0 : groupHashMap.get(groupList.get(groupPosition)).size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {

        ViewChildHolder holder = null;
        if (convertView == null) {
            holder = new ViewChildHolder();
            convertView = mInflater.inflate(R.layout.expandable_gridview_child_item, parent, false);
            RelayoutUtil.reLayoutViewHierarchy(convertView);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewChildHolder) convertView.getTag();
        }

        DeviceListItemAdapter mDeviceItemAdapter = mAdapterMap.get(groupPosition);
        if (mDeviceItemAdapter == null) {
            mDeviceItemAdapter = new DeviceListItemAdapter(mContext);
            mDeviceItemAdapter.setOnAdapterViewClickListener(this);
            mAdapterMap.put(groupPosition, mDeviceItemAdapter);
        }
        if (animationPositionArray != null && animationPositionArray.size() > 0) {
            for (int i = 0, count = animationPositionArray.size(); i < count; i++) {
                if (groupPosition == animationPositionArray.keyAt(i)) {
                    mDeviceItemAdapter.setAnimtion(isStartAnimation, isShowAnimation,
                            animationPositionArray.get(groupPosition));
                }
            }
        }
        ArrayList<DbModel> mDeviceList = getChildList(groupPosition);
        mDeviceItemAdapter.setList(mDeviceList);
        holder.setCurrentDeviceListAndSpacing(mDeviceList, getGroup(groupPosition));
        holder.mFollowListView.setAdapter(mDeviceItemAdapter);

        return convertView;
    }

    class ViewChildHolder {
        private ArrayList<DbModel> mDeviceList;

        private Spacing mCurrentSpacing;

        public void setCurrentDeviceListAndSpacing(ArrayList<DbModel> mDeviceList, Spacing mCurrentSpacing) {
            this.mDeviceList = mDeviceList;
            this.mCurrentSpacing = mCurrentSpacing;
        }

        @ViewInject(R.id.gv_container)
        private FollowListView mFollowListView;

        @Event(R.id.gv_container)
        private void OnItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnAdapterViewClickListener != null) {
                mOnAdapterViewClickListener.OnItemViewClick(parent, view, mDeviceList.get(position), mCurrentSpacing);
            }
        }

        @Event(R.id.gv_container)
        private boolean OnItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnAdapterViewClickListener != null) {
                return mOnAdapterViewClickListener.onItemLongClick(parent, view, mDeviceList.get(position),
                        mCurrentSpacing);
            }
            return false;
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Spacing group = getGroup(groupPosition);

        ViewGroupHolder holder = null;
        if (convertView == null) {
            holder = new ViewGroupHolder();
            convertView = mInflater.inflate(R.layout.group_item, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewGroupHolder) convertView.getTag();
        }
        holder.mSpacing = group;
        holder.groupPosition = groupPosition;
        convertView.setBackgroundResource(R.drawable.gray_background_selector);
        convertView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.global_padding_left_right),
                mContext.getResources().getDimensionPixelSize(R.dimen.global_padding_top_bottom),
                mContext.getResources().getDimensionPixelSize(R.dimen.global_padding_left_right),
                mContext.getResources().getDimensionPixelSize(R.dimen.global_padding_top_bottom));
        //mContext.getResources().getColor(R.color.green_color)
        holder.mTvDeviceInterval.setTextColor(ContextCompat.getColor(mContext,R.color.green_color));

//		if (getChildrenCountByGroup(groupPosition) > 1) {
        if (true) {
            holder.mTvDeviceInterval
                    .setText(TextUtils.isEmpty(group.name)?"":group.name + "(" + String.valueOf(getChildrenCountByGroup(groupPosition)) + ")");
            if (isExpanded) {
                holder.mTvDeviceInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_shrink, 0);
            } else {
                holder.mTvDeviceInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_open, 0);
            }
        } else
        {
            holder.mTvDeviceInterval.setText(group.name);
            holder.mTvDeviceInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        return convertView;
    }

    class ViewGroupHolder {

        public Spacing mSpacing;

        public int groupPosition;

        @ViewInject(R.id.tv_group_item)
        TextView mTvDeviceInterval;


        @Event({R.id.tv_group_item})
        private void OnViewClick(View view) {
            if (mOnAdapterViewClickListener != null) {
                mOnAdapterViewClickListener.OnGroupItemClick(mSpacing, view, groupPosition);
            }
        }

    }

    @Override
    public void OnAdapterViewClick(View view, DbModel mDevice) {
        if (mOnAdapterViewClickListener != null) {
            mOnAdapterViewClickListener.OnAdapterViewClick(view, mDevice);
        }
    }

    @Override
    public boolean OnAdapterLongViewClick(View view, DbModel mDevice) {
        return false;
    }
}
