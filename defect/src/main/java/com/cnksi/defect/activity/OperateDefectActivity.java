package com.cnksi.defect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.ActivityOperateDefectBinding;
import com.cnksi.defect.fragment.EliminateDefectFragment;
import com.cnksi.defect.fragment.TrackDefectFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mr.K  on 2018/5/30.
 * @decrption 操作缺陷界面
 */

public class OperateDefectActivity extends BaseTitleActivity implements ViewPager.OnPageChangeListener {
    private ActivityOperateDefectBinding binding;
    private List<Fragment> fragments = new ArrayList<>();
    private String deviceId;
    private String bdzId;
    private String defecCount;
    private String defectId;
    private int selectFragmentPosition;

    @Override
    protected View getChildContentView() {
        binding = ActivityOperateDefectBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void initUI() {
        getIntentValue();
        setTitleText("缺陷管控");
        deviceId = getIntent().getStringExtra(Device.DEVICEID);
        bdzId = getIntent().getStringExtra(Bdz.BDZID);
        defecCount = getIntent().getStringExtra(Config.DEFECT_COUNT_KEY);
        defectId = getIntent().getStringExtra(DefectRecord.DEFECTID);
        initFragments();
    }

    private void initFragments() {
        Bundle bundle = new Bundle();
        bundle.putString(Device.DEVICEID, deviceId);
        bundle.putString(Bdz.BDZID, bdzId);
        bundle.putString(Config.DEFECT_COUNT_KEY, defecCount);
        bundle.putString(DefectRecord.DEFECTID, defectId);
        TrackDefectFragment trackDefectFragment = new TrackDefectFragment();
        trackDefectFragment.setArguments(bundle);

        fragments.add(trackDefectFragment);

        EliminateDefectFragment eliminateDefectFragment = new EliminateDefectFragment();
        eliminateDefectFragment.setArguments(bundle);
        fragments.add(eliminateDefectFragment);

        String[] titleArray = getResources().getStringArray(R.array.Defect_Indicator);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragments, Arrays.asList(titleArray));
        binding.viewPager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        binding.tabStrip.setOnPageChangeListener(this);
        setPagerTabStripValue(binding.tabStrip);
        binding.viewPager.setOffscreenPageLimit(2);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectFragmentPosition = position;
        if (TextUtils.equals(defecCount, Config.SINGLE)) {
            String newDefectId = ((TrackDefectFragment) fragments.get(0)).getNewDefectId();
            if (!TextUtils.isEmpty(newDefectId)&&position==1) {
                ((EliminateDefectFragment) fragments.get(1)).setTrackDefectNewId(newDefectId);
            }

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragments.get(selectFragmentPosition).onActivityResult(requestCode, resultCode, data);
    }
}
