package com.cnksi.defect.fragment;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.FragmentTrackDefectBinding;

/**
 * @author Mr.K  on 2018/5/30.
 * @decrption 跟踪缺陷
 */

public class TrackDefectFragment extends BaseCoreFragment {

    private FragmentTrackDefectBinding binding;

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_track_defect;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {
        binding = (FragmentTrackDefectBinding) fragmentDataBinding;
    }
}
