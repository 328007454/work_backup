package com.cnksi.sjjc.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.bdloc.LocationUtil;
import com.cnksi.core.utils.AppUtils;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.AppVersion;
import com.cnksi.sjjc.bean.Users;
import com.cnksi.sjjc.databinding.DialogCopyTipsBinding;
import com.cnksi.sjjc.dialog.ModifySyncUrlBinding;
import com.cnksi.sjjc.inter.GrantPermissionListener;
import com.cnksi.sjjc.service.DepartmentService;
import com.cnksi.sjjc.service.UserService;
import com.cnksi.sjjc.sync.KSyncConfig;
import com.cnksi.sjjc.util.ActivityUtil;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.PermissionUtil;
import com.cnksi.sjjc.util.TTSUtils;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements GrantPermissionListener {


    private static final String TAG = "LoginActivity";
    public static final int SAME_ACCOUNT = 0x01111;//添加账号相同
    public static final int NO_SUCH_USER = SAME_ACCOUNT + 1;//没有对应账号
    public static final int PWD_ERROR = NO_SUCH_USER + 1;//密码错误
    public static final int USER_ONE_LOGIN_SUCCESS = PWD_ERROR + 1;//添加第一个登录人员成功
    public static final int USER_TWO_LOGIN_SUCCESS = USER_ONE_LOGIN_SUCCESS + 1;//添加第二个登录人员成功
    public static final int NO_LOGIN_USER = USER_TWO_LOGIN_SUCCESS + 1;//没人登录人员
    public static final int USER_LOGIN_SUCCESS = NO_LOGIN_USER + 1;//登录成功
    public static final int SHOW_UPDATE_LOG_DIALOG = USER_LOGIN_SUCCESS + 1;
    public static final int USER_COUNT_NOT_ACTIVITE = SHOW_UPDATE_LOG_DIALOG + 1;
    @ViewInject(R.id.txt_user_layout)
    private RelativeLayout userLayout;
    @ViewInject(R.id.user1_img)
    private ImageView user1ImageView;//登录人员1

    @ViewInject(R.id.txt_loggin_first)
    private TextView txtLogFirst;//登录人员1姓名

    @ViewInject(R.id.user2_img)
    private ImageView user2ImageView;//登录人员2

    @ViewInject(R.id.txt_loggin_second)
    private TextView txtLogSecond;//登录人员2

    @ViewInject(R.id.tv_version)//版本信息
    private TextView mTvVersion;

    @ViewInject(R.id.et_password)//添加登录人员密码
    private EditText mEtPassword;

    @ViewInject(R.id.ib_delete1)//删除登录人员1
    private ImageButton delete1;

    @ViewInject(R.id.ib_delete2)//删除登录人员2
    private ImageButton delete2;

    @ViewInject(R.id.et_auto_username)
    private AutoCompleteTextView autoCompleteTextView;
    private Users mCurrentUserOne, mCurrentUserTwo;
    private List<String> usersName = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private Dialog updateLogDialog;
    private AppVersion remoteSjjcAppVersion;
    private AppVersion currentVersion;
    private DialogCopyTipsBinding layout;
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
    private long startTime = 0;
    private boolean isGrantPermission = false;
    private String userOnePassword;
    private String userTwoPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        x.view().inject(_this);
        PermissionUtil.getInstance().setGrantPermissionListener(this).checkPermissions(this, permissions);
    }

    private void initData() {
        layout = DialogCopyTipsBinding.inflate(getLayoutInflater());
        int dialogWidth = ScreenUtils.getScreenWidth(_this) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        updateLogDialog = DialogUtils.creatDialog(_this, layout.getRoot(), dialogWidth, dialogHeight);
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                PackageInfo info = AppUtils.getLocalPackageInfo(getApplicationContext());
                int version = info.versionCode;
                PackageInfo infoXunshi = null;
                PackageManager manager = null;
                try {
                    //获取巡视app安装版本号
                    infoXunshi = manager.getPackageInfo("com.cnksi.bdzinspection", 0);
                    remoteSjjcAppVersion = CustomApplication.getDbManager().selector(AppVersion.class).where(AppVersion.DLT, "!=", "1").expr(" and version_code > '" + version + "'").expr("and file_name like '%sjjc%'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
                    String apkPath = "";
                    //增加下载APK文件夹
                    SqlInfo info1 = new SqlInfo("select short_name_pinyin from city");
                    try {
                        DbModel model = CustomApplication.getDbManager().findDbModelFirst(info1);
                        if (model != null) {
                            apkPath = "admin/" + model.getString("short_name_pinyin") + "/apk";
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (null != remoteSjjcAppVersion) {
                        checkUpdateVersion(Config.BDZ_INSPECTION_FOLDER + apkPath,
                                Config.PCODE, false, TextUtils.isEmpty(remoteSjjcAppVersion.description) ? "修复bug,优化流畅度" : remoteSjjcAppVersion.description);
                    }
                    if (null != remoteSjjcAppVersion && PreferencesUtils.get(_this, AppUtils.IS_SJJC_AREADY_UPDATE, false)) {
                        PreferencesUtils.put(_this, AppUtils.IS_SJJC_AREADY_UPDATE, false);
                    }
                    currentVersion = CustomApplication.getDbManager().selector(AppVersion.class).where(AppVersion.DLT, "!=", "1").expr(" and version_code = '" + version + "'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
                    if (currentVersion == null)
                        currentVersion = CustomApplication.getDbManager().selector(AppVersion.class).where(AppVersion.DLT, "!=", "1").expr(" and version_code = '" + infoXunshi.versionCode + "'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void initUI() {
        //设置版本信息
        mTvVersion.setText(getString(R.string.sys_copyrights_format_str, AppUtils.getVersionName(_this)));
        String userName = PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_ACCOUNT, "");
        if (!TextUtils.isEmpty(userName)) {
            String[] userNames = userName.split(",");
            autoCompleteTextView.setText(userNames[0]);
            String pwd = PreferencesUtils.get(_this, userNames[0], "");
            mEtPassword.setText(pwd);
        }
        arrayAdapter = new ArrayAdapter<String>(_this, R.layout.user_name_drop_down_item, R.id.tv_user_name);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                String pwd = PreferencesUtils.get(_this, s.toString(), "");
                mEtPassword.setText(pwd);
                mFixedThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        usersName = UserService.getInstance().searchUsersName(s.toString());
                        mHandler.sendEmptyMessage(LOAD_DATA);
                    }
                });
            }
        });

        findViewById(R.id.ivLogo).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                modifySyncURL();
                return true;
            }
        });
    }

    @Event(value = {R.id.b_add_people_button, R.id.mask_wifi, R.id.ib_delete1, R.id.ib_delete2, R.id.b_login_button, R.id.ivLogo, R.id.tv_version})
    private void onClick(View v) {
        switch (v.getId()) {
            //添加登录人员
            case R.id.b_add_people_button:
                loginUser(false);
                break;
            case R.id.mask_wifi:
                if (count == 0) {
                    startTime = System.currentTimeMillis();
                    count++;
                    break;
                } else {
                    if (System.currentTimeMillis() - startTime < 500) {
                        count++;
                    } else {
                        count = 0;
                    }
                    startTime = System.currentTimeMillis();
                }
                if (count >= 5) {
                    boolean maskWifi = !PreferencesUtils.get(_this, Config.MASK_WIFI, true);
                    PreferencesUtils.put(_this, Config.MASK_WIFI, maskWifi);
                    if (maskWifi) {
                        com.cnksi.core.utils.NetWorkUtil.disableNetWork(_this);
                        CToast.showShort(_this, "打开WIFI屏蔽");
                    } else {
                        CToast.showShort(_this, "关闭WIFI屏蔽");
                    }
                    count = 0;
                }
                break;
            //删除第一个登录人员
            case R.id.ib_delete1:
                mCurrentUserOne = null;
                delete1.setVisibility(View.INVISIBLE);
                user1ImageView.setImageResource(R.drawable.ic_portrait_thum);
                txtLogFirst.setVisibility(View.GONE);
                if (delete2.getVisibility() == View.INVISIBLE)
                    userLayout.setVisibility(View.INVISIBLE);
                else userLayout.setVisibility(View.VISIBLE);
                break;
            //删除第二个登录人员
            case R.id.ib_delete2:
                mCurrentUserTwo = null;
                delete2.setVisibility(View.INVISIBLE);
                user2ImageView.setImageResource(R.drawable.ic_portrait_thum);
                txtLogSecond.setVisibility(View.GONE);
                if (delete1.getVisibility() == View.INVISIBLE)
                    userLayout.setVisibility(View.INVISIBLE);
                else userLayout.setVisibility(View.VISIBLE);
                break;
            //登录系统
            case R.id.b_login_button:
                loginUser(true);
                break;
            //跳转数据同步
            case R.id.ivLogo:
                ActivityUtil.startSync(mCurrentActivity);
                break;
            case R.id.tv_version:
                mHandler.sendEmptyMessage(SHOW_UPDATE_LOG_DIALOG);
                break;
            default:
                break;
        }
    }

    /**
     * 显示修改服务器同步地址的对话框，该对话框由长安同步按钮触发
     */
    private void modifySyncURL() {
        final ModifySyncUrlBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_modify_iphost, null, false);
        final Dialog dialog = DialogUtils.createDialog(mCurrentActivity, binding, true);
        binding.etNewUrl.setText(Config.SYNC_URL);
        binding.etAppId.setText(Config.SYNC_APP_ID);
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        binding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getText(binding.etNewUrl).trim();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                } else {
                    url = "http://" + url;
                }
                Config.SYNC_URL = url;
                String appId = getText(binding.etAppId);
                if (!TextUtils.isEmpty(appId)) Config.SYNC_APP_ID = appId;
                dialog.dismiss();
            }
        });
        PreferencesUtils.put(mCurrentActivity, Config.KEY_SYNC_URL, Config.SYNC_URL);
        PreferencesUtils.put(mCurrentActivity, Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
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
            if (null != mCurrentUserOne || null != mCurrentUserTwo)
                mHandler.sendEmptyMessage(USER_LOGIN_SUCCESS);
            else mHandler.sendEmptyMessage(NO_LOGIN_USER);
            return;
        }
        //添加登录人员
        final String userName = autoCompleteTextView.getText().toString().trim();
        final String userPwd = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            CToast.showShort(_this, R.string.et_user_hint_str);
            return;
        }
        if (TextUtils.isEmpty(userPwd)) {
            CToast.showShort(_this, R.string.et_password_hint_str);
            return;
        }
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //根据用户名和密码查询
                Users tempUser = UserService.getInstance().findUserByNameAndPwd(userName, userPwd);
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
                        userOnePassword = mEtPassword.getText().toString().trim();
                        mHandler.sendEmptyMessage(USER_ONE_LOGIN_SUCCESS);
                    } else if (null == mCurrentUserTwo) {
                        mCurrentUserTwo = tempUser;
                        userTwoPassword = mEtPassword.getText().toString().trim();
                        mHandler.sendEmptyMessage(USER_TWO_LOGIN_SUCCESS);
                    }
                }
                //查询用户不存在
                else {
                    //根据用户名查询
                    tempUser = UserService.getInstance().findUserByAccount(userName);
                    if (null != tempUser) {
                        if ("未激活".equalsIgnoreCase(tempUser.pwd))
                            mHandler.sendEmptyMessage(USER_COUNT_NOT_ACTIVITE);
                        else {
                            mHandler.sendEmptyMessage(PWD_ERROR);
                        }
                    } else {
                        mHandler.sendEmptyMessage(NO_SUCH_USER);
                    }
                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //清空当前登录信息
        if (isGrantPermission)
            KSyncConfig.getInstance().setDept_id("-1");
        onClick(findViewById(R.id.ib_delete1));
        onClick(findViewById(R.id.ib_delete2));
    }


    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            //添加已登录账号
            case SAME_ACCOUNT:
                CToast.showShort(_this, R.string.login_the_same_account);
                break;
            // 用户1登陆成功
            case USER_ONE_LOGIN_SUCCESS:
                setUserLoginSuccess(mCurrentUserOne, user1ImageView);
                delete1.setVisibility(View.VISIBLE);
                txtLogFirst.setVisibility(View.VISIBLE);
                txtLogFirst.setText(mCurrentUserOne.username);
                userLayout.setVisibility(View.VISIBLE);
                hideKeyboard();
                break;
            // 用户2登陆成功
            case USER_TWO_LOGIN_SUCCESS:
                setUserLoginSuccess(mCurrentUserTwo, user2ImageView);
                delete2.setVisibility(View.VISIBLE);
                txtLogSecond.setVisibility(View.VISIBLE);
                txtLogSecond.setText(mCurrentUserTwo.username);
                userLayout.setVisibility(View.VISIBLE);
                hideKeyboard();
                break;
            // 没有对应账号
            case NO_SUCH_USER:
                CToast.showShort(_this, R.string.login_failed_str);
                break;
            // 密码错误
            case PWD_ERROR:
                CToast.showShort(_this, R.string.login_failed_password_str);
                break;
            // 没有登录人员
            case NO_LOGIN_USER:
                CToast.showShort(_this, R.string.login_no_user);
                break;
            //登录成功
            case USER_LOGIN_SUCCESS:
                loginSystem();
                break;
            case SHOW_UPDATE_LOG_DIALOG:
                if (null != updateLogDialog && null != currentVersion) {
                    layout.tvDialogTitle.setText("本次更新内容");
                    layout.clickLinearlayout.setVisibility(View.GONE);
                    layout.tvCopy.setVisibility(View.GONE);
                    layout.tvTips.setText(Html.fromHtml(TextUtils.isEmpty(currentVersion.description) ? "欢迎使用！" : currentVersion.description));
                    updateLogDialog.show();
                }
            case LOAD_DATA:
                arrayAdapter.clear();
                arrayAdapter.addAll(usersName);
                break;
            case USER_COUNT_NOT_ACTIVITE:
                CToast.showShort(_this, R.string.login_user_not_activte);
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
        autoCompleteTextView.setText("");
        mEtPassword.setText("");

    }

    private boolean thirdLogin() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String account = bundle.getString("account", "");
            String password = bundle.getString("pwd", "");
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) return false;
            Users user = UserService.getInstance().findUserByNameAndPwd(account, password);
            if (null != user) {
                PreferencesUtils.put(this, Config.CURRENT_LOGIN_USER, user.username);
                PreferencesUtils.put(this, Config.CURRENT_LOGIN_ACCOUNT, user.account);
                //保存登录班组和账号
                PreferencesUtils.put(this, Config.CURRENT_DEPARTMENT_ID, user.dept_id);
                KSyncConfig.getInstance().setDept_id(user.dept_id);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else {
                CToast.showLong(this, "第三方登录拉起失败，Error：用户名或者密码错误");
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
            PreferencesUtils.put(_this, Config.CURRENT_DEPARTMENT_ID, mCurrentUserOne.dept_id);
            PreferencesUtils.put(_this, mCurrentUserOne.account, userOnePassword);
        } else if (mCurrentUserOne != null) {
            username = mCurrentUserOne.username;
            userAccount = mCurrentUserOne.account;
            PreferencesUtils.put(_this, Config.CURRENT_DEPARTMENT_ID, mCurrentUserOne.dept_id);
            PreferencesUtils.put(_this, userAccount, userOnePassword);
        } else if (mCurrentUserTwo != null) {
            username = mCurrentUserTwo.username;
            userAccount = mCurrentUserTwo.account;
            PreferencesUtils.put(_this, Config.CURRENT_DEPARTMENT_ID, mCurrentUserTwo.dept_id);
            PreferencesUtils.put(_this, userAccount, userTwoPassword);
        } else {
            return;
        }
        PreferencesUtils.put(_this, Config.CURRENT_LOGIN_USER, username);
        PreferencesUtils.put(_this, Config.CURRENT_LOGIN_ACCOUNT, userAccount);

        //保存登录班组和账号
        Intent intent = new Intent(_this, HomeActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
        final String dept_id = mCurrentUserOne != null ? mCurrentUserOne.dept_id : mCurrentUserTwo != null ? mCurrentUserTwo.dept_id : "-1";
        KSyncConfig.getInstance().setDept_id(dept_id);
        if ("-1".equals(dept_id)) {
            CToast.showLong(mCurrentActivity, "当前登录帐号无任何班组信息！");
        } else
            mExcutorService.execute(new Runnable() {
                @Override
                public void run() {
                    DepartmentService.getInstance().deleteOtherDataByDept(dept_id);
                }
            });
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
        PermissionUtil.getInstance().onRequestPermissionsResult(_this, requestCode, permissions, grantResults);
    }

    @Override
    public void allPermissionsGranted() {
        PreferencesUtils.put(_this, Config.PERMISSION_STASTUS, true);
        LocationUtil.getInstance().preSearchGps(mCurrentActivity);
        CustomApplication.getInstance().initApp();
        isGrantPermission = true;
        if (thirdLogin()) return;
        initUI();
        initData();

    }
}
