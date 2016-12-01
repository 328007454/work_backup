package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.sjjc.R;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by han on 2016/5/9.
 */
public class PanoramaListAdapter extends SimpleBaseAdapter {

    public PanoramaListAdapter(Context context){
        super(context);
    }
    public PanoramaListAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String itemTitle = (String) getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.expandlist_jichu_listview_item, parent, false);
            x.view().inject(holder,convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(itemTitle.contains("日期")){
            holder.etJCName.setVisibility(View.GONE);
            holder.tvCalendar.setVisibility(View.VISIBLE);
        }else{
            holder.etJCName.setVisibility(View.VISIBLE);
            holder.tvCalendar.setVisibility(View.GONE);
        }
        holder.tvJCName.setText(itemTitle);

        return convertView;
    }

    class ViewHolder{
        @ViewInject(R.id.tv_jichu_name)
        private TextView tvJCName;
        @ViewInject(R.id.et_input_jichu_part)
        private EditText etJCName;
        @ViewInject(R.id.tv_calendar)
        private TextView tvCalendar;
        @Event({R.id.et_input_jichu_part})
        private void onViewClick(View view){
            switch (view.getId()){
                case R.id.et_input_jichu_part:

                    break;
            }
        }
    }
}
