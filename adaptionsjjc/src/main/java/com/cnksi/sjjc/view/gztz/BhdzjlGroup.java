package com.cnksi.sjjc.view.gztz;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.activity.gztz.BHDZJLActivity;
import com.cnksi.sjjc.databinding.GztzItemBhdzjlSbBinding;
import com.cnksi.sjjc.inter.SimpleTextWatcher;

import org.xutils.common.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/8 21:34
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BhdzjlGroup {
    public GztzItemBhdzjlSbBinding binding;
    BHDZJLActivity activity;
    List<BhdzjlYjGroup> lists = new ArrayList<>();
    private onSelctListener listener;

    public BhdzjlGroup(BHDZJLActivity activity, ViewGroup group) {
        this.activity = activity;
        binding = GztzItemBhdzjlSbBinding.inflate(activity.getLayoutInflater(), group, true);
        addOtherYJLX();
        if (group.getChildCount() == 1) {
            binding.sbmc.setVisible(View.VISIBLE, View.GONE);
        } else {
            binding.sbmc.setVisible(View.GONE, View.VISIBLE);
        }
        binding.sbmc.setSelectOnClickListener(view -> listener.onSelectListener(this, false));
        binding.bhsbmc.setSelectOnClickListener(v -> listener.onSelectListener(this, true));
        binding.sbmc.getAddButton().setOnClickListener(view -> {
            activity.addOtherDevice();
        });

        binding.sbmc.getDeleteButton().setOnClickListener(view -> {
            activity.removeView(this);
        });
        binding.bhqdsj.tvValue.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                rebulidStr();
            }
        });
    }

    public void addOtherYJLX() {
        lists.add(new BhdzjlYjGroup(activity, this));
    }

    public void setListener(onSelctListener listener) {
        this.listener = listener;
    }

    public void removeView(BhdzjlYjGroup group) {
        lists.remove(group);
        binding.yjlx.removeView(group.getRoot());
    }

    public View getRoot() {
        return binding.getRoot();
    }

    public void setDeviceSelectValue(KeyValue value) {
        binding.sbmc.setKeyValue(value);
    }

    public void setBHDeviceSelectValue(KeyValue value) {
        binding.bhsbmc.setKeyValue(value);
    }

    public interface onSelctListener {
        void onSelectListener(BhdzjlGroup group, boolean isBhsb);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        String bhxh = binding.bhsbmc.getValueStr();
        if (!TextUtils.isEmpty(bhxh)) {
            builder.append(bhxh).append("保护").append(StringUtils.BlankToDefault(binding.bhqdsj.getValueStr(), "  "))
                    .append("启动保护,");
            for (BhdzjlYjGroup list : lists) {
                builder.append(list.toString());
            }
            return builder.toString();
        }
        return "";
    }

    public void rebulidStr() {
        activity.rebuildStr();
    }
}
