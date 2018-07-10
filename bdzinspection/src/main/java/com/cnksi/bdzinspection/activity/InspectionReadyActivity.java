package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.xian.XFullDeviceListActivity;
import com.cnksi.bdzinspection.activity.xian.XParticularDeviceListActivity;
import com.cnksi.bdzinspection.daoservice.InspectionPreparedService;
import com.cnksi.bdzinspection.daoservice.ZzhtService;
import com.cnksi.bdzinspection.databinding.XsActivityInspectionReadyBinding;
import com.cnksi.bdzinspection.fragment.inspectionready.CopyTemperatureFragment;
import com.cnksi.bdzinspection.fragment.inspectionready.DangerContentFragment;
import com.cnksi.bdzinspection.fragment.inspectionready.MultipleBackFragment;
import com.cnksi.bdzinspection.fragment.inspectionready.ReadyExistingDefectFragment;
import com.cnksi.bdzinspection.fragment.inspectionready.ReadyExistingDefectFragment.OnFragmentEventListener;
import com.cnksi.bdzinspection.fragment.inspectionready.RoadMapFragment;
import com.cnksi.bdzinspection.fragment.inspectionready.ToolFragment;
import com.cnksi.bdzinspection.model.InspectionPrepared;
import com.cnksi.bdzinspection.model.zzht.Zzht;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.CommonUtils;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 巡检前准备 抄录温湿度 危险点 工器具等
 *
 * @author terry on 2017/10/19
 */
public class InspectionReadyActivity extends BaseActivity implements OnFragmentEventListener {
    public static final String TAG = "InspectionReady";
    public static final int UPDATE_DEVICE_DEFECT_REQUEST_CODE = 0x111;
    public static final String DOUBLE_ZERO = "00";


    XsActivityInspectionReadyBinding binding;

    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = null;
    private List<String> titleArray = null;
    /**
     * 是否显示现存缺陷
     */
    private boolean isShowExistDefect = false;
    /**
     * 抄录温湿度
     */
    private CopyTemperatureFragment mTemperatureFragment = null;
    /**
     * 危险点和工器具
     */
    private DangerContentFragment mDangerpointFragment;
    private ToolFragment mToolsFragment;
    private ReadyExistingDefectFragment mExistingDefectFragment = null;
    private RoadMapFragment mRoadMapFragment;


    private Task task;
    private Report mReport;
    private int currentPosition = 0;
    private Zzht zzht;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changedStatusColor();
        binding = XsActivityInspectionReadyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialUI();
    }

    private void initialUI() {
        getIntentValue();
        binding.tvTitle.setText(R.string.xs_inspection_ready_str);
        if (!currentInspectionType.contains(InspectionType.special.name())
                && !InspectionType.battery.name().equalsIgnoreCase(currentInspectionType)) {
            isShowExistDefect = true;
        }

        TTSUtils.getInstance().startSpeaking(getString(R.string.xs_before_inspection_ready_str));
        binding.btnLeft.setVisibility(View.GONE);
        if (SystemConfig.isMustVerifyInspectionReady()) {
            binding.btnRight.setText(getString(R.string.xs_sure_and_next));
            binding.btnRight.setBackgroundResource(R.drawable.xs_green_background_selector);
            binding.viewPager.setNoScroll(true);
            binding.tabStrip.setNoScroll(true);
        } else {
            binding.btnRight.setText(getString(R.string.xs_start_inspection_button_format_str, currentInspectionTypeName));
        }
        initClick();
        initZzht();
    }

    private void initZzht() {
        ExecutorManager.executeTask(() -> {
            zzht = ZzhtService.getInstance().bdzInZzht(currentBdzId);
            InspectionReadyActivity.this.runOnUiThread(() -> InspectionReadyActivity.this.initFragmentList());

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean needReLoad = PreferencesUtils.get("RELOAD_DATA", false) &&
                ("全面巡视".equalsIgnoreCase(currentInspectionTypeName)
                        || currentInspectionTypeName.equalsIgnoreCase(InspectionType.routine.toString()));
        if (needReLoad) {
            PreferencesUtils.put("RELOAD_DATA", false);
            if (mFragmentList.size() == 5) {
                ((RoadMapFragment) mFragmentList.get(3)).reLoadData();
            } else {
                ((RoadMapFragment) mFragmentList.get(2)).reLoadData();
            }
        }

    }

    private void initFragmentList() {
        task = TaskService.getInstance().findById(currentTaskId);
        mFragmentList = new ArrayList<>();
        Bundle args = new Bundle();
        boolean hasZzht = null != zzht;
        hasZzht = hasZzht && ("full".equals(currentInspectionType));
        if (!task.isMember()) {
            if (hasZzht) {
                titleArray = Arrays.asList(getResources().getStringArray(R.array.XS_InspectionReadyTitleArrayMore));
            } else {
                titleArray = Arrays.asList(getResources().getStringArray(R.array.XS_InspectionReadyTitleArray));
            }
            // 抄录温湿度
            mTemperatureFragment = new CopyTemperatureFragment();
            mTemperatureFragment.setArguments(args);
            mFragmentList.add(mTemperatureFragment);
        } else {
            if (hasZzht) {
                titleArray = Arrays.asList(getResources().getStringArray(R.array.XS_InspectionReadyTitleArrayForMembersMore));
            } else {
                titleArray = Arrays.asList(getResources().getStringArray(R.array.XS_InspectionReadyTitleArrayForMembers));
            }
        }
        // 危险点
        mDangerpointFragment = new DangerContentFragment();
        args = new Bundle();
        args.putString(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
        args.putString(Config.CURRENT_BDZ_ID, currentBdzId);
        mDangerpointFragment.setArguments(args);
        mFragmentList.add(mDangerpointFragment);

        // 工器具
        if (!isParticularInspection()) {
            mToolsFragment = new ToolFragment();
            args = new Bundle();
            args.putString(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
            mToolsFragment.setArguments(args);
            mFragmentList.add(mToolsFragment);
        }

        if (isShowExistDefect) {
            mRoadMapFragment = new RoadMapFragment();
            mRoadMapFragment.setArguments(args);
            args = new Bundle();
            args.putString(Config.CURRENT_BDZ_ID, currentBdzId);
            mRoadMapFragment.setArguments(args);
            mFragmentList.add(mRoadMapFragment);

            // 现存缺陷
            mExistingDefectFragment = new ReadyExistingDefectFragment();
            mExistingDefectFragment.setArguments(args);
            mExistingDefectFragment.setOnFragmentEventListener(this);
            mFragmentList.add(mExistingDefectFragment);
        }
        if (hasZzht) {
            MultipleBackFragment backFragment = new MultipleBackFragment();
            args = new Bundle();
            backFragment.setArguments(args);
            mFragmentList.add(backFragment);
        }

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, titleArray);
        binding.viewPager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        binding.tabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                handleButtonViewChange();
            }
        });
        setPagerTabStripValue(binding.tabStrip);
        binding.viewPager.setOffscreenPageLimit(mFragmentList.size());
        if (isShowExistDefect) {
            binding.tabStrip.setTabPaddingLeftRight(28);
            binding.tabStrip.setShouldExpand(false);
        }
        mReport = ReportService.getInstance().getReportById(currentReportId);
    }

    private void initClick() {
        binding.ibtnCancel.setOnClickListener(v -> {
            ExecutorManager.executeTask(() -> {
                Fragment fragment = mFragmentList.get(mFragmentList.size() - 1);
                if (fragment instanceof MultipleBackFragment) {
                    ((MultipleBackFragment) fragment).saveData();
                }

            });
            onBackPressed();
        });
        if (SystemConfig.isMustVerifyInspectionReady()) {
            binding.btnLeft.setOnClickListener(v -> binding.viewPager.setCurrentItem(--currentPosition, true));
            binding.btnRight.setOnClickListener(v -> {
                if (mFragmentList.get(currentPosition) instanceof CopyTemperatureFragment) {
                    String temperature = mTemperatureFragment.getCurrentTemperature();
                    String weather = mTemperatureFragment.getCurrentWeater();
                    if (TextUtils.isEmpty(weather) || TextUtils.isEmpty(temperature)) {
                        ToastUtils.showMessage("请选择天气和填写温度！");
                        return;
                    }
                }
                if (currentPosition < mFragmentList.size() - 1) {
                    binding.viewPager.setCurrentItem(++currentPosition, true);
                } else {
                    Fragment fragment = mFragmentList.get(currentPosition);
                    if (fragment instanceof MultipleBackFragment && !((MultipleBackFragment) fragment).getCheckAll()) {
                        ToastUtils.showMessage("请核实完所有的综自后台步骤");
                        return;
                    }
                    startInspection();
                }
            });
        } else {
            binding.btnRight.setOnClickListener(v -> startInspection());
        }
    }

    private void handleButtonViewChange() {
        if (!SystemConfig.isMustVerifyInspectionReady()) {
            return;
        }
        if (currentPosition == 0) {
            binding.btnLeft.setVisibility(View.GONE);
            binding.btnRight.setText(R.string.xs_sure_and_next);
        } else {
            binding.btnLeft.setVisibility(View.VISIBLE);
            binding.btnRight.setVisibility(View.VISIBLE);
            binding.btnLeft.setText(R.string.xs_back_last_step);
            if (currentPosition < mFragmentList.size() - 1) {
                binding.btnRight.setText(R.string.xs_sure_and_next);
            } else {
                binding.btnRight.setText(getString(R.string.xs_start_inspection_button_format_str, currentInspectionTypeName));
            }
        }
    }

    /**
     * 开始**巡检<br>
     * 1.先生成对应任务的报告<br>
     * 2.跳转到相应的巡视界面
     */
    private void startInspection() {
        // 生成报告
        if (generateReport()) {
            TTSUtils.getInstance().stopSpeak();
            Intent intent = new Intent();
            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, currentInspectionType);
            if (isParticularInspection()) {
                // 大风 大雾 下雪 雷雨 高温 夜间 熄灯
                intent.setClass(mActivity, XParticularDeviceListActivity.class);
            } else {
                intent.setClass(mActivity, XFullDeviceListActivity.class);
            }
            saveInspectionAlready();
            startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
        }
    }


    private void saveInspectionAlready() {
        ExecutorManager.executeTask(() -> {
            InspectionPrepared prepared = new InspectionPrepared(mReport.reportid, task.taskid, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
            try {
                SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(InspectionPreparedService.getInstance().getTable());
                InspectionPreparedService.getInstance().execSql(sqlInfo);
                InspectionPreparedService.getInstance().saveOrUpdate(prepared);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 生成报告
     * 1.先查询任务对应的报告是否存在，不存在新生成一个报告
     */
    private boolean generateReport() {
        if (task.isMember()) {
            if (mReport == null) {
                ToastUtils.showMessageLong("该任务不是由你创建,但没有获取到报告！请尝试重新同步！");
                return false;
            }
            return true;
        }
        String temperature = mTemperatureFragment.getCurrentTemperature();
        String weather = mTemperatureFragment.getCurrentWeater();
        if (TextUtils.isEmpty(weather) || TextUtils.isEmpty(temperature)) {
            ToastUtils.showMessage("请选择天气和填写温度！");
            return false;
        }
        boolean needTips = !CommonUtils.testTemperature(temperature) || (-99.9f > new Float(temperature) || new Float(temperature) > 99.99);
        if (needTips) {
            ToastUtils.showMessage("温度在-99.9℃到99.99℃");
            return false;
        } else {
            temperature = CommonUtils.getTransformTep(temperature);
            if (temperature.startsWith(DOUBLE_ZERO)) {
                temperature = "0.0";
            }
            mTemperatureFragment.setCurrentTempert(temperature);
        }
        try {
            mReport.temperature = temperature;
            mReport.tq = weather;
            mReport.pmsJhid = task.pmsJhid;
            mReport.reportSource = Config.REPORT_SOURCE_REPORT;
            mReport.inspectionValue = currentInspectionTypeName;
            mReport.departmentId = PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, "");
            task.status = TaskStatus.doing.name();
            ExecutorManager.executeTask(() -> {
                try {
                    ReportService.getInstance().saveOrUpdate(mReport);
                    TaskService.getInstance().saveOrUpdate(task);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            });
            if (mToolsFragment != null) {
                mToolsFragment.save(mReport.reportid);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onRefresh(Message msg) {

    }

    @Override
    public void onBackPressed() {
        generateReport();
        setResult(RESULT_OK);
        this.finish();
        mActivity.overridePendingTransition(R.anim.xs_slide_in_right, R.anim.xs_slide_out_right);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_DEVICE_DEFECT_REQUEST_CODE:
                    // 刷新界面
                    if (mExistingDefectFragment != null) {
                        mExistingDefectFragment.searchData();
                    }
                    if (null != mRoadMapFragment) {
                        mRoadMapFragment.searchData(LOAD_DATA);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 缺陷记录返回
     *
     * @param intent
     * @param requestCode
     */
    @Override
    public void onDefectItemClick(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
    }
}