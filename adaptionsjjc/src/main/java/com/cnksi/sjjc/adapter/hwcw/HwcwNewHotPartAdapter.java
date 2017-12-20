package com.cnksi.sjjc.adapter.hwcw;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.cnksi.sjjc.adapter.BaseRecyclerDataBindingAdapter;
import com.cnksi.sjjc.adapter.holder.ItemHolder;
import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;
import com.cnksi.sjjc.databinding.ItemHotDeviceHwcwBinding;
import com.cnksi.sjjc.databinding.ItemHwcwHotPartBinding;
import com.cnksi.sjjc.util.GsonUtil;
import com.cnksi.sjjc.util.StringUtils;

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.List;

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
        hotPartBinding.txtHotDeviceName.setText(item.deviceName);
        if (!TextUtils.isEmpty(item.ratedCurrent)) {
            infor = "额定电流：" + item.ratedCurrent;
        }
        if (!TextUtils.isEmpty(item.fhdl)) {
            infor = TextUtils.isEmpty(infor) ? "" : (infor + "\n" + "负荷电流：" + item.fhdl);
        }
        if (!TextUtils.isEmpty(item.hotPart)) {
            HwcwHotPart hotParts = (HwcwHotPart) GsonUtil.resolveJson(item.hotPart);
            for (HwcwHotPart.Result result : hotParts.result) {
                if (!TextUtils.isEmpty(infor) || i >= 1) {
                    infor += "\n";
                }
                infor = infor + "发热部位名称：" + result.bw_name + "\n" + "温度：" + result.wd;
                i++;
            }
        }
        hotPartBinding.txtHotPartInfomation.setText(infor);
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
