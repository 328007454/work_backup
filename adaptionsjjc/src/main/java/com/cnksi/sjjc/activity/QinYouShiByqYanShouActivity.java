package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.NumberUtil;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BindPopMenuAdapter;
import com.cnksi.sjjc.adapter.FragmentPagerAdapter;
import com.cnksi.sjjc.bean.AcceptType;
import com.cnksi.sjjc.fragment.AttendPersonFragment;
import com.cnksi.sjjc.fragment.YanShouRequestFragment;
import com.cnksi.sjjc.fragment.YanShouStandardFargment;
import com.cnksi.sjjc.fragment.YiChangDealFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by han on 2016/5/9.
 * 浸油试验收界面
 */
public class QinYouShiByqYanShouActivity extends BaseActivity {
    //顶部导航栏
    @ViewInject(R.id.tab_strip)
    private PagerSlidingTabStrip mPagerTabStrip;

    @ViewInject(R.id.viewPager)
    private ViewPager mYanshouViewPager;
    //验收类型文字
    @ViewInject(R.id.tv_right)
    private TextView tvRight;
    //标题栏
    @ViewInject(R.id.tv_title_yanshou)
    private TextView tvTitle;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private ArrayList<Fragment> mFragmentList;
    private String[] titleArray = null;

    private ArrayList<AcceptType> listType;
    // 当前选中的position
    private int currentSelectedPosition = 0;
    //验收项集合
    private ArrayList<String> listPager = new ArrayList<String>();
    //（参加人员、验收要求、异常处置）内容描述
    private ArrayList<String> contentList = new ArrayList<String>();
    //当前选择的验收类型
    private String selectType;
    //当前验收类型的id
    private String typeId;
    //验收类型所选择的第几项
    private int selectNum = 0;
    //设备id
    private String deviceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_task_remind);
        initUI();
        initData();
        initFragmentList();
    }

    private void initData() {
        selectType = listType.get(selectNum).acceptTypeName;
        deviceId = listType.get(selectNum).deviceTypeId;
        typeId = listType.get(selectNum).acceptTypeId;
        listPager.add("参加人员");
        listPager.add("验收要求");
        listPager.add("验收标准卡");
        String json = listType.get(selectNum).acceptBaseInfo;
        try {
            JSONObject jsonObject = new JSONObject(json);
            contentList.add(jsonObject.getString("参加人员"));
            contentList.add(jsonObject.getString("验收要求"));
            contentList.add(jsonObject.getString("异常处置"));
            listPager.add(2, "异常处置");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
    }

    private void initUI() {
        tvRight.setVisibility(View.VISIBLE);
        tvTitle.setText("油浸式变压器验收");
        titleArray = getResources().getStringArray(R.array.TaskTitleArray);
        try {
            listType = (ArrayList<AcceptType>) dbYShou.selector(AcceptType.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        tvRight.setText(listType.get(selectNum).acceptTypeName);
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                mFragmentList.clear();
                listPager.clear();
                contentList.clear();
                initData();
                initFragmentList();
                break;
            default:
                break;
        }
    }


    private YanShouStandardFargment ySStandardFragment;

    /**
     * 根据不同验收类型具体验收项来加载fragment数量（最多4个fragment，最少3个）
     */
    private void initFragmentList() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }
        for (int i = 0; i < listPager.size(); i++) {
            if (i == 0) {
                //参加人员fragment
                AttendPersonFragment aPFragment = new AttendPersonFragment();
                Bundle bundle = new Bundle();
                bundle.putString("0", contentList.get(i));
                aPFragment.setArguments(bundle);
                mFragmentList.add(aPFragment);
            } else if (i == 1 && i != listPager.size() - 1) {
                //验收要求fragment
                YanShouRequestFragment ySRequestFragment = new YanShouRequestFragment();
                Bundle bundle = new Bundle();
                bundle.putString("1", contentList.get(i));
                ySRequestFragment.setArguments(bundle);
                mFragmentList.add(ySRequestFragment);
            }
            if (i == 2) {
                if (i != listPager.size() - 1) {
                    //验收异常fragment
                    YiChangDealFragment yCDealFragment = new YiChangDealFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("2", contentList.get(i));
                    yCDealFragment.setArguments(bundle);
                    mFragmentList.add(yCDealFragment);

                } else if (i == listPager.size() - 1) {
                    //验收标准fragment
                    ySStandardFragment = new YanShouStandardFargment();
                    Bundle bundle = new Bundle();
                    bundle.putString("4", typeId);
                    bundle.putString(Config.DEVICE_ID, deviceId);
                    bundle.putString(Config.TYPE_NAME, selectType);
                    ySStandardFragment.setArguments(bundle);
                    mFragmentList.add(ySStandardFragment);
                }

            } else if (i == 3 && i == listPager.size() - 1) {
                //验收标准fragment
                ySStandardFragment = new YanShouStandardFargment();
                Bundle bundle = new Bundle();
                bundle.putString("4", typeId);
                bundle.putString(Config.DEVICE_ID, deviceId);
                ySStandardFragment.setArguments(bundle);
                mFragmentList.add(ySStandardFragment);
            }

        }
        fragmentPagerAdapter.setFragments(mFragmentList);
        fragmentPagerAdapter.setTitleArray(listPager);
        mYanshouViewPager.setAdapter(fragmentPagerAdapter);
        mPagerTabStrip.setViewPager(mYanshouViewPager);
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
        mPagerTabStrip.setSelectedPosition(0);//设置当选择不同的验收类型后，让当前选中验收项目文字变红
        mYanshouViewPager.setOffscreenPageLimit(listPager.size());
    }

    /**
     * 设置导航栏的样式（文字颜色以及大小等）
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
        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getTextScale() * 40), mDisplayMetrics));
        // 设置Tab Indicator的颜色
        mPagerTabStrip.setIndicatorColor(getResources().getColor(R.color.tab_strip_text_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mPagerTabStrip.setSelectedTextColor(getResources().getColor(R.color.tab_strip_text_color));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }

    @Event(R.id.tv_right)
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                showPopWindow();
                break;
        }
    }

    PopupWindow mPopWindow;

    /**
     * 验收类型的popwindow（展示当前验收条件下的所有验收类型）
     */
    private void showPopWindow() {

        if (mPopWindow == null) {
            // 动态加载弹出框布局
            View contentView = getLayoutInflater().inflate(R.layout.popwindow_listview, null, false);
            ListView mListView = (ListView) contentView.findViewById(R.id.inner_listivew);
            BindPopMenuAdapter mBindPopMenuAdapter = new BindPopMenuAdapter(this, listType);
            mListView.setAdapter(mBindPopMenuAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AcceptType mAcceptType = (AcceptType) parent.getItemAtPosition(position);
                    tvRight.setText(mAcceptType.acceptTypeName);
                    mPopWindow.dismiss();
                    if (selectNum != position) {
                        selectNum = position;
                        mHandler.sendEmptyMessage(LOAD_DATA);
                    }
                }
            });

            // 设置菜单栏布局文件和显示大小
            mPopWindow = new PopupWindow(contentView, ScreenUtils.getScreenWidth(_this) / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
            // 设置菜单栏背景为透明(不设置无法正常获取焦点)
            mPopWindow.setBackgroundDrawable(new ColorDrawable());
            // 设置菜单栏动画
            mPopWindow.setAnimationStyle(R.style.PopwindowAnimtionStyle);
            // 设置菜单栏能够获取焦点
            mPopWindow.setFocusable(true);
            mPopWindow.setOutsideTouchable(true);
            // 更新设置
            mPopWindow.update();
        }
        if (!mPopWindow.isShowing()) {
            mPopWindow.showAsDropDown(tvRight);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Config.QIAN_MING_REQUEST:
                    ySStandardFragment.onActivityResult(requestCode, resultCode, data);
                    break;
                case Config.PAIZHAO_LUXIANG_REQUSET:
                    ySStandardFragment.onActivityResult(requestCode, resultCode, data);
                    break;
                default:
                    break;
            }

        }
    }
}
