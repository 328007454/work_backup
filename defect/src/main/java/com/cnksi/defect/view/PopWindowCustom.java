package com.cnksi.defect.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.defect.R;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * Created by Mr.K on 2018/5/25.
 */

public class PopWindowCustom extends PopupWindow {


    public static class PopWindowBuilder<T> {
        private RecyclerView recyclerView;
        private PopWindowCustom popWindowCustom;
        private Activity context;
        private List<Object> datas;
        private String key;
        private BaseQuickAdapter.OnItemClickListener itemClickListener;
        private View dropView;
        private boolean outSideCancelable;

        public PopWindowBuilder(Activity context) {
            this.context = context;
            popWindowCustom = new PopWindowCustom();
            popWindowCustom.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popWindowCustom.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.transparent);
            popWindowCustom.setBackgroundDrawable(drawable);
            popWindowCustom.setOnDismissListener(() -> setBackgroundAlpha(1f));
        }

        public PopWindowBuilder setWidth(int width) {
            popWindowCustom.setWidth(width);
            return this;
        }

        public PopWindowBuilder setHeight(int height) {
            popWindowCustom.setHeight(height);
            return this;
        }

        public PopWindowBuilder setRecyclerView(RecyclerView view) {
            this.recyclerView = view;
            return this;
        }

        public PopWindowBuilder setList(List objects) {
            this.datas = objects;
            return this;
        }

        public PopWindowBuilder setDbmodelKey(String key) {
            this.key = key;
            return this;
        }

        public PopWindowBuilder setRecyclerAdater(BaseQuickAdapter adater) {

            return this;
        }

        public PopWindowBuilder setItemClickListener(BaseQuickAdapter.OnItemClickListener onItemClickListener) {
            this.itemClickListener = onItemClickListener;
            return this;
        }

        public PopWindowBuilder setDropDownOfView(View downOfView) {
            this.dropView = downOfView;
            return this;
        }

        public PopWindowBuilder setOutSideCancelable(boolean outSideCancelable) {
            this.outSideCancelable = outSideCancelable;
            return this;
        }

        /**
         * 设置添加屏幕的背景透明度
         *
         * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
         */
        public PopWindowBuilder setBackgroundAlpha(float bgAlpha) {
            WindowManager.LayoutParams lp = context.getWindow()
                    .getAttributes();
            lp.alpha = bgAlpha;
            context.getWindow().setAttributes(lp);
            return this;
        }


        public void showPopWindow() {

            return;
        }


        public void showDefault() {
            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setPadding(30, 0, 30, 0);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            DataAdapter dataAdapter = new DataAdapter(R.layout.textview_item, datas);
            dataAdapter.setDbmodelKey(key);
            dataAdapter.setItemClickListener(itemClickListener);
            dataAdapter.setPopWindow(popWindowCustom);
            recyclerView.setAdapter(dataAdapter);
            popWindowCustom.setContentView(recyclerView);
            popWindowCustom.setOutsideTouchable(outSideCancelable);
            popWindowCustom.showAsDropDown(dropView);
        }

        public void showAsDropDown(int x, int y) {
            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setPadding(30, 0, 30, 0);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setBackground(ContextCompat.getDrawable(context,R.drawable.xs_item));
            DataAdapter dataAdapter = new DataAdapter(R.layout.textview_item, datas);
            dataAdapter.setDbmodelKey(key);
            dataAdapter.setItemClickListener(itemClickListener);
            dataAdapter.setPopWindow(popWindowCustom);
            recyclerView.setAdapter(dataAdapter);
            popWindowCustom.setContentView(recyclerView);
            popWindowCustom.setOutsideTouchable(outSideCancelable);
            popWindowCustom.showAsDropDown(dropView, x, y);
        }



    }

    /**
     * 默认适配器
     */
    private static class DataAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

        private String key;
        private BaseQuickAdapter.OnItemClickListener itemClickListener;
        private PopWindowCustom popWindow;

        public void setDbmodelKey(String key) {
            this.key = key;

        }

        public DataAdapter(int layoutResId, @Nullable List<Object> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            if (item instanceof String) {
                helper.setText(R.id.txt_name, (String) item);
            } else if (item instanceof DbModel && !TextUtils.isEmpty(key)) {
                helper.setText(R.id.txt_name, ((DbModel) item).getString(key));
            }

            helper.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(null, v, helper.getAdapterPosition());
                    popWindow.dismiss();
                }
            });
        }

        public void setItemClickListener(OnItemClickListener onItemClickListener) {
            this.itemClickListener = onItemClickListener;
        }

        public void setPopWindow(PopWindowCustom popWindow) {
            this.popWindow = popWindow;
        }
    }
}
