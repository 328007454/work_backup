package com.cnksi.sjjc.adapter.hwcw;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.cnksi.sjjc.adapter.BaseRecyclerDataBindingAdapter;
import com.cnksi.sjjc.adapter.holder.ItemHolder;
import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;
import com.cnksi.sjjc.databinding.ItemHotpartBinding;
import com.cnksi.sjjc.databinding.ItemHwcwHotPartBinding;
import com.cnksi.sjjc.util.GsonUtil;

import java.util.Collection;

/**
 * @author on 2017/12/12.
 */

public class HwcwNewHotPartAdapter extends BaseRecyclerDataBindingAdapter<HwcwLocation> {
    private int currentClickPosition;
    private boolean isEtting;
    private OnLongItemListener mLongItemListener;

    public interface OnLongItemListener {
        void onLongItemClick(View v, Object data, int position);
    }

    public HwcwNewHotPartAdapter(RecyclerView v, Collection datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
    }

    public void setOnLongItemClickListener(OnLongItemListener onLongItemListener) {
        this.mLongItemListener = onLongItemListener;
    }

    @Override
    public void convert(ItemHolder holder, final HwcwLocation item, final int position, boolean isScrolling) {
        String infor = "";
        int i = 0;
        ItemHwcwHotPartBinding hotPartBinding = (ItemHwcwHotPartBinding) holder.getDataBinding();
        hotPartBinding.etEtting.setVisibility(View.GONE);
        hotPartBinding.llHotContainer.removeAllViews();
        hotPartBinding.txtHotDeviceName.setText(item.deviceName);
        hotPartBinding.txtLoadElectricityValue.setText(item.fhdl + "(A)");
        hotPartBinding.txtElectrcityValue.setText(item.ratedCurrent + "(A)");
        if (!TextUtils.isEmpty(item.hotPart)) {
            HwcwHotPart hotParts = (HwcwHotPart) GsonUtil.resolveJson(item.hotPart);
            ItemHotpartBinding partBinding = null;
            for (HwcwHotPart.Result result : hotParts.result) {
                if (i % 2 == 0) {
                    partBinding = ItemHotpartBinding.inflate(LayoutInflater.from(mContext));
                    partBinding.txtHotPartTitle.setText(result.bw_name+":");
                    partBinding.txtElectrcityValue.setText(result.wd + "(℃)");
                    hotPartBinding.llHotContainer.addView(partBinding.getRoot());
                } else {
                    partBinding.txtLoadElectricity1.setText(result.bw_name+":");
                    partBinding.txtLoadElectricityValue1.setText(result.wd + "(℃)");
                }
                i++;
            }
        }
        if (isEtting == true && position == currentClickPosition) {
            hotPartBinding.etEtting.setVisibility(View.VISIBLE);
        } else {
            hotPartBinding.etEtting.setVisibility(View.GONE);
        }
        hotPartBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mLongItemListener != null) {
                    mLongItemListener.onLongItemClick(view, item, position);
                }
                return true;
            }
        });
    }


    public void setCurrentClickPosition(boolean isEtting, int position) {
        this.currentClickPosition = position;
        this.isEtting = isEtting;
        notifyDataSetChanged();
    }

}
