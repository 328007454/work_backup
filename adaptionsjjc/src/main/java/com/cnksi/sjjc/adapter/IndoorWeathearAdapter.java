package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.List;


/**
 * Created by han on 2017/2/13.
 */

public class IndoorWeathearAdapter extends BaseLinearLayoutAdapter<ReportSnwsd> {
    private String location;
    private ItemClickListener itemClickListener;

    public IndoorWeathearAdapter(Context context, List data, LinearLayout container, int layoutId) {
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

        etLocation.setTag(ReportSnwsd.LOCATION);
        etTempreture.setTag(ReportSnwsd.WD);
        etHumidity.setTag(ReportSnwsd.SD);

        if (position == getCount() - 1)
            imageButton.setVisibility(View.VISIBLE);
        else
            imageButton.setVisibility(View.GONE);

        if (0 == position) {
            etLocation.setText(TextUtils.isEmpty(item.location) ? getLocation() : item.location);
            item.location = getLocation();
        } else
            etLocation.setText(item.location);
        etTempreture.setText(item.wd);
        etHumidity.setText(item.sd);

        etLocation.addTextChangedListener(new MyTextWatcher(etLocation, position));
        etTempreture.addTextChangedListener(new MyTextWatcher(etTempreture, position));
        etHumidity.addTextChangedListener(new MyTextWatcher(etHumidity, position));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemClickListener)
                    itemClickListener.itemClick(v, item, position);
            }
        });


    }

    class MyTextWatcher implements TextWatcher {
        private int position;
        private EditText inputEdittext;

        public MyTextWatcher(EditText editText, int position) {
            this.position = position;
            this.inputEdittext = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ReportSnwsd reportSnwsd = (ReportSnwsd) getItem(position);
            if (ReportSnwsd.LOCATION.equalsIgnoreCase(inputEdittext.getTag().toString()))
                reportSnwsd.location = s.toString();
            if (ReportSnwsd.WD.equalsIgnoreCase(inputEdittext.getTag().toString()))
                reportSnwsd.wd = s.toString();
            if (ReportSnwsd.SD.equalsIgnoreCase(inputEdittext.getTag().toString()))
                reportSnwsd.sd = s.toString();
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
