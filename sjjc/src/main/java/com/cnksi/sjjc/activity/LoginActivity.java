package com.cnksi.sjjc.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.cnksi.bdloc.LocationUtil;
import com.cnksi.common.CommonApplication;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.model.Users;
import com.cnksi.common.utils.DateCalcUtils;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.ActivityLoginBinding;
import com.cnksi.sjjc.dialog.ModifySyncUrlBinding;
import com.cnksi.sjjc.inter.GrantPermissionListener;
import com.cnksi.common.KSyncConfig;
import com.cnksi.sjjc.util.AESUtil;
import com.cnksi.sjjc.util.AccountUtil;
import com.cnksi.sjjc.util.ActivityUtil;
import com.cnksi.sjjc.util.AppUtils;
import com.cnksi.sjjc.util.PermissionUtil;

import org.xutils.common.util.DatabaseUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;


/**
 * 登录界面
 * @author Wastrel
 */
public class LoginActivity extends BaseSjjcActivity implements GrantPermissionListener {

    private static final String TAG = "LoginActivity";
    public static final int SAME_ACCOUNT = 0x01111;
    //添加账号相同
    public static final int NO_SUCH_USER = SAME_ACCOUNT + 1;
    //没有对应账号
    public static final int PWD_ERROR = NO_SUCH_USER + 1;
    //密码错误
    public static final int USER_ONE_LOGIN_SUCCESS = PWD_ERROR + 1;
    //添加第一个登录人员成功
    public static final int USER_TWO_LOGIN_SUCCESS = USER_ONE_LOGIN_SUCCESS + 1;
    //添加第二个登录人员成功
    public static final int NO_LOGIN_USER = USER_TWO_LOGIN_SUCCESS + 1;
    //没人登录人员
    public static final int USER_LOGIN_SUCCESS = NO_LOGIN_USER + 1;
    //登录成功
    public static final int SHOW_UPDATE_LOG_DIALOG = USER_LOGIN_SUCCESS + 1;
    public static final int USER_COUNT_NOT_ACTIVITE = SHOW_UPDATE_LOG_DIALOG + 1;
    private Users mCurrentUserOne, mCurrentUserTwo;
    private List<String> usersName = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA};
    /**
     * 屏蔽Wifi计数器
     */
    private int count = 0;
    private long startTime1 = 0;
    private boolean isGrantPermission = false;
    private String userOnePassword;
    private String userTwoPassword;

    private ActivityLoginBinding binding;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime1 = System.currentTimeMillis();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        PermissionUtil.getInstance().setGrantPermissionListener(this).checkPermissions(this, permissions);
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void inUI() {
        //设置版本信息
        binding.tvVersion.setText(getString(R.string.sys_copyrights_format_str, AppUtils.getVersionName(mActivity)));
        String userName = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
        if (!TextUtils.isEmpty(userName)) {
            String[] userNames = userName.split(",");
            binding.etAutoUsername.setText(userNames[0]);
            String pwd = PreferencesUtils.get(userNames[0], "");
            if (!DateCalcUtils.timeNormal(pwd)) {
                binding.etPassword.setText(pwd);
            }
        }
        arrayAdapter = new ArrayAdapter<>(mActivity, R.layout.user_name_drop_down_item, R.id.tv_user_name);
        binding.etAutoUsername.setAdapter(arrayAdapter);
        binding.etAutoUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                String pwd = PreferencesUtils.get(s.toString(), "");
                if (!DateCalcUtils.timeNormal(pwd)) {
                    binding.etPassword.setText(pwd);
                }
                ExecutorManager.executeTaskSerially(() -> {
                    usersName = UserService.getInstance().searchUsersName(s.toString());
                    mHandler.sendEmptyMessage(LOAD_DATA);
                });
            }
        });

        findViewById(R.id.ivLogo).setOnLongClickListener(v -> {
            LoginActivity.this.modifySyncURL();
            return true;
        });
    }

    private void initOnClick() {
        binding.bLoginButton.setOnClickListener(view -> LoginActivity.this.loginUser(true));
        binding.ivLogo.setOnClickListener(view -> ActivityUtil.startSync(mActivity));
        binding.tvVersion.setOnClickListener(view -> mHandler.sendEmptyMessage(SHOW_UPDATE_LOG_DIALOG));
        binding.bAddPeopleButton.setOnClickListener(view -> LoginActivity.this.loginUser(false));
        binding.maskWifi.setOnClickListener(view -> {
            if (count == 0) {
                startTime = System.currentTimeMillis();
                count++;
            } else {
                if (System.currentTimeMillis() - startTime < 500) {
                    count++;
                } else {
                    count = 0;
                }
                startTime = System.currentTimeMillis();
            }
            if (count >= 5) {
                boolean maskWifi = !PreferencesUtils.get(Config.MASK_WIFI_SWITCH_KEY, true);
                PreferencesUtils.put(Config.MASK_WIFI_SWITCH_KEY, maskWifi);
                if (maskWifi) {
                    com.cnksi.core.utils.NetWorkUtils.disableNetWork(mActivity);
                    ToastUtils.showMessage("打开WIFI屏蔽");
                } else {
                    ToastUtils.showMessage("关闭WIFI屏蔽");
                }
                count = 0;
            }
        });

        binding.ibDelete1.setOnClickListener(view -> {
            mCurrentUserOne = null;
            binding.ibDelete1.setVisibility(View.INVISIBLE);
            binding.user1Img.setImageResource(R.drawable.ic_portrait_thum);
            binding.txtLogginFirst.setVisibility(View.GONE);
            if (binding.ibDelete2.getVisibility() == View.INVISIBLE) {
                binding.txtUserLayout.setVisibility(View.INVISIBLE);
            } else {
                binding.txtUserLayout.setVisibility(View.VISIBLE);
            }
        });
        binding.ibDelete2.setOnClickListener(view -> {
            mCurrentUserTwo = null;
            binding.ibDelete2.setVisibility(View.INVISIBLE);
            binding.user2Img.setImageResource(R.drawable.ic_portrait_thum);
            binding.txtLogginSecond.setVisibility(View.GONE);
            if (binding.ibDelete1.getVisibility() == View.INVISIBLE) {
                binding.txtUserLayout.setVisibility(View.INVISIBLE);
            } else {
                binding.txtUserLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 显示修改服务器同步地址的对话框，该对话框由长安同步按钮触发
     */
    private void modifySyncURL() {
        final ModifySyncUrlBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_modify_iphost, null, false);
        final Dialog dialog = DialogUtils.createDialog(mActivity, binding, true);
        binding.etNewUrl.setText(Config.SYNC_URL);
        binding.etAppId.setText(Config.SYNC_APP_ID);
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        binding.btnSure.setOnClickListener(v -> {
            String url = LoginActivity.this.getText(binding.etNewUrl).trim();
            if (url.startsWith("http://") || url.startsWith("https://")) {
            } else {
                url = "http://" + url;
            }
            Config.SYNC_URL = url;
            String appId = LoginActivity.this.getText(binding.etAppId);
            if (!TextUtils.isEmpty(appId)) {
                Config.SYNC_APP_ID = appId;
            }
            PreferencesUtils.put(Config.KEY_SYNC_URL, Config.SYNC_URL);
            PreferencesUtils.put(Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
            dialog.dismiss();
        });
        PreferencesUtils.put(Config.KEY_SYNC_URL, Config.SYNC_URL);
        PreferencesUtils.put(Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
        dialog.show();
    }

    /**
     * 登录
     *
     * @param login true 登录系统 false 添加登录人员
     */
    private void loginUser(boolean login) {
        //登录系统
        if (login) {
            if (null != mCurrentUserOne || null != mCurrentUserTwo) {
                mHandler.sendEmptyMessage(USER_LOGIN_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(NO_LOGIN_USER);
            }
            return;
        }
        //添加登录人员
        final String userName = binding.etAutoUsername.getText().toString().trim();
        final String userPwd = binding.etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showMessage(R.string.et_user_hint_str);
            return;
        }
        if (TextUtils.isEmpty(userPwd)) {
            ToastUtils.showMessage(R.string.et_password_hint_str);
            return;
        }
        if (AccountUtil.getUtilInstance().JudgeAccountBlocked(mActivity, binding.etAutoUsername.getText().toString())) {
            return;
        }
        ExecutorManager.executeTaskSerially(() -> {
            //根据用户名和密码查询
            Users tempUser = null;
            try {
                tempUser = UserService.getInstance().findUserByNameAndPwd(userName, AESUtil.encode(userPwd));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //查询用户存在
            if (null != tempUser) {
                //判断与第一个用户是否相同
                if (null != mCurrentUserOne && mCurrentUserOne.account.equals(tempUser.account)) {
                    mHandler.sendEmptyMessage(SAME_ACCOUNT);
                    return;
                }
                //判断与第二个用户是否相同
                if (null != mCurrentUserTwo && mCurrentUserTwo.account.equals(tempUser.account)) {
                    mHandler.sendEmptyMessage(SAME_ACCOUNT);
                    return;
                }
                if (null == mCurrentUserOne) {
                    mCurrentUserOne = tempUser;
                    userOnePassword = binding.etPassword.getText().toString().trim();
                    mHandler.sendEmptyMessage(USER_ONE_LOGIN_SUCCESS);
                } else if (null == mCurrentUserTwo) {
                    mCurrentUserTwo = tempUser;
                    userTwoPassword = binding.etPassword.getText().toString().trim();
                    mHandler.sendEmptyMessage(USER_TWO_LOGIN_SUCCESS);
                }
            }
            //查询用户不存在
            else {
                //根据用户名查询
                tempUser = UserService.getInstance().findUserByAccount(userName);
                if (null != tempUser) {
                    if ("未激活".equalsIgnoreCase(tempUser.pwd)) {
                        mHandler.sendEmptyMessage(USER_COUNT_NOT_ACTIVITE);
                    } else {
                        mHandler.sendEmptyMessage(PWD_ERROR);
                    }
                } else {
                    mHandler.sendEmptyMessage(NO_SUCH_USER);
                }
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //清空当前登录信息
        if (isGrantPermission) {
            Log.i("message", (System.currentTimeMillis() - startTime1) + "");
        }
    }


    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            //添加已登录账号
            case SAME_ACCOUNT:
                ToastUtils.showMessage(R.string.login_the_same_account);
                break;
            // 用户1登陆成功
            case USER_ONE_LOGIN_SUCCESS:
                setUserLoginSuccess(mCurrentUserOne, binding.user1Img);
                binding.ibDelete1.setVisibility(View.VISIBLE);
                binding.txtLogginFirst.setVisibility(View.VISIBLE);
                binding.txtLogginFirst.setText(mCurrentUserOne.username);
                binding.txtUserLayout.setVisibility(View.VISIBLE);
                KeyBoardUtils.closeKeybord(mActivity);
                break;
            // 用户2登陆成功
            case USER_TWO_LOGIN_SUCCESS:
                setUserLoginSuccess(mCurrentUserTwo, binding.user2Img);
                binding.ibDelete2.setVisibility(View.VISIBLE);
                binding.txtLogginSecond.setVisibility(View.VISIBLE);
                binding.txtLogginSecond.setText(mCurrentUserTwo.username);
                binding.txtUserLayout.setVisibility(View.VISIBLE);
                KeyBoardUtils.closeKeybord(mActivity);
                break;
            // 没有对应账号
            case NO_SUCH_USER:
                ToastUtils.showMessage(R.string.login_failed_str);
                break;
            // 密码错误
            case PWD_ERROR:
                AccountUtil.getUtilInstance().preBlockAccount(mActivity, binding.etAutoUsername.getText().toString(), binding.etPassword);
                break;
            // 没有登录人员
            case NO_LOGIN_USER:
                ToastUtils.showMessage(R.string.login_no_user);
                break;
            //登录成功
            case USER_LOGIN_SUCCESS:
                loginSystem();
                break;
            case LOAD_DATA:
                arrayAdapter.clear();
                arrayAdapter.addAll(usersName);
                break;
            case USER_COUNT_NOT_ACTIVITE:
                ToastUtils.showMessage(R.string.login_user_not_activte);
                break;
            default:
                break;

        }
    }

    /**
     * 显示登陆成功的用户
     *
     * @param mUsers
     */
    private void setUserLoginSuccess(Users mUsers, ImageView mImageView) {
        speak(mUsers.username + "登陆成功");
        String imagePath = Config.PICTURES_FOLDER + mUsers.headpic;
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        if (bm == null) {
            mImageView.setImageResource(R.mipmap.default_header);
        } else {
            mImageView.setImageBitmap(bm);
        }
        binding.etAutoUsername.setText("");
        binding.etPassword.setText("");

    }

    private boolean thirdLogin() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String account = bundle.getString("account", "");
            String password = bundle.getString("pwd", "");
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                return false;
            }
            Users user = UserService.getInstance().findUserByNameAndPwd(account, password);
            if (null != user) {
                PreferencesUtils.put(Config.CURRENT_LOGIN_USER, user.username);
                PreferencesUtils.put(Config.CURRENT_LOGIN_ACCOUNT, user.account);
                //保存登录班组和账号
                PreferencesUtils.put(Config.CURRENT_DEPARTMENT_ID, user.dept_id);
                KSyncConfig.getInstance().setDept_id(user.dept_id);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else {
                ToastUtils.showMessage("第三方登录拉起失败，Error：用户名或者密码错误");
                return false;
            }
        }
        return false;
    }

    /**
     * 进入系统
     */

    private void loginSystem() {
        String username = "";
        String userAccount = "";
        if (mCurrentUserOne != null && mCurrentUserTwo != null) {
            username = mCurrentUserOne.username + Config.COMMA_SEPARATOR + mCurrentUserTwo.username;
            userAccount = mCurrentUserOne.account + Config.COMMA_SEPARATOR + mCurrentUserTwo.account;
            PreferencesUtils.put(Config.CURRENT_DEPARTMENT_ID, mCurrentUserOne.dept_id);
            PreferencesUtils.put(mCurrentUserOne.account, userOnePassword);
        } else if (mCurrentUserOne != null) {
            username = mCurrentUserOne.username;
            userAccount = mCurrentUserOne.account;
            PreferencesUtils.put(Config.CURRENT_DEPARTMENT_ID, mCurrentUserOne.dept_id);
            PreferencesUtils.put(userAccount, userOnePassword);
        } else if (mCurrentUserTwo != null) {
            username = mCurrentUserTwo.username;
            userAccount = mCurrentUserTwo.account;
            PreferencesUtils.put(Config.CURRENT_DEPARTMENT_ID, mCurrentUserTwo.dept_id);
            PreferencesUtils.put(userAccount, userTwoPassword);
        } else {
            return;
        }
        PreferencesUtils.put(Config.CURRENT_LOGIN_USER, username);
        PreferencesUtils.put(Config.CURRENT_LOGIN_ACCOUNT, userAccount);
        PreferencesUtils.put(Config.OTHER_DEPT_USER,mCurrentUserOne.type);
        PreferencesUtils.put(Config.CURRENT_DEPARTMENT_NAME, mCurrentUserOne.deptName);
        //保存登录班组和账号
        Intent intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
        final String dept_id = mCurrentUserOne != null ? mCurrentUserOne.dept_id : mCurrentUserTwo != null ? mCurrentUserTwo.dept_id : "-1";
        KSyncConfig.getInstance().setDept_id(dept_id);
        if ("-1".equals(dept_id)) {
            ToastUtils.showMessage("当前登录帐号无任何班组信息！");
        } else {
//            ExecutorManager.executeTaskSerially(() -> DepartmentService.getInstance().deleteOtherDataByDept(dept_id));
        }
    }

    /**
     * 语音播报
     *
     * @param content
     */
    protected void speak(String content) {
        TTSUtils.getInstance().startSpeaking(content);
    }

    @Override
    public void onBackPressed() {
        compeletlyExitSystem();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.getInstance().onRequestPermissionsResult(mActivity, requestCode, permissions, grantResults);
    }

    @Override
    public void allPermissionsGranted() {
        LocationUtil.getInstance().preSearchGps(mActivity);
        CustomApplication.getInstance().initApp();
        copyBdzInspectionDb();
        isGrantPermission = true;
        if (thirdLogin()) {
            return;
        }
        inUI();
        initOnClick();
    }
    /**
     * 备份数据库
     */
    public void copyBdzInspectionDb() {
        ProgressDialog dialog = ProgressDialog.show(this, "提示", "正在加密数据，请稍等...请不要强行取消，耐心等待", false, false);
        ExecutorManager.executeTaskSerially(() -> {
            try {
                String innerDateBaseFolder = CommonApplication.getAppContext().getFilesDir().getAbsolutePath() + "/database/";
                File innerFile = new File(innerDateBaseFolder);
                if (!innerFile.exists()) {
                    innerFile.mkdir();
                }
                if (FileUtils.isFileExists(Config.DATABASE_FOLDER + Config.DATABASE_NAME)) {
                    DatabaseUtils.copyDatabase(new File(Config.DATABASE_FOLDER + Config.DATABASE_NAME), "", new File(innerDateBaseFolder + Config.ENCRYPT_DATABASE_NAME), "com.cnksi");
                    FileUtils.deleteFile(Config.DATABASE_FOLDER + Config.DATABASE_NAME);
                }
                runOnUiThread(() -> {
                    checkUpdate();
                    KSyncConfig.getInstance().setDept_id("-1");
                });
            } catch (Exception e) {
                ToastUtils.showMessage("加密失败，请重新同步数据，继续使用");
                e.printStackTrace();
            } finally {
                dialog.cancel();
            }
        });

    }
}
