package com.cnksi.inspe.widget;

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
import com.cnksi.inspe.R;
import com.cnksi.inspe.db.entity.InspeScoreEntity;

import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/27 10:56
 */

public class PopItemWindow extends PopupWindow implements BaseQuickAdapter.OnItemClickListener {

    public PopItemWindow(Context context) {
        super(context);
        init(context);
    }

    private View rootView;
    private RecyclerView recyclerView;

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.widget_popitemwindow, null);
//        int h = context.getWindowManager().getDefaultDisplay().getHeight();
//        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(rootView);
        // 设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(w / 2 + 50);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果，设置动画，一会会讲解
//        this.setAnimationStyle(R.style.AnimationPreview);
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


    public PopItemWindow setListAdapter(List<String> list)

    {
        ListAdapter adapter = new ListAdapter(R.layout.inspe_sprinitem_textview, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return this;
    }


    public PopItemWindow setInspeScoreAdapter(List<InspeScoreEntity> list) {
        InspeScoreAdapter adapter = new InspeScoreAdapter(R.layout.inspe_sprinitem_textview, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        return this;
    }

    public PopItemWindow setLayoutManager(RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
        return this;
    }

    private BaseQuickAdapter.OnItemClickListener onItemClickListener;

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(adapter, view, position);
        }
        dismiss();
    }

    public class ListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public ListAdapter(int layoutResId, List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.itemText, item);
        }
    }

    public class InspeScoreAdapter extends BaseQuickAdapter<InspeScoreEntity, BaseViewHolder> {
        public InspeScoreAdapter(int layoutResId, List<InspeScoreEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, InspeScoreEntity item) {
            helper.setText(R.id.itemText, item.content);
        }
    }

}
