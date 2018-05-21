package com.cnksi.bdzinspection.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseRecyclerAdapter;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.DevicePart;
import com.cnksi.bdzinspection.utils.Config;

import org.xutils.db.table.DbModel;

import java.util.Collection;

/**
 * Created by han on 2017/3/29.
 */

public class DevicePartRecylerAdapter extends BaseRecyclerAdapter<DbModel> {

    /**
     * 当前设备部件id
     */
    protected String currentDevicePartId = "";
    /**
     * 当前设备部件名称
     */
    private DevicePartClickListener listener;
    private int clickPosition = 0;
    private boolean isNew = false;


    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    public interface DevicePartClickListener {
        void devicePartClick1(View view);
    }

    public void setDevicePartListener(DevicePartClickListener listener) {
        this.listener = listener;
    }

    public DevicePartRecylerAdapter(RecyclerView v, Collection<DbModel> datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public void convert(RecyclerHolder holder, final DbModel item, final int position, boolean isScrolling) {
        final DbModel mDevicePart = item;
        final TextView mTvDevicePart = holder.getView(R.id.tv_device_part_name);
        final TextView mTvDevicePartName = holder.getView(R.id.iv_device_part_name);
        // 显示缺陷数量
        TextView mTvDefectCount = holder.getView(R.id.tv_device_defect_count);
        // 一定先判断最高级别是否为空
        if (!TextUtils.isEmpty(mDevicePart.getString(DefectRecord.DEFECTLEVEL))) {
            mTvDefectCount.setVisibility(View.VISIBLE);
            mTvDefectCount.setText(mDevicePart.getString(Config.DEFECT_COUNT_KEY));
            int backgrounResId = isNew ? R.drawable.xs_yellow_background_circle : R.color.xs_yellow_color;
            if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(mDevicePart.getString(DefectRecord.DEFECTLEVEL))) {
                backgrounResId = isNew ? R.drawable.xs_red_background_circle : R.color.xs_red_color;
            } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(mDevicePart.getString(DefectRecord.DEFECTLEVEL))) {
                backgrounResId = isNew ? R.drawable.xs_orange_background_circle : R.color.xs_orange_color;
            }
            mTvDefectCount.setBackgroundResource(backgrounResId);
        } else {
            mTvDefectCount.setVisibility(View.GONE);
        }

        if (position == clickPosition) {
            mTvDevicePartName.setSelected(true);
            mTvDevicePartName.setTextColor(mContext.getResources().getColor(R.color.xs_light_blue));
        } else {
            mTvDevicePartName.setSelected(false);
            mTvDevicePartName.setTextColor(mContext.getResources().getColor(R.color.xs_gray_text_color_new));
        }
        mTvDevicePart.setText(mDevicePart.getString(DevicePart.NAME));
        // 换成文字模式
        mTvDevicePartName.setText(mDevicePart.getString(DevicePart.NAME));
        mTvDevicePartName.setTag(R.id.xs_first_key, DevicePart.getPic(mDevicePart.getString(DevicePart.PIC)));
        mTvDevicePartName.setTag(R.id.xs_second_key, mDevicePart.getString(DevicePart.NAME));
        mTvDevicePartName.setTag(R.id.xs_thrid_key, mDevicePart.getString(DevicePart.DUID));
        mTvDevicePartName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPosition = position;
                listener.devicePartClick1(v);
            }
        });
    }
}