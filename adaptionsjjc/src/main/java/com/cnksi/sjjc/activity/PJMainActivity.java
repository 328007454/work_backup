package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.NumberUtil;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.FragmentPagerAdapter;
import com.cnksi.sjjc.bean.EvaluationItem;
import com.cnksi.sjjc.bean.EvaluationItemReport;
import com.cnksi.sjjc.bean.EvaluationReport;
import com.cnksi.sjjc.fragment.CheckFragment;
import com.cnksi.sjjc.fragment.ToolFragment;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价主界面
 */
public class PJMainActivity extends BaseActivity {

    @ViewInject(R.id.tab_strip)
    private PagerSlidingTabStrip mPagerTabStrip;
    @ViewInject(R.id.viewPager)
    private ViewPager mTaskViewPager;

    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = null;
    private List<String> titleArray = null;
    CheckFragment checkFragment;
    ToolFragment toolFragment;
    EvaluationItem evaItem;
    EvaluationReport evaluationReport;
    EvaluationItemReport itemReport;
    DbManager db= CustomApplication.getPJDbManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_pjmain);
        getIntentValue();
        initUI();
        initFragmentList();

    }

    private void initUI()
    {
        setTitleText(evaItem.itemContent);
        try {
            itemReport=db.selector(EvaluationItemReport.class).where(EvaluationItemReport.REPORT_ID,"=",evaluationReport.reportId).
                    and(EvaluationItemReport.ITEM_ID,"=",evaItem.itemId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void initFragmentList() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }
        Bundle args = new Bundle();
        args.putSerializable(Config.DATA,evaItem);
        args.putSerializable(Config.DATA1,itemReport);
        // 检查fragment
        checkFragment = new CheckFragment();
        checkFragment.setArguments(args);
        mFragmentList.add(checkFragment);
        // 危险点
        toolFragment = new ToolFragment();
        toolFragment.setArguments(args);
        mFragmentList.add(toolFragment);
        titleArray=new ArrayList<String>();
        titleArray.add("检查及扣分");
        titleArray.add("工具箱");
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, titleArray);
        mTaskViewPager.setAdapter(fragmentPagerAdapter);
        mPagerTabStrip.setViewPager(mTaskViewPager);
        setPagerTabStripValue(mPagerTabStrip);
        mTaskViewPager.setOffscreenPageLimit(mFragmentList.size());
    }

    public void getIntentValue() {
        evaluationReport= (EvaluationReport) getIntent().getSerializableExtra(Config.DATA1);
        evaItem = (EvaluationItem) getIntent().getSerializableExtra(Config.DATA);
    }

    @Event(value = {R.id.btn_cancel,R.id.btn_confirm})
    private void OnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                save();
                onBackPressed();
                break;
            default:
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK)
        {
                toolFragment.onActivityResult(requestCode,resultCode,data);
        }
    }
    private boolean isUpdate=false;
    public void save()
    {
        EvaluationItemReport item=checkFragment.getSaveData();
        String []str=toolFragment.getSaveData();
        item.itemImages=str[0];
        item.itemAudio=str[1];
        item.itemVideo=str[2];
        item.reportId=evaluationReport.reportId;
        item.itemId=evaItem.itemId;
        if (itemReport!=null) {
            item.uid = itemReport.uid;
            isUpdate = true;
        }
        try {
            db.saveOrUpdate(item);
            Intent data=new Intent();
            data.putExtra(Config.DATA,isUpdate);
            data.putExtra(Config.DATA1,item.discountScore);
            setResult(RESULT_OK,data);
        } catch (DbException e) {
            e.printStackTrace();
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
        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getTextScale() * 40), mDisplayMetrics));
        // 设置Tab Indicator的颜色
        mPagerTabStrip.setIndicatorColor(getResources().getColor(R.color.tab_strip_text_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mPagerTabStrip.setSelectedTextColor(getResources().getColor(R.color.tab_strip_text_color));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }
}
