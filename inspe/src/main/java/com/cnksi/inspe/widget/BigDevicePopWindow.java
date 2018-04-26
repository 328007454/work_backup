package com.cnksi.inspe.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.SpaceItemAdapter;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * Created by Mr.K on 2018/4/18.
 */

public class BigDevicePopWindow extends PopupWindow {

    private Context context;
    private SpaceItemAdapter bigDeviceAdapter;
    /**
     * 大类id
     */
    private String deviceBigId;
    private List<String> bigTypeNames;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View view,Object item,int position);
    }

    public void setOnItemClickListtener(OnItemClickListener onItemClickListtener){
        this.onItemClickListener = onItemClickListtener;
    }

    public BigDevicePopWindow(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private View rootView;
    private RecyclerView recyclerView;



    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.widget_popitemwindow, null);
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
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        //布局控件初始化与监听设置
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
    }

    public BigDevicePopWindow setPopWindowWidth(int width) {
        this.setWidth(width);
        this.update();
        return this;
    }

    public void setAdapterData(List<DbModel> models) {
        if (models != null && !models.isEmpty()) {
            bigDeviceAdapter = new SpaceItemAdapter(R.layout.inspe_device_item, models);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            recyclerView.setAdapter(bigDeviceAdapter);
            bigDeviceAdapter.setOnItemClickListener((adapter1, view, position) -> {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(view,models.get(position),position);
                }

            });
        }
    }
}
