package com.cnksi.workticket.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cnksi.core.view.CustomerDialog;
import com.cnksi.workticket.R;
import com.cnksi.workticket.adapter.TicketFragmentPagerAdapter;
import com.cnksi.workticket.databinding.ActivityTicketDateWorkBinding;
import com.cnksi.workticket.db.WorkTicketDbManager;
import com.cnksi.workticket.enum_ticket.TicketEnum;
import com.cnksi.workticket.fragment.TicketDaliyWorkFragment;
import com.cnksi.workticket.fragment.TicketWorkRecordFragment;
import com.cnksi.workticket.sync.KSyncConfig;

import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mr.K on 2018/4/28.
 */

public class TicketDateHomeActivity extends TicketBaseActivity {

    ActivityTicketDateWorkBinding binding;

    private String[] titleArrays = new String[2];
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<Fragment> mFragmentList;

    private TicketWorkRecordFragment recordFragment;
    private TicketDaliyWorkFragment daliyWorkFragment;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_ticket_date_work;
    }

    @Override
    public void initUI() {
        getIntentVaule();
        binding = (ActivityTicketDateWorkBinding) rootDataBinding;
        binding.includeTitle.ticketTxtAdd.setVisibility(View.VISIBLE);
        titleArrays[1] = TicketEnum.GZJL.value;
        titleArrays[0] = TicketEnum.GZRZ.value;
        initFragments();
        initClick();
    }


    private void initFragments() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<>();
        }

        daliyWorkFragment = new TicketDaliyWorkFragment();
        mFragmentList.add(daliyWorkFragment);

        recordFragment = new TicketWorkRecordFragment();
        mFragmentList.add(recordFragment);

        fragmentPagerAdapter = new TicketFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, Arrays.asList(titleArrays));
        binding.viewpager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewpager);
        setTabStripStyle(binding.tabStrip);
        binding.tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (0 == position) {

                } else if (1 == position) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        binding.viewpager.setOffscreenPageLimit(2);
    }

    @Override
    public void initData() {
        WorkTicketDbManager.getInstance().initDbManager(getApplicationContext());
        CustomerDialog.showProgress(this, "正在同步数据，请确保网络畅通");
        KSyncConfig.getInstance().getKNConfig(getApplicationContext()).downLoad();
    }


    public void initClick() {
        binding.includeTitle.ticketTxtAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, TicketDateWorkActivity.class);
            startActivity(intent);

        });
    }

}
