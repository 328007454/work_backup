package com.cnksi.defect.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.defect.R;
import com.cnksi.defect.bean.DefectType;
import com.cnksi.defect.bean.DefectTypeChild;

import java.util.List;

/**
 * @author Mr.K  on 2018/6/4.
 * @decrption 缺陷来源
 */

public class DefectOriginAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    /**
     * 大类
     */
    public static final int PARENT_ITEM = 0;
    /**
     * 小
     */
    public static final int CHILD_ITEM = 1;

    private OnItemClickListener onItemClickListener;

    public void setItemOnClick(OnItemClickListener onClick) {
        this.onItemClickListener = onClick;
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DefectOriginAdapter(List data) {
        super(data);
        addItemType(PARENT_ITEM, R.layout.layout_defect_parent);
        addItemType(CHILD_ITEM, R.layout.layout_defect_child);
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case PARENT_ITEM:
                DefectType defectType = (DefectType) item;
                helper.setText(R.id.tv_group_item, TextUtils.isEmpty(defectType.txtType) ? "" : defectType.txtType);
                break;
            case CHILD_ITEM:
                DefectTypeChild child = (DefectTypeChild) item;
                TextView textView = helper.getView(R.id.txt_name);
                ImageView imageView = helper.getView(R.id.img);
                textView.setText(TextUtils.isEmpty(child.defectContent) ? "" : child.defectContent);
                textView.setTag("txt");
                textView.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(null, v, helper.getAdapterPosition());
                    }
                });
                imageView.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(null, v, helper.getAdapterPosition());
                    }
                });
                break;
            default:
                break;
        }
    }
}
