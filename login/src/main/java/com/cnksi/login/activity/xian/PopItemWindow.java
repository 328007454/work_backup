package com.cnksi.login.activity.xian;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.login.R;

import java.util.List;

/**
 * @author Today(张军)
 * @version v1.0
 * @date 2018/3/27 10:56
 */

public class PopItemWindow extends PopupWindow implements BaseQuickAdapter.OnItemClickListener {
    private Context context;

    public PopItemWindow(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private View rootView;
    private RecyclerView recyclerView;
    private BaseQuickAdapter.OnItemClickListener onItemClickListener;

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.inspe_widget_popitemwindow, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(rootView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        //布局控件初始化与监听设置
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    public PopItemWindow setOnItemClickListener(BaseQuickAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public PopItemWindow setPopWindowWidth(int width) {
        this.setWidth(width);
        this.update();
        return this;
    }

    public PopItemWindow setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        return this;
    }

    public PopItemWindow setDefaultAdapter(List<? extends WidgetItemData> list) {
        DefaultAdapter adapter = new DefaultAdapter(R.layout.inspe_sprinitem_textview, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return this;
    }

    public PopItemWindow setLayoutManager(RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
        return this;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(adapter, view, position);
        }
        dismiss();
    }

    private class DefaultAdapter extends BaseQuickAdapter<WidgetItemData, BaseViewHolder> {
        public DefaultAdapter(int layoutResId, List<? extends WidgetItemData> list) {
            super(layoutResId, (List<WidgetItemData>) list);
        }

        @Override
        protected void convert(BaseViewHolder helper, WidgetItemData item) {
            helper.setImageResource(R.id.icoImg, item.imgIds);
            helper.setText(R.id.itemText, item.desc);
        }
    }

}
