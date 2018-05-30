package com.cnksi.bdzinspection.adapter.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseAdapter;
import com.cnksi.common.utils.ViewHolder;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by han on 2016/12/8.
 */

public class BlueDeviceAdapter extends BaseAdapter<BluetoothDevice> {

    private ArrayList<BluetoothDevice> deviceList;
    @SuppressWarnings("unchecked")
    public BlueDeviceAdapter(Context context, Collection data, int layoutId) {
        super(context, data, layoutId);
    }

    public void setDeviceList(ArrayList<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder holder, BluetoothDevice item, int position) {
        TextView tvName = holder.getView(R.id.txt_device_name);
        tvName.append(item.getName() + "--" + item.getAddress());
    }
}
