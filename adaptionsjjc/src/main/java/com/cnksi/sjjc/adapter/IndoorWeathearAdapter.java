package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.databinding.AdapterIndoorItemBinding;

import java.util.List;


/**
 * Created by han on 2017/2/13.
 * 室内温湿度适配器
 */

public class IndoorWeathearAdapter extends BaseLinearBindingAdapter<AdapterIndoorItemBinding,ReportSnwsd> {
    private String location;
    private ItemClickListener imgClickListener;

    private final static String TAG = "indoor_weather_adapter";

    public IndoorWeathearAdapter(Context context, List<ReportSnwsd> data, LinearLayout container, int layoutId) {
        super(context, data, container, layoutId);
    }

    @Override
    public void convert(AdapterIndoorItemBinding holder, ReportSnwsd item, int position) {
        EditText etLocation =holder.etLocation;
        EditText etTempreture =holder.etTempreture;
        EditText etHumidity = holder.etCurrentHumidity;
        ImageButton imageButton = holder.addIndoorWeather;
        ImageButton deleteButton =holder.deleteIndoorWeather;

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
            if (null != imgClickListener) {
                imgClickListener.onClick(v, item, position);
            }
        });
        deleteButton.setOnClickListener(view -> {
            if (null != imgClickListener) {
                imgClickListener.onClick(view, item, position);
            }
        });
    }

    public ItemClickListener getImgClickListener() {
        return imgClickListener;
    }

    public void setImgClickListener(ItemClickListener imgClickListener) {
        this.imgClickListener = imgClickListener;
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
