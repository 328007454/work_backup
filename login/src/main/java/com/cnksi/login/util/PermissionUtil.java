package com.cnksi.login.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.login.inter.GrantPermissionListener;
import com.cnksi.login.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.PermissionBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * 权限管理工具
 *
 * @version 1.0
 * @author lyndon
 * @date 2016/11/10 18:15
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

public class PermissionUtil {

    public static final int REQUEST_PERMISSIONS_CODE = 0x100;

    private static PermissionUtil instance;

    private GrantPermissionListener grantPermissionListener;

    public static PermissionUtil getInstance() {
        if (null == instance) {
            instance = new PermissionUtil();
        }
        return instance;
    }

    public PermissionUtil setGrantPermissionListener(GrantPermissionListener grantPermissionListener) {
        this.grantPermissionListener = grantPermissionListener;
        return this;
    }

    /**
     * 权限检测
     *
     * @param activity    检测界面
     * @param permissions 检测权限
     */
    public void checkPermissions(Activity activity, String... permissions) {
        //当android版本大于23(M),才进行验证
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //默认所有验证都通过
            boolean allGranted = true;
            for (String permission : permissions) {
                if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(activity, permission)) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                activity.requestPermissions(permissions, REQUEST_PERMISSIONS_CODE);
                return;
            } else {
                grantPermissionListener.allPermissionsGranted();
                instance =null;
            }
        } else {
            grantPermissionListener.allPermissionsGranted();
            instance =null;
        }

    }

    /**
     * 申请权限回调方法
     *
     * @param activity     申请界面
     * @param requestCode  申请码
     * @param permissions  申请权限名称
     * @param grantResults 申请结果
     */
    public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_PERMISSIONS_CODE == requestCode) {
            String[] deniedPermissions = checkDeniedPermissions(permissions, grantResults);
            if (deniedPermissions.length > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //提示dialog
//                    showPermissionDeniedDialog(activity, deniedPermissions);
                    activity.requestPermissions(deniedPermissions, requestCode);
                } else {
                    grantPermissionListener.allPermissionsGranted();
                    instance =null;
                }
            } else {
                grantPermissionListener.allPermissionsGranted();
                instance =null;
            }
        }
    }

    /**
     * 显示权限拒绝dialog
     *
     * @param activity
     * @param deniedPermissions
     */
    private void showPermissionDeniedDialog(final Activity activity, final String[] deniedPermissions) {
        int dialogWidth = ScreenUtils.getScreenWidth(activity) * 7 / 9;
        PermissionBinding permissionBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.permission_dialog_tips, null, false);
        final Dialog permissionDialog = DialogUtils.creatDialog(activity, permissionBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        permissionBinding.tvDialogContent.setText("权限不足");
        permissionBinding.btnCancel.setText("退出程序");
        permissionBinding.btnSure.setText("重新授权");
        permissionBinding.btnSure.setOnClickListener(v -> {
            if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
                permissionDialog.dismiss();
                activity.requestPermissions(deniedPermissions, REQUEST_PERMISSIONS_CODE);
            }
        });
        permissionBinding.btnCancel.setOnClickListener(v -> {
//                退出系统
            permissionDialog.dismiss();
            CustomApplication.getInstance().exitApplication();
        });
        permissionDialog.show();
        permissionDialog.setCanceledOnTouchOutside(false);

    }


    /**
     * 检测拒绝权限
     *
     * @param permissions
     * @param grantResults
     * @return
     */
    public String[] checkDeniedPermissions(@NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0, count = grantResults.length; i < count; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions.toArray(new String[]{});
    }
}
