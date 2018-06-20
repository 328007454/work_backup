package com.cnksi.common.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.R;
import com.cnksi.common.model.Lookup;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.K on 2018/6/8.
 * @decrption 人员列表适配器
 */

public class InspectionTypeAdapter extends BaseQuickAdapter<DbModel, BaseViewHolder> {
    private boolean multipleChoice;
    private List<DbModel> selectModels = new ArrayList<>();

    public InspectionTypeAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, DbModel item) {
        TextView txtType = helper.getView(R.id.txt_name);
        TextView txtDes = helper.getView(R.id.txt_description);
        txtType.setText(item.getString(Lookup.V));
        if (TextUtils.isEmpty(item.getString(Lookup.REMARK))) {
            txtDes.setVisibility(View.GONE);
        } else {
            txtDes.setVisibility(View.VISIBLE);
            txtDes.setText(item.getString(Lookup.REMARK));
        }

        if (multipleChoice) {
            txtDes.setVisibility(View.GONE);
            Drawable leftDrawable;
            if (selectModels.contains(item)) {
                leftDrawable = ContextCompat.getDrawable(mContext, R.drawable.xs_icon_select);
            } else {
                leftDrawable = ContextCompat.getDrawable(mContext, R.drawable.xs_icon_unselect);
            }
            txtType.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        } else {
            txtType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        if (multipleChoice) {
            helper.itemView.setOnClickListener(v -> {
                if (multipleChoice) {
                    if (selectModels.contains(item)) {
                        selectModels.remove(item);
                    } else {
                        selectModels.add(item);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setMultiChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public List<DbModel> getSelectModels() {
        return selectModels;
    }
}

