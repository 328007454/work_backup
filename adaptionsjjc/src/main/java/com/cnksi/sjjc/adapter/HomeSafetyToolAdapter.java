package com.cnksi.sjjc.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
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
 * @author wastrel
 * @date 2017/7/5 14:00
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class HomeSafetyToolAdapter extends BaseLinearBindingAdapter<ItemHomeSafetyToolBinding, DbModel> {
    View lookMore;

    public HomeSafetyToolAdapter(final Activity context, List<DbModel> data, final LinearLayout container) {
        super(context, data, container, R.layout.item_home_safety_tool);
        lookMore = LayoutInflater.from(context).inflate(R.layout.item_look_more, null, false);
        lookMore.setOnClickListener(v -> ActivityUtil.startSafetyRemindActivity(context));
    }

    @Override
    public void convert(ItemHomeSafetyToolBinding holder, DbModel item, int position) {
        holder.tvToolName.setText(item.getString("name"));
        String num = item.getString("num");
        holder.tvSerial.setText("编号：" + ("-1".equals(num) ? "" : num));
        String date = DateUtils.getFormatterTime(item.getString("next_check_time"), "yyyy/MM/dd");
        holder.tvDate.setText("下次试验时间：" + (TextUtils.isEmpty(date) ? "未知" : date));
        if ("-1".equals(item.getString("bdz_id"))) {
            holder.tvBdzShort.setText("班");
        } else {
            holder.tvBdzShort.setText(getShortName(item.getString("bdz_name")));
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor("#fd6067"));
        holder.tvBdzShort.setBackground(drawable);
    }

    private String getShortName(String bdzName) {
        if (TextUtils.isEmpty(bdzName)) {
            return "变";
        } else {
            for (char c : bdzName.toCharArray()) {
                if (c >= '\u4e00' && c <= '\u9fa5') {
                    return String.valueOf(c);
                }
            }
            return String.valueOf(bdzName.charAt(0));
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (getCount() > 0) {
            container.addView(lookMore);
        }
    }
}
