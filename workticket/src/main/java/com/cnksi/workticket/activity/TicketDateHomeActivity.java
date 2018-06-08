package com.cnksi.workticket.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.R;
import com.cnksi.workticket.adapter.TicketFragmentPagerAdapter;
import com.cnksi.workticket.base.TicketBaseActivity;
import com.cnksi.workticket.databinding.ActivityTicketDateWorkBinding;
import com.cnksi.workticket.db.WorkTicketDbManager;
import com.cnksi.workticket.enum_ticket.TicketEnum;
import com.cnksi.workticket.fragment.TicketDailyWorkFragment;
import com.cnksi.workticket.fragment.TicketWorkRecordFragment;
import com.cnksi.workticket.sync.KSyncConfig;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mr.K on 2018/4/28.
 * @decrption 该类是工作预约模块进入的首页，主要展示工作日志和工作记录
 */

public class TicketDateHomeActivity extends TicketBaseActivity {

    private ActivityTicketDateWorkBinding binding;

    /**
     * 引导栏标题
     */
    private String[] titleArrays = new String[2];

    private List<Fragment> mFragmentList;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_ticket_date_work;
    }

    @Override
    public void initUI() {
        getIntentVaule();
        binding = (ActivityTicketDateWorkBinding) rootDataBinding;
        binding.includeTitle.ibtSync.setVisibility(View.VISIBLE);
        titleArrays[1] = TicketEnum.GZJL.value;
        titleArrays[0] = TicketEnum.GZRZ.value;
        initFragments();
        initClick();
    }


    private void initFragments() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<>();
        }

        TicketDailyWorkFragment dailyWorkFragment = new TicketDailyWorkFragment();
        mFragmentList.add(dailyWorkFragment);

        TicketWorkRecordFragment recordFragment = new TicketWorkRecordFragment();
        mFragmentList.add(recordFragment);

        FragmentPagerAdapter fragmentPagerAdapter = new TicketFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, Arrays.asList(titleArrays));
        binding.viewpager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewpager);
        setTabStripStyle(binding.tabStrip);
        binding.tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {
        CustomerDialog.showProgress(this, "正在同步数据，请确保网络畅通");
        KSyncConfig.getInstance().getKNConfig(getApplicationContext()).setFailListener(syncSuccess -> {
            if (!TextUtils.isEmpty(Config.otherDeptUser) && Config.OTHER_DEPT_USER.equalsIgnoreCase(Config.otherDeptUser)) {
                try {
                    DbModel model = WorkTicketDbManager.getInstance().getTicketManager().findDbModelFirst(new SqlInfo("select * from users where account = '" + Config.userAccount + "' and dlt = 0"));
                    if (null != model) {
                        Config.deptName = model.getString("dept_name");
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            if (!syncSuccess) {
                ToastUtils.showMessage("同步失败");
            } else {
                ToastUtils.showMessage("同步成功，请继续操作");
            }
            CustomerDialog.dismissProgress();
        }).downLoad();
    }


    @Override
    protected void onPause() {
        super.onPause();
        KSyncConfig.getInstance().setCallBackListenerNull();
    }

    public void initClick() {
        binding.ticketTxtAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, TicketDateWorkActivity.class);
            startActivity(intent);

        });

        binding.includeTitle.ticketBack.setOnClickListener(v -> onBackPressed());
        binding.includeTitle.ibtSync.setOnClickListener(v -> {
            CustomerDialog.showProgress(this, "正在同步数据，请确保网络畅通");
            KSyncConfig.getInstance().setFailListener(syncSuccess -> {
                if (!syncSuccess) {
                    ToastUtils.showMessage("同步失败");
                } else {
                    ToastUtils.showMessage("同步成功，请继续操作");
                }
                CustomerDialog.dismissProgress();
            }).upload().downLoad();
        });

    }

}
