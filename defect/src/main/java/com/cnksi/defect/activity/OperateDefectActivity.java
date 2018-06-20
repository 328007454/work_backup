package com.cnksi.defect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
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
    private String defectCount;
    private String defectId;
    private int selectFragmentPosition;
    private boolean standardSwitchDefectId;
    private String switchStandardId;

    @Override
    protected View getChildContentView() {
        binding = ActivityOperateDefectBinding.inflate(getLayoutInflater());
        mTitleBinding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        return binding.getRoot();
    }

    @Override
    public void initUI() {
        getIntentValue();
        setTitleText("缺陷管控");
        deviceId = getIntent().getStringExtra(Device.DEVICEID);
        bdzId = getIntent().getStringExtra(Bdz.BDZID);
        defectCount = getIntent().getStringExtra(Config.DEFECT_COUNT_KEY);
        defectId = getIntent().getStringExtra(DefectRecord.DEFECTID);
        standardSwitchDefectId = getIntent().getBooleanExtra(Config.SWITCH_DEFECT, false);
        currentReportId = getIntent().getStringExtra(Config.CURRENT_REPORT_ID);
        switchStandardId = getIntent().getStringExtra(Config.CURRENT_STANDARD_ID);
        initFragments();
    }

    private void initFragments() {
        Bundle bundle = new Bundle();
        bundle.putString(Device.DEVICEID, deviceId);
        bundle.putString(Bdz.BDZID, bdzId);
        bundle.putString(Config.DEFECT_COUNT_KEY, defectCount);
        bundle.putString(DefectRecord.DEFECTID, defectId);
        bundle.putBoolean(Config.SWITCH_DEFECT, standardSwitchDefectId);
        bundle.putString(Config.CURRENT_REPORT_ID, currentReportId);
        bundle.putString(Config.CURRENT_STANDARD_ID, switchStandardId);
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
        String newDefectId = ((TrackDefectFragment) fragments.get(0)).getNewDefectId();
        if (!TextUtils.isEmpty(newDefectId) && position == 1) {
            ((EliminateDefectFragment) fragments.get(1)).setTrackDefectNewId(newDefectId);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragments.get(selectFragmentPosition).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            onBackPressed();
        }
        return false;
    }
}
