package com.cnksi.inspe.adapter;

import android.bluetooth.BluetoothClass;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * @description 设备精益化评价大类adapter
 * Created by Mr.K on 2018/4/9.
 */

public class DeviceStandardTypeAdapter extends BaseQuickAdapter<PlusteRuleEntity, BaseViewHolder> {

    private OnItemClickListener onItemClickListener;
    private int clickItemPosition;

    public interface OnItemClickListener {
        void onItemClick(View view, PlusteRuleEntity item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public DeviceStandardTypeAdapter(int layoutResId, @Nullable List<PlusteRuleEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlusteRuleEntity item) {
        TextView typeName = helper.getView(R.id.txt_standard_type_name);
        helper.setText(R.id.txt_standard_type_name, item.getName().replaceAll(" ", ""));//item.getString("name"));
        if (helper.getAdapterPosition() == clickItemPosition) {
            typeName.setSelected(true);
            typeName.setTextColor(mContext.getResources().getColor(R.color.color_05c8b8));
        } else {
            typeName.setSelected(true);
            typeName.setTextColor(mContext.getResources().getColor(R.color.btn_disable));
        }

        helper.itemView.setOnClickListener(view -> {
            if (null != onItemClickListener) {
                clickItemPosition = helper.getAdapterPosition();
                onItemClickListener.onItemClick(view, item, helper.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }
}
