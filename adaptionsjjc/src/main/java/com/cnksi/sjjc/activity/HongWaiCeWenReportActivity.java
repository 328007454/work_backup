//package com.cnksi.sjjc.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.cnksi.core.utils.CoreConfig;
//import com.cnksi.core.utils.DateUtils;
//import com.cnksi.sjjc.Config;
//import com.cnksi.sjjc.R;
//import com.cnksi.sjjc.adapter.FragmentPagerAdapter;
//import com.cnksi.sjjc.bean.Report;
//import com.cnksi.sjjc.bean.ReportHwcw;
//import com.cnksi.sjjc.enmu.InspectionType;
//import com.cnksi.sjjc.fragment.HongWaiCeWenFragment;
//import com.cnksi.sjjc.service.BaseService;
//import com.cnksi.sjjc.service.ReportService;
//
//import org.xutils.ex.DbException;
//import org.xutils.view.annotation.ViewInject;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by han on 2016/5/6.
// */
//public class HongWaiCeWenReportActivity extends BaseReportActivity {
//    /**
//     * 巡检开始时间
//     */
//    @ViewInject(R.id.tv_inspection_start_time)
//    private TextView mTvInspectionStartTime;
//
//    /**
//     * 巡检结束时间
//     */
//    @ViewInject(R.id.tv_inspection_end_time)
//    private TextView mTvInspectionEndTime;
//    /**
//   * 巡检人员
//     */
//    @ViewInject(R.id.tv_inspection_person)
//    private TextView mTvInspectionPerson;
//    /**
//     * 温度
//     */
//    @ViewInject(R.id.tv_inspection_temperature)
//    private TextView mTvInspectionTemperature;
//    /**
//     * 湿度
//     */
//    @ViewInject(R.id.tv_inspection_humidity)
//    private TextView mTvInspectionHumidity;
//    /**
//     * 天气
//     */
//    @ViewInject(R.id.tv_inspection_weather)
//    private TextView mTvInspectionWeather;
//
//    /**
//     * 巡检结果
//     */
//    @ViewInject(R.id.tv_inspection_result)
//    private TextView mTvInspectionResult;
//
//    /**
//     * 继续巡检
//     */
//    @ViewInject(R.id.tv_continue_inspection)
//    private TextView mTvInspectionContinue;
//
//    /**
//     * 记录页面
//     */
//    @ViewInject(R.id.mhelp_viewpager)
//    private ViewPager mHelpViewPager;
//    /**
//     * 滑动点
//     */
//    @ViewInject(R.id.ll_dot_container)
//    private LinearLayout mLLDotContainer;
//
//    private FragmentPagerAdapter fragmentPagerAdapter;
//    private ArrayList<Fragment> mFragmentList=new ArrayList<Fragment>();
//    /**
//     * 是否是保护屏测温
//     */
//
//    private boolean isBhpcw = false;
//    /**
//     * 当前报告
//     */
//    private   Report report;
//
//    /**
//     *
//     * 当前记录详情
//     */
//    private List<ReportHwcw> mReportList=new ArrayList<ReportHwcw>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getIntentValue();
//        initUI();
//        initData();
//
//    }
//
//    @Override
//    public View setReportView() {
//        return getLayoutInflater().inflate(R.layout.report_hwcw,null);
//    }
//
//    private  void initFragment()
//    {
//        for (int i=0,count=mReportList.size();i<count;i++)
//        {
//            HongWaiCeWenFragment fragment=new HongWaiCeWenFragment();
//            Bundle args = new Bundle();
//            args.putSerializable(Config.CURRENT_REPORT, mReportList.get(i));
//            args.putBoolean(Config.STATUS, isBhpcw);
//            fragment.setArguments(args);
//            mFragmentList.add(fragment);
//            ImageView dotImageView = new ImageView(_this);
//            dotImageView.setLayoutParams(new LinearLayout.LayoutParams(40,40));
//            dotImageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            dotImageView.setPadding(5, 5, 5, 5);
//            if (i == 0) {
//                dotImageView.setImageResource(R.mipmap.select_dot);
//            } else {
//                dotImageView.setImageResource(R.mipmap.unselect_dot);
//            }
//            mLLDotContainer.addView(dotImageView);
//        }
//        fragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager(),mFragmentList);
//        mHelpViewPager.setAdapter(fragmentPagerAdapter);
//    }
//
//
//    private void initUI() {
//        if (currentInspectionType.equals(InspectionType.SBJC_02.name())) {
//            isBhpcw = true;
//        }
//        mTvInspectionContinue.setText("继续测温");
//        mTvInspectionContinue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(_this,HWCWMainActivity.class));
//                finish();
//            }
//        });
//        mHelpViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//            @Override
//            public void onPageSelected(int position) {
//                int count = mLLDotContainer.getChildCount();
//                if ( count > 0) {
//                    for (int i = 0; i < count; i++) {
//                        if (i == position) {
//                            ((ImageView) mLLDotContainer.getChildAt(i)).setImageResource(R.mipmap.select_dot);
//                        } else {
//                            ((ImageView) mLLDotContainer.getChildAt(i)).setImageResource(R.mipmap.unselect_dot);
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//    }
//
//
//    private void initData() {
//        mExcutorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    report = ReportService.getInstance().findById(currentReportId);
//                    mReportList= BaseService.getInstance(ReportHwcw.class).selector().and(ReportHwcw.REPORT_ID,"=",currentReportId).findAll();
//                } catch (DbException e) {
//                    e.printStackTrace(System.out);
//                }
//                mHandler.sendEmptyMessage(LOAD_DATA);
//            }
//        });
//    }
//
//    @Override
//    protected void onRefresh(Message msg) {
//        super.onRefresh(msg);
//        switch (msg.what) {
//            case LOAD_DATA:
//                mTvInspectionStartTime.setText(DateUtils.getFormatterTime(report.starttime, CoreConfig.dateFormat8));
//                mTvInspectionEndTime.setText(TextUtils.isEmpty(report.endtime) ? DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8) : DateUtils.getFormatterTime(report.endtime, CoreConfig.dateFormat8));
//                mTvInspectionPerson.setText(report.persons);
//                mTvInspectionTemperature.setText(report.temperature.contains(getString(R.string.temperature_unit_str)) ? report.temperature : report.temperature + getString(R.string.temperature_unit_str));
//                mTvInspectionHumidity.setText(report.humidity.contains(getString(R.string.humidity_unit_str)) ? report.humidity : report.humidity + getString(R.string.humidity_unit_str));
//                mTvInspectionWeather.setText(report.tq);
//                if (mReportList != null) {
//                    mTvInspectionResult.append((mReportList.size()>0?"异常"+"，发现发热点" + mReportList.size() + "个":"正常"));
//                    initFragment();
//                }
//
//                break;
//        default:
//        }
//
//    }
//
//
//
//
//}
