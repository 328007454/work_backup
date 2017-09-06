package com.cnksi.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.core.R;
import com.cnksi.core.adapter.DividerItemDecoration;
import com.cnksi.core.application.CoreApplication;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.common.UpdateInfor;
import com.cnksi.core.okhttp.OkHttpUtils;
import com.cnksi.core.okhttp.callback.Callback;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.NetWorkUtil;
import com.cnksi.core.utils.PermissionsChecker;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.UpdateUtils;
import com.cnksi.core.view.CustomerDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Oliver on 2015/11/24.
 */
public abstract class BaseCoreActivity extends AppCompatActivity {

    /**
     * 退出时间
     */
    private static long currentBackPressedTime = 0;
    /**
     * 当前的Activity
     */
    protected BaseCoreActivity mCurrentActivity = null;
    /**
     * fragment管理器
     */
    protected FragmentManager mFragmentManager = getSupportFragmentManager();
    /**
     * 线程池
     */
    protected ExecutorService mExcutorService = CoreApplication.getExcutorService();
    /**
     * 升级的apk文件
     */
    protected File mUpdateFile = null;
    /**
     * 下载文件存放目录
     */
    private String mDownloadFolder = "";
    /**
     * 下载文件名称
     */
    private String mDownloadFile = "";
    /**
     * 自定义Handler
     */
    protected CustomerHanlder mHandler = null;
    /**
     * 权限检查
     */
    protected boolean isCheckPermission = false;

    /**
     * 更新日志
     */
    protected String updateContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new CustomerHanlder(mCurrentActivity = this);
        ScreenManager.getInstance().pushActivity(mCurrentActivity);
    }

    /**
     * 刷新数据
     *
     * @param msg
     */
    protected void onRefresh(android.os.Message msg) {
        switch (msg.what) {
            case CoreConfig.NETWORK_UNVISIBLE:
                CustomerDialog.dismissProgress();
//                CToast.showLong(mCurrentActivity, R.string.network_unvisible_str);
                break;
            case CoreConfig.UPDATE_APP_CODE:

                if (msg.obj != null && msg.obj instanceof UpdateInfor) {
                    UpdateUtils.showUpdateDialog(mCurrentActivity, CoreConfig.SERVER_URL + File.separator + ((UpdateInfor) msg.obj).file, ((UpdateInfor) msg.obj).remark, mDownloadFolder, mDownloadFile);
                }
                break;
            case CoreConfig.INSTALL_APP_CODE:
                // TODO:显示安装对话框
//                UpdateUtils.showInstallNewApkDialog(mCurrentActivity, mUpdateFile);
                UpdateUtils.showInstallNewApkDialog(mCurrentActivity, mUpdateFile, isPms, updateContent);
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    protected void onDestroy() {
        CustomerDialog.dismissProgress();
        ScreenManager.getInstance().popActivity(mCurrentActivity);
        mHandler.removeCallbacks(null);
        super.onDestroy();
    }


    /**
     * 退出应用
     */
    protected void exitSystem() {
        exitSystem(false);
    }

    /**
     * 退出应用 true 完全退出 false 结束当前页面
     *
     * @param isExitSystem
     */
    protected void exitSystem(boolean isExitSystem) {
        if (System.currentTimeMillis() - currentBackPressedTime > 2000) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(mCurrentActivity, R.string.one_more_click_exit_str, Toast.LENGTH_SHORT).show();
        } else {
            if (isExitSystem) {
                compeletlyExitSystem();
            } else {
                this.finish();
            }
        }
    }

    /**
     * 完全退出应用
     */
    protected void compeletlyExitSystem() {
        // 退出
        ScreenManager.getInstance().popAllActivityExceptOne(null);
        android.os.Process.killProcess(android.os.Process.myPid());
        CoreApplication.getExcutorService().shutdownNow();
        System.exit(0);
    }

    /**
     * 自定义Handler
     */
    public static class CustomerHanlder extends Handler {
        WeakReference<BaseCoreActivity> mActivity;

        public CustomerHanlder(BaseCoreActivity activity) {
            mActivity = new WeakReference<BaseCoreActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseCoreActivity mCurrentActivity = mActivity.get();
            if (mCurrentActivity != null) {
                mCurrentActivity.onRefresh(msg);
            }
        }
    }



    protected String mStartUUID = "";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PermissionActivity.REQUEST_PERMISSION_CODE && resultCode == PermissionActivity.PERMISSIONS_DENIED) {
                // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
                compeletlyExitSystem();
            } else if (requestCode == PermissionActivity.REQUEST_PERMISSION_CODE && resultCode == PermissionActivity.PERMISSIONS_GRANTED) {
                isCheckPermission = true;
                //获得权限后的操作
                afterGrantedPermissions();
            }
        }
    }



    private boolean isPms = false;

    protected void checkUpdateVersion(final String downloadFolder, String downloadFileName, boolean isPms, String updateContent) {
        this.updateContent = updateContent;
        this.isPms = isPms;
        checkUpdateVersion(downloadFolder, downloadFileName, FunctionUtils.getMetaValue(mCurrentActivity, CoreConfig.PROGRAM_APP_CODE));
    }

    /**
     * 检测更新
     */
    protected void checkUpdateVersion(final String downloadFolder, String downloadFileName, final String appCode) {
        mDownloadFolder = downloadFolder;
        mDownloadFile = downloadFileName;
        mExcutorService.execute(new Runnable() {

            @Override
            public void run() {
                if (mUpdateFile == null) {
                    mUpdateFile = UpdateUtils.hasUpdateApk(mCurrentActivity, downloadFolder,isPms);
                    if (mUpdateFile != null) {
//                        if (AppUtils.verSignature(mCurrentActivity, mUpdateFile.getAbsolutePath())) {
                        mHandler.sendEmptyMessage(CoreConfig.INSTALL_APP_CODE);
//                        }
                    } else if (null == mUpdateFile) {
                        return;
                    } else {
                        if (NetWorkUtil.isNetworkConnected(mCurrentActivity)) {
                            // 上传用户信息 检查升级
                            Map<String, String> params = UpdateUtils.getDeviceInforMapParams(mCurrentActivity, appCode);
                            OkHttpUtils.post().url(CoreConfig.UPDATE_URL).params(params).tag(mCurrentActivity).build().execute(new Callback<String>() {

                                @Override
                                public String parseNetworkResponse(Response response) throws Exception {
                                    String result = response.body().string();
                                    // 解析升级数据
                                    UpdateUtils.resolveData(mCurrentActivity, mHandler, result, CoreConfig.UPDATE_APP_CODE);
                                    return result;
                                }

                                @Override
                                public void onError(Call call, Exception e) {
                                }

                                @Override
                                public void onResponse(String response) {
                                }
                            });
                        }
                    }
                } else {
                    mHandler.sendEmptyMessage(CoreConfig.INSTALL_APP_CODE);
                }
            }
        });
    }

    /**
     * 创建文件夹
     *
     * @param filePathArray
     */
    protected void initFileSystem(final String[] filePathArray) {
        FileUtils.initFile(filePathArray);
    }


    /**
     * 设置RecyclerView 的样式
     *
     * @param mRecyclerView
     * @param orientation   LinearLayoutManager.HORIZONTAL/VERTICAL
     */
    protected void setRecyclerViewStyle(RecyclerView mRecyclerView, int orientation) {
        setRecyclerViewStyle(mRecyclerView, orientation, true);
    }

    /**
     * 设置RecyclerView 的样式
     *
     * @param mRecyclerView
     * @param orientation   LinearLayoutManager.HORIZONTAL/VERTICAL
     * @param isAddDivider  是否加上分割线
     */
    protected void setRecyclerViewStyle(RecyclerView mRecyclerView, int orientation, boolean isAddDivider) {
        //添加布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(orientation);
        mRecyclerView.setLayoutManager(layoutManager);
        if (isAddDivider) {
            //添加分割线
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, orientation));
        }
    }


    /**
     * 设置文本控件值
     *
     * @param resId
     * @param content
     */
    protected void setTextContent(int resId, String content) {
        ((TextView) this.findViewById(resId)).setText(StringUtils.cleanString(content));
    }

    /**
     * 设置文本控件值
     *
     * @param resId
     * @param contentId
     */
    protected void setTextContent(int resId, int contentId) {
        ((TextView) this.findViewById(resId)).setText(contentId);
    }

    protected void checkPermissions(String... PERMISSIONS) {
        // 缺少权限时, 进入权限配置页面
        if (PermissionsChecker.getInstance(mCurrentActivity).lacksPermissions(PERMISSIONS)) {
            isCheckPermission = true;
            PermissionActivity.startActivityForResult(this, PermissionActivity.REQUEST_PERMISSION_CODE, PERMISSIONS);
        } else if (!isCheckPermission) {
            afterGrantedPermissions();
        }
    }

    /**
     * 获得权限后的操作
     */
    protected void afterGrantedPermissions() {

    }
}
