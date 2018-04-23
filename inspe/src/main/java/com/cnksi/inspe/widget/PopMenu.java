package com.cnksi.inspe.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;

import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/23 13:12
 */
public class PopMenu extends PopupWindow implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    private Context context;

    public PopMenu(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private View rootView;
    private RecyclerView recyclerView;
    private Button okBtn, cancleBtn;
    private View.OnClickListener okListener, cancleListener;

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.widget_popmenuwindow, null);
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
        ColorDrawable dw = new ColorDrawable(-00000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        //布局控件初始化与监听设置
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        okBtn = (Button) rootView.findViewById(R.id.okBtn);
        cancleBtn = (Button) rootView.findViewById(R.id.cancleBtn);
        okBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        okBtn.setVisibility(View.GONE);
        cancleBtn.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    public PopMenu setOnItemClickListener(BaseQuickAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public PopMenu setPopWindowWidth(int width) {
        this.setWidth(width);
        this.update();
        return this;
    }

    public PopMenu setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        return this;
    }


    public PopMenu setListAdapter(List<String> list) {
        PopMenu.ListAdapter adapter = new PopMenu.ListAdapter(R.layout.inspe_popmenu_textview, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return this;
    }

    public PopMenu setLayoutManager(RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
        return this;
    }


    public PopMenu setOkListener(String okTxt, View.OnClickListener listener) {
        okBtn.setText(okTxt);
        okListener = listener;
        okBtn.setVisibility(View.VISIBLE);
        return this;
    }

    public PopMenu setCancleListener(String cancleTxt, View.OnClickListener listener) {
        cancleBtn.setText(cancleTxt);
        cancleListener = listener;
        cancleBtn.setVisibility(View.VISIBLE);
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


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.okBtn) {
            if (okListener != null) {
                okListener.onClick(view);
            }
            dismiss();
        } else if (view.getId() == R.id.cancleBtn) {
            if (cancleListener != null) {
                cancleListener.onClick(view);
            }
            dismiss();
        }
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

    public void show(View v) {
        showAtLocation(v, Gravity.FILL, 0, 0);
    }
}
