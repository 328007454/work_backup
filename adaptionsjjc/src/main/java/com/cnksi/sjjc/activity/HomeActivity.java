package com.cnksi.sjjc.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.cnksi.sjjc.databinding.ActivityHomePageBinding;


/**
 * Created by han on 2017/3/24.
 */

public class HomeActivity extends BaseActivity {
    ActivityHomePageBinding homePageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePageBinding = ActivityHomePageBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setContentView(homePageBinding.getRoot());
        initUI();
    }

    private void initUI() {
        homePageBinding.setTypeClick(this);
    }

//    public void goToInspectionPage(int id) {
//        Intent intent = null;
//        switch (id) {
//            case R.id.device_xunshi:
//            case R.id.device_weihu:
//                intent = new Intent(_this, NewLauncherActivity.class);
//                intent.putExtra("position", id == R.id.device_xunshi ? 0 : 1);
//                startActivity(intent);
//                break;
//            case R.id.device_operate:
//                ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.OperateTaskListActivity");
//                Intent intent2 = new Intent();
//                intent2.setComponent(componentName);
//                intent2.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_USER, ""));
//                intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_ACCOUNT, ""));
//                break;
//            case R.id.device_unify:
//                break;
//            case R.id.device_defect:
//                break;
//            case R.id.device_copy:
//                break;
//            case R.id.device_tjwt:
//                break;
//            case R.id.device_sycn:
//                break;
//            default:
//                break;
//        }
//    }
}
