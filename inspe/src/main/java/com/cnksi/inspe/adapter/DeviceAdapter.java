package com.cnksi.inspe.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.R;
import com.cnksi.inspe.entity.device.DeviceItem;
import com.cnksi.inspe.entity.device.SpaceItem;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * Created by Mr.K on 2018/4/9.
 */

public class DeviceAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int SPACE_ITEM = 1;
    public final static int DEVICE_ITEM = 2;
    private OnItemClickListerner onItemClickListerner;

    public interface OnItemClickListerner {
        void OnItemClickListen(View v, Object item, int position);
    }

    public void setOnItemClickListener(OnItemClickListerner onItemClickListener) {
        this.onItemClickListerner = onItemClickListener;
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DeviceAdapter(Activity context, List<MultiItemEntity> data) {
        super(data);
        addItemType(SPACE_ITEM, R.layout.inspe_device_group_item);
        addItemType(DEVICE_ITEM, R.layout.inspe_device_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SPACE_ITEM:
                SpaceItem spaceItem = (SpaceItem) item;
                DbModel spModel = spaceItem.spacing;
                helper.setText(R.id.tv_group_item, spModel.getString("sname"));
                ImageView imgOpen = helper.getView(R.id.img_open);
                // 间隔展开
                imgOpen.setImageResource(spaceItem.isExpanded() ? R.drawable.inspe_device_shrink : R.drawable.inspe_device_open);
                helper.itemView.setOnClickListener(view -> {
                    if (spaceItem.isExpanded()) {
                        collapse(helper.getAdapterPosition(), true);
                    } else {
                        expand(helper.getAdapterPosition(), true);
                    }
                });



                break;
            case DEVICE_ITEM:
                DeviceItem deviceItem = (DeviceItem) item;
                DbModel dvModle = deviceItem.dbModel;
                helper.setText(R.id.tv_device_name, dvModle.getString("dnameshort"));
                helper.itemView.setOnClickListener(view -> {
                    if (null != onItemClickListerner) {
                        onItemClickListerner.OnItemClickListen(view, deviceItem, helper.getAdapterPosition());
                    }
                });
                break;
        }
    }

}
