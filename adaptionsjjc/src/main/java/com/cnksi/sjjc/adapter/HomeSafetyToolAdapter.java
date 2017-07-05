package com.cnksi.sjjc.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.ItemHomeSafetyToolBinding;

import org.xutils.db.table.DbModel;

import java.util.List;

import static com.cnksi.sjjc.bean.Users.DEPT_ID;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/5 14:00
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class HomeSafetyToolAdapter extends BaseLinearBindingAdapter<ItemHomeSafetyToolBinding, DbModel> {
    View lookMore;

    public HomeSafetyToolAdapter(final Context context, List<DbModel> data, final LinearLayout container) {
        super(context, data, container, R.layout.item_home_safety_tool);
        lookMore = LayoutInflater.from(context).inflate(R.layout.item_look_more, null, false);
        lookMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.maintenance.SafetyToolsRemindActivity"));
                intent.putExtra("dept_id", PreferencesUtils.get(CustomApplication.getAppContext(), DEPT_ID, ""));
                context.startActivity(intent);
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
