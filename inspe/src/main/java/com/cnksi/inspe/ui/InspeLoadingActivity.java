package com.cnksi.inspe.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeLoadingBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.utils.PermissionUtil;

import java.util.List;

/**
 * App启动页面，其他模块启动可以参考本页面启动主模块方式
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 13:40
 */
public class InspeLoadingActivity extends AppBaseActivity implements PermissionUtil.GrantPermissionListener, View.OnClickListener {


    private String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET", "android.permission.CAMERA"};
    private ActivityInspeLoadingBinding dataBinding;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_loading;
    }

    @Override
    public void initUI() {
        setTitle("启动页面");
        dataBinding = (ActivityInspeLoadingBinding) rootDataBinding;
        dataBinding.directorBtn.setOnClickListener(this);
        dataBinding.teamleaderBtn.setOnClickListener(this);
        dataBinding.trackerBtn.setOnClickListener(this);
        dataBinding.clearIssueBtn.setOnClickListener(this);
        dataBinding.checkUserBtn.setOnClickListener(this);

    }


    @Override
    public void initData() {

//        new TeamService().saveRuleResult(new TeamRuleEntity("id", "name", "level", "type", "score_group_id", "pid", "dependence", "dept_name", "workshop", "team", 1, "stand_content", System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis(), "gzrwid", "sort", "level_namel", "chengdu", System.currentTimeMillis()));
        PermissionUtil.getInstance().setGrantPermissionListener(this).checkPermissions(this, permissions);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (!checkPermission(permissions)) {
//            startAppSettings();
//        } else {
//
//        }
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

    private Handler handler = new Handler();

    private void startMain(String id[], String accect[]) {
        handler.removeCallbacksAndMessages(null);
        try {
            Intent intent = new Intent();
            if (id != null) {
                intent.putExtra("userid_array", id);//①传递登录用户ID
            }
            if (accect != null) {
                intent.putExtra("username_array", accect);//②传递登录用户用户名,①②任选一种即可
            }

            //②传递登录用户用户名,①②任选一种即可
            ComponentName componentName = new ComponentName(getPackageName(), "com.cnksi.inspe.ui.InspeMainActivity");
            intent.setComponent(componentName);
            startActivity(intent);
        } catch (Exception e) {
            showToast("启动页面失败!");
        }
    }

    /**
     * 根据不同角色启动
     *
     * @param roleType
     */
    private void startMain(RoleType roleType) {
        handler.removeCallbacksAndMessages(null);
        switch (roleType) {
            case director:
            case specialty:
                startMain(null, new String[]{"test2"});
                break;
            case team_leader:
                startMain(null, new String[]{"test3"});
                break;
            case tracker:
                startMain(null, new String[]{"test4"});
                break;
        }

    }

    @Override
    public void allPermissionsGranted() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMain(RoleType.director);
            }
        }, 3000);

        //Arouter
//            ARouter.getInstance().build("/inspe/main").navigation(this, new NavCallback() {
//                @Override
//                public void onArrival(Postcard postcard) {
//                    finish();
//                }
//            });
    }

    private TeamService teamService = new TeamService();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.directorBtn) {
            startMain(RoleType.director);
        } else if (v.getId() == R.id.teamleaderBtn) {
            startMain(RoleType.team_leader);
        } else if (v.getId() == R.id.trackerBtn) {
            startMain(RoleType.tracker);
        } else if (v.getId() == R.id.clearIssueBtn) {
            //清除任务
            teamService.clear(TeamRuleResultEntity.class);
            //恢复任务为未开始状态
            List<InspecteTaskEntity> list = teamService.getTaskList();
            for (InspecteTaskEntity task : list) {
                task.setProgress(TaskProgressType.doing.name());
                teamService.saveTask(task);
            }
        } else if (v.getId() == R.id.checkUserBtn) {
            checkUserLogin();
        }
    }

    private void checkUserLogin() {

        final List<UserEntity> userList = getUserService().getUsers(null, null, null);
        String[] userArray = new String[userList.size()];

        for (int i = 0, length = userArray.length; i < length; i++) {
            userArray[i] = userList.get(i).getUsername();
        }
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("选择整改人员")
                .setSingleChoiceItems(userArray, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startMain(null, new String[]{userList.get(which).getAccount()});
                    }
                }).create();
        dialog.show();
    }
}

