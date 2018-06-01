package com.cnksi.sjjc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sjjc.bean.AppVersion;
import com.cnksi.sjjc.databinding.DialogCopyTipsBinding;
import com.cnksi.sjjc.util.AppUtils;
import com.cnksi.sjjc.util.FunctionUtils;
import com.cnksi.sjjc.util.UpdateUtils;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import java.io.File;
import java.util.ArrayList;

import static com.cnksi.sjjc.activity.LoginActivity.SHOW_UPDATE_LOG_DIALOG;

/**
 * @author luoxy
 * @version 1.0
 * @date 16/4/23
 */
public abstract class BaseSjjcActivity extends BaseTitleActivity {


    /**
     * 显示大图片
     *
     * @param position
     */
    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList,
                                 boolean isShowDelete, boolean isDeleteFile) {
        ImageDetailsActivity.with(context).setPosition(position).setImageUrlList(mImageUrlList).setDeleteFile(isDeleteFile).setShowDelete(isShowDelete).start();
    }


    /**
     * 可以标记图片
     */

    public void drawCircle(String pictureName, String pictureContent) {
        DrawCircleImageActivity.with(mActivity).setTxtContent(pictureContent).setPath(pictureName).start();
    }


    private boolean isPms = false;

    /**
     * 更新日志
     */
    protected String updateContent;

    protected void checkUpdateVersion(final String downloadFolder, String downloadFileName, boolean isPms, String updateContent) {
        this.updateContent = updateContent;
        this.isPms = isPms;
        checkUpdateVersion(downloadFolder, downloadFileName, FunctionUtils.getMetaValue(mActivity, Config.PROGRAM_APP_CODE));
    }

    /**
     * 检测更新
     */
    File localUpdateFile;

    @Override
    protected void checkUpdateVersion(final String downloadFolder, String downloadFileName, final String appCode) {
        ExecutorManager.executeTaskSerially(() -> {
            if (localUpdateFile == null) {
                localUpdateFile = UpdateUtils.hasUpdateApk(mActivity, downloadFolder, isPms);
                if (localUpdateFile != null) {
                    CustomerDialog.dismissProgress();
                    mHandler.sendEmptyMessage(Config.INSTALL_APP_CODE);
                } else if (null == localUpdateFile) {
                    CustomerDialog.dismissProgress();
                    return;
                }
            } else {
                CustomerDialog.dismissProgress();
                mHandler.sendEmptyMessage(Config.INSTALL_APP_CODE);
            }
        });
    }


    /**
     * 刷新数据
     *
     * @param msg
     */
    @Override
    protected void onRefresh(android.os.Message msg) {
        switch (msg.what) {
            case Config.INSTALL_APP_CODE:
                // TODO:显示安装对话框
                UpdateUtils.showInstallNewApkDialog(mActivity, localUpdateFile, isPms, updateContent);
                break;
            case SHOW_UPDATE_LOG_DIALOG:
                if (null != updateLogDialog && null != remoteSjjcAppVersion) {
                    layout.tvDialogTitle.setText("本次更新内容");
                    layout.clickLinearlayout.setVisibility(View.GONE);
                    layout.tvCopy.setVisibility(View.GONE);
                    layout.tvTips.setText(Html.fromHtml(TextUtils.isEmpty(remoteSjjcAppVersion.description) ? "欢迎使用！" : remoteSjjcAppVersion.description));
                    updateLogDialog.show();
                }
                break;
            default:
        }
    }

    private Dialog updateLogDialog;
    private AppVersion remoteSjjcAppVersion;
    private DialogCopyTipsBinding layout;

    /**
     * 检测升级
     */
    public void checkUpdate() {
        layout = DialogCopyTipsBinding.inflate(getLayoutInflater());
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        updateLogDialog = DialogUtils.creatDialog(mActivity, layout.getRoot(), dialogWidth, dialogHeight);
        PackageInfo info = AppUtils.getLocalPackageInfo(getApplicationContext());
        int version = info.versionCode;
        try {
            remoteSjjcAppVersion = BaseService.getInstance(AppVersion.class).selector().expr(" and version_code > '" + version + "'").expr("and file_name like '%sjjc%'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (remoteSjjcAppVersion != null) {
            com.cnksi.sjjc.view.CustomerDialog.showProgress(mActivity, "检测到需要升级，请等待");
            ExecutorManager.executeTaskSerially(() -> {
                try {
                    remoteSjjcAppVersion = BaseService.getInstance(AppVersion.class).selector().expr(" and version_code > '" + version + "'").expr("and file_name like '%sjjc%'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
                    String apkPath = "";
                    //下载APK文件夹
                    SqlInfo info1 = new SqlInfo("select short_name_pinyin from city");
                    try {
                        DbModel model =BaseService.getInstance(AppVersion.class).findDbModelFirst(info1);
                        if (model != null) {
                            apkPath = "admin/" + model.getString("short_name_pinyin") + "/apk";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != remoteSjjcAppVersion) {
                        checkUpdateVersion(Config.BDZ_INSPECTION_FOLDER + apkPath,
                                Config.PCODE, false, TextUtils.isEmpty(remoteSjjcAppVersion.description) ? "修复bug,优化流畅度" : remoteSjjcAppVersion.description);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
