package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.List;


/**
 * Created by han on 2017/2/13.
 * 室内温湿度适配器
 */

public class IndoorWeathearAdapter extends BaseLinearLayoutAdapter<ReportSnwsd> {
    private String location;
    private ItemClickListener itemClickListener;

    private final static String TAG = "indoor_weather_adapter";

    public IndoorWeathearAdapter(Context context, List<ReportSnwsd> data, LinearLayout container, int layoutId) {
        super(context, data, container, layoutId);
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ViewHolder holder, final ReportSnwsd item, final int position) {
        EditText etLocation = holder.getView(R.id.et_location);
        EditText etTempreture = holder.getView(R.id.et_tempreture);
        EditText etHumidity = holder.getView(R.id.et_current_humidity);
        ImageButton imageButton = holder.getView(R.id.add_indoor_weather);
        ImageButton deleteButton = holder.getView(R.id.delete_indoor_weather);

        MyTextWatcher watcherLocation = (MyTextWatcher) etLocation.getTag();
        if (watcherLocation == null) {
            etLocation.addTextChangedListener(watcherLocation = new MyTextWatcher(etLocation, position, ReportSnwsd.LOCATION));
            etLocation.setTag(watcherLocation);
        } else {
            watcherLocation.update(position, ReportSnwsd.LOCATION);
        }


        MyTextWatcher watcherTempreture = (MyTextWatcher) etTempreture.getTag();
        if (watcherTempreture == null) {
            etTempreture.addTextChangedListener(watcherTempreture = new MyTextWatcher(etTempreture, position, ReportSnwsd.WD));
            etTempreture.setTag(watcherTempreture);
        } else {
            watcherTempreture.update(position, ReportSnwsd.WD);
        }


        MyTextWatcher watcherHumidity = (MyTextWatcher) etHumidity.getTag();
        if (watcherHumidity == null) {
            etHumidity.addTextChangedListener(watcherHumidity = new MyTextWatcher(etHumidity, position, ReportSnwsd.SD));
            etHumidity.setTag(watcherHumidity);
        } else {
            watcherHumidity.update(position, ReportSnwsd.SD);
        }


        if (position == getCount() - 1) {
            imageButton.setVisibility(View.VISIBLE);
        } else {
            imageButton.setVisibility(View.GONE);
        }

        if (0 == position) {
            etLocation.setText(TextUtils.isEmpty(item.location) ? getLocation() : item.location);
            deleteButton.setVisibility(View.GONE);
        } else {
            etLocation.setText(item.location);
            deleteButton.setVisibility(View.VISIBLE);
        }
        etTempreture.setText(item.wd);
        etHumidity.setText(item.sd);


        imageButton.setOnClickListener(v -> {
            if (null != itemClickListener) {
                itemClickListener.itemClick(v, item, position);
            }
        });
        deleteButton.setOnClickListener(view -> {
            if (null != itemClickListener) {
                itemClickListener.itemClick(view, item, position);
            }
        });

    }

    class MyTextWatcher implements TextWatcher {
        private int position;
        private EditText inputEdittext;
        private String tag;

        public MyTextWatcher(EditText editText, int position, String tag) {
            this.position = position;
            this.tag = tag;
            this.inputEdittext = editText;
        }

        public void update(int position, String tag) {
            this.position = position;
            this.tag = tag;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ReportSnwsd reportSnwsd = getItem(position);
            if (ReportSnwsd.LOCATION.equalsIgnoreCase(tag)) {
                reportSnwsd.location = s.toString();
            }
            if (ReportSnwsd.WD.equalsIgnoreCase(tag)) {
                reportSnwsd.wd = StringUtilsExt.getDecimalPoint(s.toString());
            }
            if (ReportSnwsd.SD.equalsIgnoreCase(tag)) {
                reportSnwsd.sd = StringUtilsExt.getDecimalPoint(s.toString());
            }
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        notifyDataSetChanged();
    }
}
