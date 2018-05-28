package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.FragmentPagerAdapter;
import com.cnksi.bdzinspection.databinding.XsActivityDefectControlBinding;
import com.cnksi.bdzinspection.fragment.defectcontrol.EliminateDefectFragment;
import com.cnksi.bdzinspection.fragment.defectcontrol.EliminateDefectFragment.OnFunctionButtonClickListener;
import com.cnksi.bdzinspection.fragment.defectcontrol.RecordDefectFragment;
import com.cnksi.bdzinspection.fragment.defectcontrol.TrackDefectFragment;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.common.Config;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.utils.KeyBoardUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 缺陷管控
 *
 * @author terry
 */
public class DefectControlActivity extends BaseActivity implements OnPageChangeListener, OnFunctionButtonClickListener,
        com.cnksi.bdzinspection.fragment.defectcontrol.TrackDefectFragment.OnFunctionButtonClickListener,
        com.cnksi.bdzinspection.fragment.defectcontrol.RecordDefectFragment.OnFunctionButtonClickListener {

    public boolean isDefectChanged = false;
    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = null;
    private String[] titleArray = null;


    /**
     * 是否显示缺陷原因的dialog
     */
    private boolean isShowDefectReason = false;
    /**
     * 是否跟踪缺陷
     */
    private boolean isTrackDefect = false;
    /**
     * 跟踪缺陷的id
     */
    private String mTrackDefectRecordId = "";
    /**
     * 是否是从电池组跳转过来的
     */
    private boolean isFromBattery = false;
    /**
     * 当前选中的页
     */
    private int mCurrentPagePosition = 0;
    private XsActivityDefectControlBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity,R.layout.xs_activity_defect_control);
        
        initialUI();
        initOnClick();
    }


    private void initialUI() {
        getIntentValue();
         binding.includeTitle.tvTitle.setText(R.string.xs_defect_control_str);
        isFromBattery = getIntent().getBooleanExtra(Config.IS_FROM_BATTERY, false);
        titleArray = getResources().getStringArray(R.array.XS_DefectControlArray);
        isShowDefectReason = getIntent().getBooleanExtra(Config.IS_SHOW_DEFECT_REASON, false);
        isTrackDefect = getIntent().getBooleanExtra(Config.IS_TRACK_DEFECT, false);
        mTrackDefectRecordId = getIntent().getStringExtra(Config.TRACK_DEFECT_RECORD_ID);

        initFragmentList();
    }

    private void initFragmentList() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }

        // 记录缺陷
        RecordDefectFragment mFragment = new RecordDefectFragment();
        mFragment.setOnFunctionButtonClickListener(this);
        Bundle args = getBundle();
        args.putBoolean(Config.ADD_NEW_DEFECT_RECORD, getIntent().getBooleanExtra(Config.ADD_NEW_DEFECT_RECORD, false));
        args.putBoolean(Config.IS_SHOW_DEVICE_WIDGET, getIntent().getBooleanExtra(Config.IS_SHOW_DEVICE_WIDGET, true));
        args.putBoolean(Config.IS_NEED_SEARCH_DEFECT_REASON, getIntent().getBooleanExtra(Config.IS_NEED_SEARCH_DEFECT_REASON, false));
        args.putString(Config.CURRENT_FUNCTION_MODEL, Config.RECORD_DEFECT_MODEL);
        mFragment.setArguments(args);
        mFragmentList.add(mFragment);

        // 跟踪缺陷
        TrackDefectFragment mTrackFragment = new TrackDefectFragment();
        mTrackFragment.setOnFunctionButtonClickListener(this);
        args = getBundle();
        args.putString(Config.TRACK_DEFECT_RECORD_ID, mTrackDefectRecordId);
        args.putString(Config.CURRENT_FUNCTION_MODEL, Config.TRACK_DEFECT_MODEL);
        mTrackFragment.setArguments(args);
        mFragmentList.add(mTrackFragment);

        // 消除缺陷
        EliminateDefectFragment mEliminateFragment = new EliminateDefectFragment();
        mEliminateFragment.setOnFunctionButtonClickListener(this);
        args = getBundle();
        args.putString(Config.CURRENT_FUNCTION_MODEL, Config.ELIMINATE_DEFECT_MODEL);
        mEliminateFragment.setArguments(args);
        mFragmentList.add(mEliminateFragment);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, Arrays.asList(titleArray));
         binding.viewPager.setAdapter(fragmentPagerAdapter);
         binding.tabStrip.setViewPager( binding.viewPager);
         binding.tabStrip.setOnPageChangeListener(this);
        setPagerTabStripValue( binding.tabStrip);
         binding.viewPager.setOffscreenPageLimit(3);
        if (isTrackDefect) {
             binding.viewPager.setCurrentItem(1, false);
        }
    }

    /**
     * 传递参数
     */
    private Bundle getBundle() {
        Bundle args = new Bundle();
        args.putBoolean(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
        args.putString(Config.CURRENT_DEVICE_ID, currentDeviceId);
        args.putString(Config.CURRENT_DEVICE_NAME, currentDeviceName);
        args.putString(Config.CURRENT_DEVICE_PART_ID, currentDevicePartId);
        args.putString(Config.CURRENT_DEVICE_PART_NAME, currentDevicePartName);
        args.putString(Config.CURRENT_SPACING_ID, currentSpacingId);
        args.putString(Config.CURRENT_SPACING_NAME, currentSpacingName);
        args.putBoolean(Config.IS_SHOW_DEFECT_REASON, isShowDefectReason);
        args.putString(Config.CURRENT_STANDARD_ID, currentStandardId); // 传递巡视标准ID到Fregement中
        args.putBoolean(Config.IS_FROM_BATTERY, isFromBattery);
        args.putBoolean(Config.IS_DEVICE_PART, getIntent().getBooleanExtra(Config.IS_DEVICE_PART, false));
        return args;
    }

    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> DefectControlActivity.this.onBackPressed());
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPagePosition = position;
    }

    @Override
    public void onFunctionClick(View view, DefectRecord mDefect) {
        PlaySound.getIntance(mActivity).play(R.raw.control_click);
        int i = view.getId();
        if (i == R.id.tv_track_defect) {
            binding.viewPager.setCurrentItem(1, false);
            Fragment mFragment = mFragmentList.get(1);
            if (mFragment instanceof TrackDefectFragment) {
                ((TrackDefectFragment) mFragment).setCurrentSelectedPosition(mDefect);
            }


        } else if (i == R.id.tv_eliminate_defect) {
            Fragment mFragment;
            binding.viewPager.setCurrentItem(2, false);
            mFragment = mFragmentList.get(2);
            if (mFragment instanceof EliminateDefectFragment) {
                ((EliminateDefectFragment) mFragment).setCurrentSelectedPosition(mDefect);
            }


        } else if (i == R.id.tv_report_defect) {
            Intent intent = new Intent(mActivity, ReportToLeaderActivity.class);
            intent.putExtra(Config.CURRENT_DEVICE_NAME, mDefect.devcie);
            intent.putExtra(DefectRecord.DESCRIPTION, mDefect.description);
            intent.putExtra(DefectRecord.DEFECTLEVEL, mDefect.defectlevel);
            startActivity(intent);

        } else {
        }
    }

    @Override
    public void onBackPressed() {
//		if (isDefectChanged) {
        setResult(RESULT_OK);

//		}
        if (KeyBoardUtils.getInputMethodStatus(mActivity)) {
            KeyBoardUtils.closeKeybord(mActivity);
        }
        super.onBackPressed();
    }

    @Override
    protected void onRefresh(Message msg) {

    }

    // 只要是对缺陷进行了操作 就需要更新三个界面中的缺陷数量以及状态
    @Override
    public void onDefectChanged(String speakContent) {
        TTSUtils.getInstance().startSpeaking(speakContent);
        isDefectChanged = true;
        for (int i = 0, count = mFragmentList.size(); i < count; i++) {
            if (mCurrentPagePosition != i) {
                Fragment fragment = mFragmentList.get(i);
                if (fragment instanceof TrackDefectFragment) {
                    ((TrackDefectFragment) fragment).searchData();
                } else if (fragment instanceof EliminateDefectFragment) {
                    ((EliminateDefectFragment) fragment).searchData();
                } else if (fragment instanceof RecordDefectFragment) {
                    ((RecordDefectFragment) fragment).searchData();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            Fragment fragment = mFragmentList.get(mCurrentPagePosition);
            fragment.onActivityResult(requestCode, RESULT_OK, data);
        }
    }

    @Override
    public void drawCircle(String pictureName, String pictureContent) {
        Intent intent = new Intent(mActivity, DrawCircleImageActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_NAME, pictureName);
        intent.putExtra(Config.PICTURE_CONTENT, pictureContent);
        startActivityForResult(intent, LOAD_DATA);
    }

    @Override
    public void takePicture(String pictureName, String folder, int requestCode) {
        FunctionUtil.takePicture(DefectControlActivity.this, pictureName, folder, requestCode);
    }
}