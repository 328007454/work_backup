package com.cnksi.bdzinspection.czp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.activity.DownloadOperationTickActivity;
import com.cnksi.bdzinspection.adapter.FragmentPagerAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsActivityOperateTaskListBinding;
import com.cnksi.bdzinspection.fragment.OperateTaskListFragment;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.OperateTaskStatus;
import com.cnksi.bdzinspection.utils.Config.OperateTaskType;
import com.cnksi.xscore.xsutils.CLog;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操作票任务列表界面
 *
 * @author Oliver
 */
public class OperateTaskListActivity extends BaseActivity implements OnPageChangeListener {

    public static final int DOWNLOAD_OPERATION_CODE = 0x111;
    public static final int START_OPERATE_CODE = DOWNLOAD_OPERATION_CODE + 1;
    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private List<String> titleArray = null;
    private String[] functionModelArray = {OperateTaskType.DBRW.name(), OperateTaskType.BYRW.name(), OperateTaskType.BNRW.name()};

    private int currentPosition = 0;

    private XsActivityOperateTaskListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_operate_task_list);
        String id = getIntent().getStringExtra("task_id");
        if (!TextUtils.isEmpty(id)) {
            try {
                DbModel model = XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo("select * from operate_tick where id='" + id + "'"));
                if (model != null) {
                    CLog.e("收到第三方跳转请求，中断加载Activity");
                    startOperateTask(model);
                    finish();
                    return;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        initUI();

        initData();

        initOnClick();
    }


    private void initUI() {
        if (getIntent().getBooleanExtra("isThird", false)) {
              binding.includeTitle.tvTitle.setText("倒闸操作");
        } else {
              binding.includeTitle.tvTitle.setText(R.string.xs_operate_task_list_str);
        }
          binding.includeTitle.tvBatteryTestStep.setText(R.string.xs_download_str);
          binding.includeTitle.tvBatteryTestStep.setVisibility(View.VISIBLE);
    }

    private void initData() {
        titleArray = Arrays.asList(getResources().getStringArray(R.array.XS_OperateTaskTitleArray));
        initFragmentList();
        searchTitleArray();
    }

    private void searchTitleArray() {
        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                List<String> titleList = OperateTicketService.getInstance().getTaskCount(currentActivity);
                mHandler.sendMessage(mHandler.obtainMessage(LOAD_DATA, titleList));
            }
        });

    }

    private void initFragmentList() {
        for (int i = 0, count = titleArray.size(); i < count; i++) {
            Bundle bundle = new Bundle();
            bundle.putString(Config.CURRENT_FUNCTION_MODEL, functionModelArray[i]);
            mFragmentList.add(OperateTaskListFragment.getInstance(bundle));
        }
        fragmentPagerAdapter = new FragmentPagerAdapter(fManager, mFragmentList, titleArray);
          binding.viewPager.setAdapter(fragmentPagerAdapter);
          binding.tabStrip.setViewPager(  binding.viewPager);
        setPagerTabStripValue(  binding.tabStrip);
          binding.viewPager.setOffscreenPageLimit(mFragmentList.size());
          binding.viewPager.addOnPageChangeListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                titleArray = (List<String>) msg.obj;
                fragmentPagerAdapter.setTitleArray(titleArray);
                  binding.tabStrip.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
    
    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> finish());
        binding.includeTitle.tvBatteryTestStep.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, DownloadOperationTickActivity.class);
            startActivityForResult(intent, DOWNLOAD_OPERATION_CODE);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_OPERATE_CODE:
                case DOWNLOAD_OPERATION_CODE:
                    searchTitleArray();
                    for (int i = 0, count = mFragmentList.size(); i < count; i++) {
                        if (i != currentPosition) {
                            ((OperateTaskListFragment) mFragmentList.get(i)).setIsFirstLoad(true);
                        } else {
                            ((OperateTaskListFragment) mFragmentList.get(i)).searchData();
                        }
                    }

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
    }

    public void startOperateTask(DbModel item) {
        if (OperateTaskStatus.wwc.name().equalsIgnoreCase(item.getString(OperateTick.STATUS)) || OperateTaskStatus.yzt.name().equalsIgnoreCase(item.getString(OperateTick.STATUS))) {
            // 已暂停和未完成 状态跳转到任务详情界面
            Intent intent = new Intent(currentActivity, OperateTaskDetailsActivity.class);
            intent.putExtra(Config.CURRENT_TASK_ID, item.getString(OperateTick.ID));
            startActivity(intent);
        } else if (OperateTaskStatus.dsh.name().equalsIgnoreCase(item.getString(OperateTick.STATUS))) {
            // 跳转到待审核界面
            Intent intent = new Intent(currentActivity, OperateTaskCheckedActivity.class);
            intent.putExtra(Config.CURRENT_TASK_ID, item.getString(OperateTick.ID));
            startActivityForResult(intent, START_OPERATE_CODE);
        } else if (OperateTaskStatus.ywc.name().equalsIgnoreCase(item.getString(OperateTick.STATUS))) {
            // 跳转到待审核界面
            Intent intent = new Intent(currentActivity, OperateTicketReportActivity.class);
            intent.putExtra(Config.CURRENT_TASK_ID, item.getString(OperateTick.ID));
            intent.putExtra(Config.IS_FROM_BATTERY, false);
            startActivity(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Config.IS_FROM_BATTERY, false)) {
            onActivityResult(START_OPERATE_CODE, RESULT_OK, intent);
        }
    }
}
