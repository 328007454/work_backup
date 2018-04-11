package com.cnksi.bdzinspection.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsItemMultipleContentBinding;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.zzht.Zzht;
import com.cnksi.bdzinspection.model.zzht.ZzhtResult;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.table.DbModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kkk on 2018/1/31.
 */

public class MultipleBackAdapter extends BaseQuickAdapter<DbModel, MultipleBackAdapter.ItemHolder> {

    private ItemClickListener mClickListener;
    private Map<String, List<DbModel>> zzhtInfoMap = new HashMap<>();

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public MultipleBackAdapter(int layoutResId, @Nullable List<DbModel> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(ItemHolder helper, final DbModel item) {
        ((TextView) helper.getView(R.id.txt_multiple_title)).setText(item.getString("xsbw"));
        ((TextView) helper.getView(R.id.txt_multiple_num)).setText((getData().indexOf(item) + 1) + "");
        LinearLayout linearLayout = helper.getView(R.id.txt_item_multiple_content);
        final ImageView imageView = helper.getView(R.id.check);
        if (!TextUtils.isEmpty(item.getString(ZzhtResult.CONFIRM_TIME))) {
            imageView.setBackground(ContextCompat.getDrawable(imageView.getContext(), R.drawable.xs_green_circle_background));
        } else {
            imageView.setBackground(ContextCompat.getDrawable(imageView.getContext(), R.drawable.xs_ic_confirm_none));
        }
        linearLayout.removeAllViews();
        List<DbModel> inforModels = zzhtInfoMap.get(item.getString(Zzht.ID));
        if (!inforModels.isEmpty()) {
            for (DbModel model : inforModels) {
                XsItemMultipleContentBinding contentBinding = XsItemMultipleContentBinding.inflate(LayoutInflater.from(linearLayout.getContext()));
                contentBinding.tvDesc.setText((inforModels.size() == 1 ? "" : (inforModels.indexOf(model) + 1) + "） ") + model.getString(Zzht.DESCRIPTION));
                if ("0".equalsIgnoreCase(model.getString(Zzht.IS_COPY))) {
                    contentBinding.etCopyValues.setVisibility(View.GONE);
                    contentBinding.tvCopyUnit.setVisibility(View.GONE);
                } else {
                    contentBinding.tvCopyUnit.setVisibility(View.VISIBLE);
                    contentBinding.etCopyValues.setVisibility(View.VISIBLE);
                    contentBinding.tvCopyUnit.setText("(" + model.getString(Zzht.UNIT) + ")");
                }
                linearLayout.addView(contentBinding.getRoot());
                contentBinding.etCopyValues.setText(model.getString(ZzhtResult.COPY_VALUE));
                contentBinding.etCopyValues.addTextChangedListener(new Watcher(model));
            }
        }
        imageView.setOnClickListener(view -> {
            if (!checkItemCopy(item.getString(Zzht.ID))) {
                CToast.showShort("您还有输入框没有填写！");
                return;
            }
            if (mClickListener != null) {
                mClickListener.onItemClick(view, item, getParentPosition(item));
                view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.xs_green_circle_background));
                notifyItemChanged(getParentPosition(item));
                item.add(ZzhtResult.CONFIRM_TIME, DateUtils.getCurrentLongTime());
            }
        });

    }

    private boolean checkItemCopy(String zzhtid) {
        List<DbModel> inforModels = zzhtInfoMap.get(zzhtid);
        if (inforModels != null && !inforModels.isEmpty()) {
            for (DbModel model : inforModels) {
                if (!TextUtils.equals("0",model.getString(ZzhtResult.IS_COPY))&&TextUtils.isEmpty(model.getString(ZzhtResult.COPY_VALUE))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        if (binding == null) {
            return super.getItemView(layoutResId, parent);
        }
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding);
        return super.getItemView(layoutResId, parent);
    }

    public void setInforMap(Map<String, List<DbModel>> map) {
        this.zzhtInfoMap = map;
        notifyDataSetChanged();
    }

    public static class ItemHolder extends BaseViewHolder {

        public ItemHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }

    public class Watcher implements TextWatcher {
        DbModel mModel;

        public Watcher(DbModel model) {
            this.mModel = model;
        }

        @Override
        public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            mModel.add(ZzhtResult.COPY_VALUE, s);
        }
    }
}
