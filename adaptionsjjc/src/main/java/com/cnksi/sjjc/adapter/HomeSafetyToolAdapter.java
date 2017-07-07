package com.cnksi.sjjc.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.ItemHomeSafetyToolBinding;
import com.cnksi.sjjc.util.ActivityUtil;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/5 14:00
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class HomeSafetyToolAdapter extends BaseLinearBindingAdapter<ItemHomeSafetyToolBinding, DbModel> {
    View lookMore;

    public HomeSafetyToolAdapter(final Activity context, List<DbModel> data, final LinearLayout container) {
        super(context, data, container, R.layout.item_home_safety_tool);
        lookMore = LayoutInflater.from(context).inflate(R.layout.item_look_more, null, false);
        lookMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startSafetyRemindActivity(context);
            }
        });
    }

    @Override
    public void convert(ItemHomeSafetyToolBinding holder, DbModel item, int position) {
        String date = DateUtils.formatDateTime(item.getString("next_check_time"), "yyyy/MM/dd");
        holder.tvDate.setText(date);
        holder.tvName.setText(item.getString("name"));
        holder.tvNum.setText(item.getString("num"));
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (getCount() > 0) container.addView(lookMore);
    }
}
