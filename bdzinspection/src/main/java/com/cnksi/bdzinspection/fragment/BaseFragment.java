package com.cnksi.bdzinspection.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.activity.ImageDetailsActivity;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.InspectionType;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.xscore.xsutils.CLog;
import com.cnksi.xscore.xsutils.PreferencesUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

@SuppressLint("HandlerLeak")
public abstract class BaseFragment extends BaseCoreFragment {

    /**
     * 是否可见的标志
     */
    protected boolean isVisible;
    /**
     * 标志位，标志已经初始化完成。
     */
    protected boolean isPrepared = false;
    /**
     * 是否是第一次加载
     */
    protected boolean isFirstLoad = true;

    /**
     * 是否需要重新加载
     */
    public boolean isRepeatLoad = false;

    /**
     * 是否是特殊巡检
     */
    protected boolean isParticularInspection = false;
    /**
     * 当前巡检类型
     */
    protected String currentInspectionType = "";
    /**
     * 当前巡检类型Name
     */
    protected String currentInspectionTypeName = InspectionType.full.toString();

    /**
     * 当前巡检变电站
     */
    protected String currentBdzId = "";

    /**
     * 当前巡视标准
     */
    protected String currentStandardId = "";

    /**
     * 当前的功能选项
     */
    protected String currentFunctionModel = "";
    /**
     * 是否显示缺陷原因
     */
    protected boolean isShowDefectReason = false;
    /**
     * 当前选中的跟踪缺陷
     */
    protected String currentSelectDefectRecordId = "";

    /**
     * 当前设备的id
     */
    protected String currentDeviceId = "";
    /**
     * 当前设备的Name
     */
    protected String currentDeviceName = "";
    /**
     * 当前设备部件id
     */
    protected String currentDevicePartId = "";
    /**
     * 当前设备部件名称
     */
    protected String currentDevicePartName = "";
    /**
     * 当前间隔id
     */
    protected String currentSpacingId = "";
    /**
     * 当前间隔名称
     */
    protected String currentSpacingName = "";

    /**
     * 当前变电站名称
     */
    protected String currentBdzName = "";
    /**
     * 当前的任务id
     */
    protected String currentTaskId = "";
    /**
     * 当前的报告id
     */
    protected String currentReportId = "";

    public String currentAcounts;

    boolean isDetach = false;
    protected Activity currentActivity;

    protected ExecutorService mFixedThreadPoolExecutor = XunshiApplication.getFixedThreadPoolExecutor();

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (isDetach) {
                CLog.e("Activity 已经消失不转发消息");
            } else {
                onRefresh(msg);
            }
        }

        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            if (msg.getCallback() != null && isDetach) {
                CLog.e("Activity 已经消失，不再接收新的消息");
                return true;
            }
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    };

    @Override
    public int getFragmentLayout() {
        return 0;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (currentActivity == null) {
            currentActivity = activity;
        }
        isDetach = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRepeatLoad) {
            isFirstLoad = true;
            lazyLoad();
        }
    }

    @Override
    public void onDetach() {
        isDetach = true;
        super.onDetach();
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 得到Bundle传过来的值
     */
    protected void getBundleValue() {

        isParticularInspection = bundle.getBoolean(Config.IS_PARTICULAR_INSPECTION, false);
        currentStandardId = bundle.getString(Config.CURRENT_STANDARD_ID); // 巡视标准ID
        currentFunctionModel = bundle.getString(Config.CURRENT_FUNCTION_MODEL);
        isShowDefectReason = bundle.getBoolean(Config.IS_SHOW_DEFECT_REASON, false);
        currentDeviceId = bundle.getString(Config.CURRENT_DEVICE_ID);
        currentDeviceName = bundle.getString(Config.CURRENT_DEVICE_NAME);
        currentDevicePartId = bundle.getString(Config.CURRENT_DEVICE_PART_ID);
        currentDevicePartName = bundle.getString(Config.CURRENT_DEVICE_PART_NAME);
        currentSpacingId = bundle.getString(Config.CURRENT_SPACING_ID);
        currentSpacingName = bundle.getString(Config.CURRENT_SPACING_NAME);

        currentBdzId = PreferencesUtils.getString(currentActivity, Config.CURRENT_BDZ_ID, "");
        currentBdzName = PreferencesUtils.getString(currentActivity, Config.CURRENT_BDZ_NAME, "");
        currentInspectionTypeName = PreferencesUtils.getString(currentActivity, Config.CURRENT_INSPECTION_TYPE_NAME, "");
        currentInspectionType = PreferencesUtils.getString(currentActivity, Config.CURRENT_INSPECTION_TYPE, "");
        currentTaskId = PreferencesUtils.getString(currentActivity, Config.CURRENT_TASK_ID, "");
        currentReportId = PreferencesUtils.getString(currentActivity, Config.CURRENT_REPORT_ID, "");
        currentAcounts = PreferencesUtils.getString(currentActivity, Config.CURRENT_LOGIN_ACCOUNT, "");
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见的时候
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 延迟加载
     */
    protected abstract void lazyLoad();

    /**
     * 不可见时
     */
    protected void onInvisible() {
    }

    /**
     * 刷新数据
     *
     * @param msg
     */
    protected void onRefresh(android.os.Message msg) {

    }

    /**
     * 初始化图片加载器
     *
     * @param context
     */
    public void initBitmapUtils(Context context) {

    }

    /**
     * 显示大图片
     *
     * @param position
     */
    public void showImageDetails(Fragment context, int position, ArrayList<String> mImageUrlList, boolean isShowDelete) {
        Intent intent = new Intent(context.getActivity(), ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
        if (mImageUrlList != null) {
            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
        }
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
        context.getActivity().startActivityForResult(intent, com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE);
    }

    /**
     * 显示大图片
     */
    public void showImageDetails(Fragment context, ArrayList<String> mImageUrlList) {
        showImageDetails(context, 0, mImageUrlList, false);
    }

    public void showImageDetails(Fragment context, ArrayList<String> mImageUrlList, boolean isShowDelete) {
        showImageDetails(context, 0, mImageUrlList, isShowDelete);
    }

    /**
     * 显示大图片
     */
    public void showImageDetails(Fragment context, String mImageUrl, boolean isShowDelete) {
        ArrayList<String> mImageUrlList = new ArrayList<String>();
        mImageUrlList.add(mImageUrl);
        showImageDetails(context, 0, mImageUrlList, isShowDelete);
    }

    /**
     * 显示大图片
     */
    public void showImageDetails(Fragment context, String mImageUrl) {
        ArrayList<String> mImageUrlList = new ArrayList<String>();
        mImageUrlList.add(mImageUrl);
        showImageDetails(context, 0, mImageUrlList, false);
    }


}
