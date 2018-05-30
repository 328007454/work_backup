package com.cnksi.defect.fragment;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.FragmentEliminateDefectBinding;

/**
 * 消除缺陷
 *
 * @author Mr.K  on 2018/5/30.
 */

public class EliminateDefectFragment extends BaseCoreFragment {

    private FragmentEliminateDefectBinding binding;

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_eliminate_defect;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {
        binding = (FragmentEliminateDefectBinding) fragmentDataBinding;
    }
}
