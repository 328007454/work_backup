package com.cnksi.bdzinspection.ywyth;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.daoservice.LookupService;
import com.cnksi.bdzinspection.databinding.XsActivityYwDeviceListBinding;
import com.cnksi.bdzinspection.fragment.YunweiTypeListFragment;
import com.cnksi.common.Config;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.model.Lookup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wastrel
 * @date 2016-3-24 运维一体化设备列表
 */
public class YWDeviceListActivity extends BaseActivity {

    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private List<Lookup> lookups = null;
    private XsActivityYwDeviceListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_yw_device_list);
        initialUI();
        initialData();
        initOncLick();
    }


    private void initialUI() {
        getIntentValue();
        binding.tvTitle.setText(R.string.xs_yw_function_name);
        binding.btnStartInspection.setVisibility(View.GONE);
    }

    private void initialData() {
        // 从Lookup表中查询电压等级
        lookups = LookupService.getInstance().findLookupByType(LookUpType.proType.name());
        initFragmentList();
    }

    /**
     * 初始化
     */
    private void initFragmentList() {
        List<String> titleArray = new ArrayList<String>();
        int count = lookups.size();
        for (int i = 0; i < count; i++) {
            YunweiTypeListFragment mFragment = new YunweiTypeListFragment();
            Bundle args = new Bundle();
            args.putString(Config.CURRENT_FUNCTION_MODEL, lookups.get(i).id);
            mFragment.setArguments(args);
            mFragmentList.add(mFragment);
            titleArray.add(lookups.get(i).v);
        }
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, titleArray);
        binding.viewPager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        setPagerTabStripValue(binding.tabStrip);
        binding.viewPager.setOffscreenPageLimit(count);
    }
    
    private void initOncLick() {
        binding.ibtnCancel.setOnClickListener(view -> YWDeviceListActivity.this.onBackPressed());
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
