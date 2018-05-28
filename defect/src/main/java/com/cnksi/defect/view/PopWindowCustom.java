package com.cnksi.defect.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.defect.R;

import java.util.List;

/**
 * Created by Mr.K on 2018/5/25.
 */

public class PopWindowCustom extends PopupWindow {


    public static class PopWindowBuilder<T> {

        public interface CustomStringInter<T> {
            /**
             * 返回默认的需要指定的值
             *
             * @param t 相关泛型
             * @return 实体类具体字段
             */
            String convertString(T t);
        }

        private CustomStringInter<T> customStringInter = t -> "";
        private RecyclerView recyclerView;
        private PopWindowCustom popWindowCustom;
        private Activity context;
        private List<T> datas;
        private BaseQuickAdapter.OnItemClickListener itemClickListener;
        private View dropView;
        private boolean outSideCancelable;


        public PopWindowBuilder(Activity context) {
            this.context = context;
            popWindowCustom = new PopWindowCustom();
            popWindowCustom.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popWindowCustom.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.transparent);
            popWindowCustom.setBackgroundDrawable(drawable);
            popWindowCustom.setOnDismissListener(() -> setBackgroundAlpha(1f));
        }

        public PopWindowBuilder<T> setWidth(int width) {
            popWindowCustom.setWidth(width);
            return this;
        }

        public PopWindowBuilder<T> setPopWindowBuilder(CustomStringInter<T> customStringInter) {
            this.customStringInter = customStringInter;
            return this;
        }

        public PopWindowBuilder<T> setHeight(int height) {
            popWindowCustom.setHeight(height);
            return this;
        }

        public PopWindowBuilder<T> setRecyclerView(RecyclerView view) {
            this.recyclerView = view;
            return this;
        }

        public PopWindowBuilder<T> setList(List<T> objects) {
            this.datas = objects;
            return this;
        }

        public PopWindowBuilder<T> setRecyclerAdater(BaseQuickAdapter adater) {

            return this;
        }

        public PopWindowBuilder<T> setItemClickListener(BaseQuickAdapter.OnItemClickListener onItemClickListener) {
            this.itemClickListener = onItemClickListener;
            return this;
        }

        public PopWindowBuilder<T> setDropDownOfView(View downOfView) {
            this.dropView = downOfView;
            return this;
        }

        public PopWindowBuilder<T> setOutSideCancelable(boolean outSideCancelable) {
            this.outSideCancelable = outSideCancelable;
            return this;
        }

        /**
         * 设置添加屏幕的背景透明度
         *
         * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
         */
        public PopWindowBuilder<T> setBackgroundAlpha(float bgAlpha) {
            WindowManager.LayoutParams lp = context.getWindow()
                    .getAttributes();
            lp.alpha = bgAlpha;
            context.getWindow().setAttributes(lp);
            return this;
        }


        public void showPopWindow() {

            return;
        }

        public void showAsDropDown(int x, int y) {
            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setPadding(30, 0, 30, 0);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setBackground(ContextCompat.getDrawable(context, R.drawable.xs_item));
            DataAdapter<T> dataAdapter = new DataAdapter<T>(R.layout.textview_item, datas);
            dataAdapter.setPopWindowBuilder(customStringInter);
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
    private static class DataAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

        private BaseQuickAdapter.OnItemClickListener itemClickListener;
        private PopWindowCustom popWindow;

        private PopWindowBuilder.CustomStringInter<T> customStringInter;

        void setPopWindowBuilder(PopWindowBuilder.CustomStringInter<T> customStringInter) {
            this.customStringInter = customStringInter;
        }

        DataAdapter(int layoutResId, @Nullable List<T> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            if (customStringInter == null) {
                throw new RuntimeException("请调用接口PopWindowBuilder.CustomStringInter<T>");
            }
            helper.setText(R.id.txt_name, customStringInter.convertString(item));

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

        void setPopWindow(PopWindowCustom popWindow) {
            this.popWindow = popWindow;
        }
    }
}
