package com.cnksi.bdzinspection.adapter.BluetoothAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.cnksi.bdzinspection.R;

import java.util.List;
import java.util.Map;

/**
 * Created by han on 2017/1/5.
 */

public class RFIDReadDataAdapter extends SimpleAdapter {

    private LayoutInflater inflater = null;
    private List<Map<String, Object>> styles = null;

    public List<Map<String, Object>> getStyles() {
        return styles;
    }

    public void setStyles(List<Map<String, Object>> styles) {
        this.styles = styles;
    }

    public RFIDReadDataAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = super.getView(position, convertView, parent);
        if (result != null) {
            inflater.inflate(R.layout.xs_rfid_user, null);
        }
        return result;
    }
}
