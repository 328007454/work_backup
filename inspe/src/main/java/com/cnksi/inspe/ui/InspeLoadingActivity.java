package com.cnksi.inspe.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;

/**
 * App启动页面，其他模块启动可以参考本页面启动主模块方式
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 13:40
 */
public class InspeLoadingActivity extends AppBaseActivity {


    private String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET"};

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_loading;
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {


//        new TeamService().saveRuleResult(new TeamRuleEntity("id", "name", "level", "type", "score_group_id", "pid", "dependence", "dept_name", "workshop", "team", 1, "stand_content", System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis(), "gzrwid", "sort", "level_namel", "chengdu", System.currentTimeMillis()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermission(permissions)) {
            startAppSettings();
        } else {
            try {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(getPackageName(), "com.cnksi.inspe.ui.InspeMainActivity");
                intent.setComponent(componentName);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                showToast("模块暂未开放!");
            }

//            ARouter.getInstance().build("/inspe/main").navigation(this, new NavCallback() {
//                @Override
//                public void onArrival(Postcard postcard) {
//                    finish();
//                }
//            });
        }
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private boolean checkPermission(String[] permissions) {
        for (String permission : permissions) {
            boolean isPer = ContextCompat.checkSelfPermission(InspeLoadingActivity.this, permission) == PackageManager.PERMISSION_DENIED;
            if (isPer) {
                return false;
            }

        }


        return true;
    }
}

