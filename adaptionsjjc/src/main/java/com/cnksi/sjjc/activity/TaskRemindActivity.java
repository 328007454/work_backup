package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.FragmentPagerAdapter;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.fragment.TaskRemindFragment;
import com.cnksi.sjjc.inter.OnFragmentEventListener;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 任务列表
 */
public class TaskRemindActivity extends BaseActivity {
    public static final int ADD_TASK_REQUEST_CODE = 0x01;

    public static final int FINISH_TASK = 0x777;
    @ViewInject(R.id.tab_strip)
    private PagerSlidingTabStrip mPagerTabStrip;

    @ViewInject(R.id.viewPager)
    private ViewPager mTaskViewPager;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private ArrayList<Fragment> mFragmentList;
    private String[] titleArray = null;
    /**
     * 主菜单界面点击的巡检类型
     */
    private InspectionType mInspectionType;
    // 当前选中的position
    private int currentSelectedPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果直接启动的TaskRemind则不需要在OnResume执行更新，因为是全新加载数据。
        isNeedUpdateTaskState=false;
        setChildView(R.layout.activity_task_remind);
        getIntentValue();
        initUI();
    }



    private void initUI() {
        tvTitle.setText(R.string.inspection_task_remind_str);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setImageResource(R.drawable.add_task_button_background);
        titleArray = getResources().getStringArray(R.array.TaskTitleArray);
        String mInspectionValue = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
        ////容错处理代码 避免别的地方回来拿不到type
        // start
        if (TextUtils.isEmpty(mInspectionValue))
        {
            mInspectionValue=PreferencesUtils.getString(_this,Config.CURRENT_SELECT_TASK_TYPE_INSPECTION,"");
        }else{
            PreferencesUtils.put(_this,Config.CURRENT_SELECT_TASK_TYPE_INSPECTION,mInspectionValue);
        }
        //end
        mInspectionType = InspectionType.get(mInspectionValue);
        initFragmentList();
    }



    private void initFragmentList() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }
        String[] functionModelArray = {Config.UNFINISH_MODEL, Config.FINISHED_MODEL, Config.ALL_TASK_MODEL, Config.OVER_DUE_MODEL};
        for (int i = 0; i < titleArray.length; i++) {
            TaskRemindFragment mTaskFragment = new TaskRemindFragment();
//            mTaskFragment.setOnFragmentEventListener(this);
            mTaskFragment.setOnFragmentEventListener(new OnFragmentEventListener() {
                @Override
                public void updateTaskStatused() {
                    updateTaskStatus();
                }
            });
            Bundle args = new Bundle();
            args.putString(Config.CURRENT_FUNCTION_MODEL, functionModelArray[i]);
            args.putString(Config.CURRENT_INSPECTION_TYPE_NAME, mInspectionType.name());
            mTaskFragment.setArguments(args);
            mFragmentList.add(mTaskFragment);
        }
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, Arrays.asList(titleArray));
        mTaskViewPager.setAdapter(fragmentPagerAdapter);
        mPagerTabStrip.setViewPager(mTaskViewPager);

        mPagerTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentSelectedPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setPagerTabStripValue(mPagerTabStrip);
        mTaskViewPager.setOffscreenPageLimit(4);
    }

    @Event(value = {R.id.btn_back, R.id.btn_right})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_right:
                Intent intent = new Intent(this, AddTaskActivity.class);
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, mInspectionType.name());
                startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedUpdateTaskState) {
            updateTaskStatus();
        }
    }

    /**
     * 对PagerSlidingmPagerTabStriptrip的各项属性进行赋值。
     */
    protected void setPagerTabStripValue(PagerSlidingTabStrip mPagerTabStrip) {

        // 当前屏幕密度
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        // 设置Tab的分割线是透明的
        mPagerTabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mPagerTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDisplayMetrics));
        // 设置Tab Indicator的高度
        mPagerTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics));
        // 设置Tab标题文字的大小
        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mCurrentActivity.getResources().getDimensionPixelOffset(R.dimen.tab_strip_text_size), mDisplayMetrics));
        // 设置Tab Indicator的颜色
        mPagerTabStrip.setIndicatorColor(getResources().getColor(R.color.tab_strip_text_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mPagerTabStrip.setSelectedTextColor(getResources().getColor(R.color.tab_strip_text_color));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_TASK_REQUEST_CODE:
                    updateTaskStatus();
                    mTaskViewPager.setCurrentItem(0, false);
                    break;
                case FINISH_TASK:
                    updateTaskStatus();
                    break;
                default:
                    break;
            }
        }
    }

    public void updateTaskStatus() {
        isNeedUpdateTaskState = false;
        for (int i = 0, count = mFragmentList.size(); i < count; i++) {
            if (mFragmentList.get(i) instanceof TaskRemindFragment) {
                if (i == currentSelectedPosition) {
                    ((TaskRemindFragment) (mFragmentList.get(i))).query();
                } else {
                    ((TaskRemindFragment) (mFragmentList.get(i))).setIsFirstLoad(true);
                }
            }
        }
    }


}