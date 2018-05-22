package com.cnksi.sjjc.view.gztz;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.sjjc.activity.gztz.BHDZJLActivity;
import com.cnksi.sjjc.bean.gztz.BhyjBean;
import com.cnksi.sjjc.databinding.GztzItemBhdzjlYjlxBinding;
import com.cnksi.sjjc.inter.SimpleTextWatcher;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/8 21:42
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BhdzjlYjGroup {
    GztzItemBhdzjlYjlxBinding binding;
    BhyjBean bhyjBean;

    public BhdzjlYjGroup(BHDZJLActivity activity, BhdzjlGroup group) {
        binding = GztzItemBhdzjlYjlxBinding.inflate(activity.getLayoutInflater(), group.binding.yjlx, true);
        LinearLayout parentLayout = (LinearLayout) binding.getRoot().getParent();
        if (parentLayout.getChildCount() == 1) {
            binding.add.setVisibility(View.VISIBLE);
            binding.delete.setVisibility(View.GONE);
        } else {
            binding.add.setVisibility(View.GONE);
            binding.delete.setVisibility(View.VISIBLE);
        }
        binding.add.setOnClickListener(view -> {
            group.addOtherYJLX();
        });
        binding.delete.setOnClickListener(view -> {
            group.removeView(this);
        });
        binding.bhdzsj.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                group.rebulidStr();
            }
        });
        binding.bhyjlx.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                group.rebulidStr();
            }
        });
    }

    public void setBhyjBean(BhyjBean bhyjBean) {
        this.bhyjBean = bhyjBean;
        binding.bhyjlx.setText(bhyjBean.bhyjlx);
        binding.bhdzsj.setText(bhyjBean.bhdzsj);
    }

    public BhyjBean getBhyjBean() {

        String lx = binding.bhyjlx.getText().toString();
        String sj = binding.bhdzsj.getText().toString();
        if (TextUtils.isEmpty(lx) && TextUtils.isEmpty(sj)) {
            return null;
        }
        if (bhyjBean == null) {
            bhyjBean = new BhyjBean();
        }
        bhyjBean.bhdzsj = sj;
        bhyjBean.bhyjlx = lx;
        return bhyjBean;
    }

    public View getRoot() {
        return binding.getRoot();
    }


    @Override
    public String toString() {
        String a = binding.bhdzsj.getText().toString();
        String b = binding.bhyjlx.getText().toString();
        if (TextUtils.isEmpty(a)) {
            return "";
        } else {
            return a + "ms" + b + ",";
        }
    }
}
