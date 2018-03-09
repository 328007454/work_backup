package com.cnksi.sjjc.view.gztz;

import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.sjjc.activity.gztz.BHDZJLActivity;
import com.cnksi.sjjc.databinding.GztzItemBhdzjlYjlxBinding;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/8 21:42
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BhdzjlYjGroup {
    GztzItemBhdzjlYjlxBinding binding;

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
    }

    public View getRoot(){
        return binding.getRoot();
    }
}
