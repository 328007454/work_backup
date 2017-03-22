package com.cnksi.sjjc.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Users;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.UserService;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.OnViewClickListener;
import com.iflytek.cloud.SpeechSynthesizer;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * launcher界面
 */
public class LauncherActivity extends BaseActivity {
    @ViewInject(R.id.ibtn_exit)
    private ImageButton exitProject;
    private boolean isFromThirdApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_launcher);
        x.view().inject(_this);
        if (null == (mTts = SpeechSynthesizer.getSynthesizer())) {
            initSpeech(this);
        }
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String account = bundle.getString("u_account", "");
            String password = bundle.getString("u_password", "");
            Users user = UserService.getInstance().findUserByNameAndPwd(account, password);
            if (null != user) {
                PreferencesUtils.put(this, Config.CURRENT_LOGIN_USER, user.username);
                PreferencesUtils.put(this, Config.CURRENT_LOGIN_ACCOUNT, user.account);
                //保存登录班组和账号
                PreferencesUtils.put(this, Config.CURRENT_DEPARTMENT_ID, user.dept_id);
                isFromThirdApp = true;
            } else {
                CToast.showLong(this, "用户名或者密码错误");
                finish();
            }

        }

        mTts.startSpeaking(String.format("欢迎使用%1$s", getString(R.string.app_name)), null);

        tvTitle.setText(R.string.app_name);
        exitProject.setImageResource(R.drawable.exit_button_background);
        exitProject.setVisibility(View.VISIBLE);

        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DeviceService.getInstance().refreshDeviceHasCopy();
            }
        });
    }

    //
    @Event(value = {R.id.sbxs, R.id.sbjc, R.id.dqsy, R.id.dqwh, R.id.ywyth, R.id.dzhgzp, R.id.zxxs, R.id.sbys, R.id.jyhys, R.id.jyhpj, R.id.gzp, R.id.ibtn_exit, R.id.btn_back, R.id.layout_data_sync,R.id.tjwt})
    private void onClick(View view) {
        Intent intent = new Intent(this, TypeListActivity.class);
        switch (view.getId()) {
            //设备巡视
            case R.id.sbxs:
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBXS.name());
                break;
            //设备检测
            case R.id.sbjc:
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC.name());
                break;
            //定期试验
            case R.id.dqsy:
//                intent.putExtra(Config.CURRENT_INSPECTION_VALUE, InspectionType.switchover.value);
                CustomApplication.closeDbConnection();
                Intent intent4 = new Intent();
                ComponentName componentName4 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
                String typeName4 = InspectionType.switchover.name();
                intent4.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, typeName4);
                intent4.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_USER, ""));
                intent4.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_ACCOUNT, ""));
                intent4.setComponent(componentName4);
                startActivity(intent4);
                return;
            //定期维护
            case R.id.dqwh:
//                intent.putExtra(Config.CURRENT_INSPECTION_VALUE, InspectionType.maintenance.value);
                CustomApplication.closeDbConnection();
                Intent intent3 = new Intent();
                ComponentName componentName3 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
                String typeName3 = InspectionType.maintenance.name();
                intent3.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, typeName3);
                intent3.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_USER, ""));
                intent3.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_ACCOUNT, ""));
                intent3.setComponent(componentName3);
                startActivity(intent3);
                return;
            //运维一体化
            case R.id.ywyth:
                Intent intent1 = new Intent();
                ComponentName componentName1 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
                String typeName = InspectionType.operation.name();
                intent1.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, typeName);
                intent1.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_USER, ""));
                intent1.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_ACCOUNT, ""));
                intent1.setComponent(componentName1);
                startActivity(intent1);
                return;
            //电子化操作票
            case R.id.dzhgzp:
                ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.OperateTaskListActivity");
                Intent intent2 = new Intent();
                intent2.setComponent(componentName);
                intent2.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_USER, ""));
                intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(this, Config.CURRENT_LOGIN_ACCOUNT, ""));
                startActivity(intent2);
                return;
            //专项巡视
            case R.id.zxxs:
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.exclusive.name());
                break;
            //设备验收
            case R.id.sbys:
                return;
            //精益化验收
            case R.id.jyhys:
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.JYHYS.name());
                break;
            //精益化评价
            case R.id.jyhpj:
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.JYHPJ.name());
                break;
            //工作票
            case R.id.gzp:
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.GZP.name());
                break;
            case R.id.ibtn_exit:
                compeletlyExitSystem();
                return;
            case R.id.btn_back:
                onBackPressed();

                return;
            case R.id.layout_data_sync:
                List<String> upload = new ArrayList<>();
                upload.add("admin/ningxiashizuishan/buqiao");
                startSync();
                return;
            //图解五通
            case R.id.tjwt:
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.TJWT.name());
                break;
            default:
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isFromThirdApp) {
            finish();
        } else {
            DialogUtils.showSureTipsDialog(_this, null, "是否退出登录?", "确定", "取消", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    Intent intent = new Intent(_this, LoginActivity.class);
                    startActivity(intent);
                    LauncherActivity.this.finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent intent = new Intent("com.cnksi.sjjc.service.androidservice.LocatoinService");
//        Intent intent = new Intent();
//        intent.setAction("com.cnksi.sjjc.service.androidservice.LocatoinService");
//        intent.setPackage(getPackageName());
//        startService(intent);
    }
}