package com.cnksi.bdzinspection.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseRecyclerAdapter;
import com.cnksi.bdzinspection.emnu.ToolStatus;
import com.cnksi.bdzinspection.view.SwipeMenuLayout;
import com.cnksi.core.utils.DateUtils;

import org.xutils.db.table.DbModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 单个变电站工器具列表
 * Created by han on 2017/6/29.
 */

public class SafeToolsInfoAdapter extends BaseRecyclerAdapter<DbModel> {
    private HashMap<String, Boolean> checkMap = new HashMap<>();


    ItemClickListener listener;

    public SafeToolsInfoAdapter(RecyclerView v, Collection<DbModel> datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
    }

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }


    /**
     * 工器具是否被选中
     */
    private boolean isSelected;

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        notifyDataSetChanged();
    }

    private boolean isSelectedAll;

    public void setSelectedAll(boolean selectedAll) {
        this.isSelectedAll = selectedAll;
        if (selectedAll) {
            checkMap.clear();
            List<DbModel> modelList = (List<DbModel>) realDatas;
            for (DbModel dbModel : modelList) {
                checkMap.put(dbModel.getString("id"), true);
            }
        }

    }

    /**
     * 清空选中的工器具
     */
    public void setClearCheckMap() {
        checkMap.clear();
    }

    public HashMap<String, Boolean> getSelecCBMap() {
        return this.checkMap;
    }

    public void setCheckMap(View v, HashSet<DbModel> selectmodels) {
        DbModel dbModel = null;
        CheckBox box = null;
        if (v.getTag() instanceof DbModel) {
            box = (CheckBox) v;
            dbModel = (DbModel) v.getTag();
            if (box.isChecked()) {
                selectmodels.add(dbModel);
            } else {
                selectmodels.remove(dbModel);
            }
        } else if (v.getTag() instanceof View) {
            box = (CheckBox) v.getTag();
            dbModel = (DbModel) box.getTag();
            if (box.isChecked()) {
                box.setChecked(false);
                selectmodels.remove(dbModel);
            } else {
                box.setChecked(true);
                selectmodels.add(dbModel);
            }
        }
    }

    private boolean isSwipeLeft = true;

    public void setRootViewDrag(boolean isSwipeLeft) {
        this.isSwipeLeft = isSwipeLeft;
    }


    @Override
    public void convert(RecyclerHolder holder, DbModel item, final int position, boolean isScrolling) {
        final DbModel dbModel = (DbModel) item;
        String date = dbModel.getString("next_check_time");
        String currentTime = DateUtils.getCurrentLongTime();
        String status = dbModel.getString("status");

        TextView orderView = ((holder.getView(R.id.txt_tool_order)));//序号
        TextView toolName = ((holder.getView(R.id.txt_tool_name)));//工器具名称
        TextView toolNum = ((holder.getView(R.id.txt_tool_num)));//工器具编号
        TextView toolNextTime = ((holder.getView(R.id.txt_tool_next)));//工器具下次试验时间
        holder.getView(R.id.txt_stop).setVisibility(View.VISIBLE);//停用按钮
        ((SwipeMenuLayout) holder.getView(R.id.root)).quickClose();
        if (!(ToolStatus.inTest.name().equalsIgnoreCase(status)) && (DateUtils.compareDate(currentTime, date, DateUtils.yyyy_MM_dd_HH_mm_ss))) {
            orderView.setBackgroundResource(R.drawable.xs_red_fd6067_background);
            toolName.setTextColor(mContext.getResources().getColor(R.color.xs__fd6067_color));
        } else if (!(ToolStatus.inTest.name().equalsIgnoreCase(status)) && (!TextUtils.isEmpty(date) && DateUtils.compareDate(date, currentTime, DateUtils.yyyy_MM_dd_HH_mm_ss) && DateUtils.getTimeDifferenceDays(currentTime, date) <= 30)) {
            orderView.setBackgroundResource(R.drawable.xs_color_ff9912_background);
            toolName.setTextColor(mContext.getResources().getColor(R.color.xs__ff9912_color));
        } else if ("inTest".equalsIgnoreCase(dbModel.getString("status"))) {
            holder.getView(R.id.txt_stop).setVisibility(View.GONE);
            orderView.setBackgroundResource(R.drawable.xs_color_c0bab9_background);
            toolName.setTextColor(mContext.getResources().getColor(R.color.xs__c0bab9_color));
        } else {
            orderView.setBackgroundResource(R.drawable.xs_color_03b9a0_background);
            toolName.setTextColor(mContext.getResources().getColor(R.color.xs_global_base_color));
        }
        if (!TextUtils.isEmpty(date)) {
            date = DateUtils.getFormatterTime(date, DateUtils.yyyy_MM_dd);
        }
        orderView.setText(String.valueOf(position + 1));
        toolName.setText(TextUtils.isEmpty(dbModel.getString("name")) ? "" : dbModel.getString("name"));
        toolNum.setText(TextUtils.isEmpty(dbModel.getString("num")) ? "编号:" : (("-1".equalsIgnoreCase(dbModel.getString("num"))) ? "编号:" : "编号:" + dbModel.getString("num")));
        toolNextTime.setText(TextUtils.isEmpty(date) ? "" : date);

        if (isSelected) {
            holder.getView(R.id.cb_selected).setVisibility(View.VISIBLE);
            orderView.setVisibility(View.GONE);
        } else {
            holder.getView(R.id.cb_selected).setVisibility(View.GONE);
            orderView.setVisibility(View.VISIBLE);
        }
        (holder.getView(R.id.cb_selected)).setTag(dbModel);
        (holder.getView(R.id.ll_root)).setTag((holder.getView(R.id.cb_selected)));
        ((CheckBox) (holder.getView(R.id.cb_selected))).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DbModel dbModel1 = (DbModel) buttonView.getTag();
                if (!isChecked) {
                    checkMap.remove(dbModel1.getString("id"));
                } else {
                    checkMap.put(dbModel1.getString("id"), isChecked);
                }
            }
        });

        if (checkMap.size() > position && checkMap.get(dbModel.getString("id")) == null) {
            ((CheckBox) (holder.getView(R.id.cb_selected))).setChecked(false);
        }
        if (isSelectedAll) {
            ((CheckBox) (holder.getView(R.id.cb_selected))).setChecked(true);
        } else {
            if (checkMap.containsKey(dbModel.getString("id")) && checkMap.get(dbModel.getString("id")) != null && checkMap.get(dbModel.getString("id")) == true) {
                ((CheckBox) (holder.getView(R.id.cb_selected))).setChecked(true);
            } else {
                ((CheckBox) (holder.getView(R.id.cb_selected))).setChecked(false);
            }
        }
        (holder.getView(R.id.txt_stop)).setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v, dbModel, position);
            }
        });

        (holder.getView(R.id.txt_test)).setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v, dbModel, position);
            }
        });
        (holder.getView(R.id.ll_root)).setOnClickListener(v -> {
            if (null != listener) {
                listener.onClick(v, dbModel, position);
            }
        });
        (holder.getView(R.id.cb_selected)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClick(v, dbModel, position);
                }
            }
        });
        if (!isSwipeLeft) {
            ((SwipeMenuLayout) holder.getView(R.id.root)).setSwipeEnable(false);
        } else {
            ((SwipeMenuLayout) holder.getView(R.id.root)).setSwipeEnable(true);
        }
    }
}
