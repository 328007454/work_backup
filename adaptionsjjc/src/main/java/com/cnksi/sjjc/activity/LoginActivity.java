package com.cnksi.sjjc.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.AppUtils;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.UpdateUtils;
import com.cnksi.ksynclib.activity.KSyncAJActivity;
import com.cnksi.sjjc.BuildConfig;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Users;
import com.cnksi.sjjc.inter.GrantPermissionListener;
import com.cnksi.sjjc.service.UserService;
import com.cnksi.sjjc.sync.DataSync;
import com.cnksi.sjjc.util.PermissionUtil;
import com.iflytek.cloud.SpeechSynthesizer;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
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
    private String speakContent;
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

    @ViewInject(R.id.et_username)//添加登录人员账号
    private EditText mEtUserName;

    @ViewInject(R.id.et_password)//添加登录人员密码
    private EditText mEtPassword;

    @ViewInject(R.id.ib_delete1)//删除登录人员1
    private ImageButton delete1;

    @ViewInject(R.id.ib_delete2)//删除登录人员2
    private ImageButton delete2;

    @ViewInject(R.id.et_auto_username)
    private AutoCompleteTextView autoCompleteTextView;
    private Users mCurrentUserOne, mCurrentUserTwo;
    private List<String> usersName = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;

    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};
    /**
     * 屏蔽Wifi计数器
     */
    private int count = 0;
    private long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        x.view().inject(_this);
        initUI();
        PermissionUtil.getInstance().setGrantPermissionListener(this).checkPermissions(this, permissions);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void initUI() {
        //设置版本信息
        mTvVersion.setText(getString(R.string.sys_copyrights_format_str, AppUtils.getVersionName(_this)));
        if (null == (mTts = SpeechSynthesizer.getSynthesizer())) {
            initSpeech(_this);
        }
        //上次登录人员信息
//        String userName = PreferencesUtils.getString(_this, Users.ACCOUNT, "");
        String userName = PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_ACCOUNT, "");
        if (!TextUtils.isEmpty(userName)) autoCompleteTextView.setText(userName);
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
                mFixedThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        usersName = UserService.getInstance().searchUsersName(s.toString());
                        mHandler.sendEmptyMessage(LOAD_DATA);
                    }
                });
            }
        });
        if (BuildConfig.DEBUG) {
            autoCompleteTextView.setText("00030417");
            mEtPassword.setText("1");
        }
    }

    @Event(value = {R.id.b_add_people_button, R.id.mask_wifi, R.id.ib_delete1, R.id.ib_delete2, R.id.b_login_button, R.id.ivLogo})
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
                if (BuildConfig.USE_NETWORK_SYNC) {
                    Intent intent;
                    intent = new Intent(mCurrentActivity, KSyncAJActivity.class);
                    startActivity(intent);
                } else {
                    ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                    Intent newIntent = new Intent(LoginActivity.this, DataSync.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失败
                    newIntent.putExtra(Config.SYNC_COME_FROM, Config.LOGACTIVITY_TO_SYNC);
                    startActivity(newIntent);
                }

                break;
        }
    }


    /**
     * 登录
     *
     * @param login trun 登录系统 false 添加登录人员
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
        CustomApplication.getExcutorService().execute(new Runnable() {
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
                        mHandler.sendEmptyMessage(USER_ONE_LOGIN_SUCCESS);
                    } else if (null == mCurrentUserTwo) {
                        mCurrentUserTwo = tempUser;
                        mHandler.sendEmptyMessage(USER_TWO_LOGIN_SUCCESS);
                    }
                }
                //查询用户不存在
                else {
                    //根据用户名查询
                    tempUser = UserService.getInstance().findUserByAccount(userName);
                    if (null != tempUser) {
                        mHandler.sendEmptyMessage(PWD_ERROR);
                    } else {
                        mHandler.sendEmptyMessage(NO_SUCH_USER);
                    }
                }
            }
        });
    }

    //    ViewHolder holder;
//    Dialog dialog;
    @Override
    protected void onResume() {
        super.onResume();
//        boolean diff = true;
//        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//            String str = formatter.format(curDate);
//            diff = DateUtils.compareDate(str, "2017-03-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (diff) {
//            holder = new ViewHolder();
//            dialog = DialogUtils.createDialog(this, com.cnksi.sjjc.R.layout.dialog_tips, holder);
//            holder.btCancel.setVisibility(View.GONE);
//            holder.tvTitle.setText("提示");
//            holder.tvContent.setText("已过期");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    compeletlyExitSystem();
//                }
//            });
//            dialog.show();
//            holder.btSure.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    System.exit(0);
//                }
//            });
//        }
    }
//    class ViewHolder {
//        @ViewInject(com.cnksi.sjjc.R.id.btn_sure)
//        private Button btSure;
//        @ViewInject(com.cnksi.sjjc.R.id.tv_dialog_title)
//        private TextView tvTitle;
//        @ViewInject(R.id.tv_dialog_content)
//        private TextView tvContent;
//
//        @ViewInject(com.cnksi.sjjc.R.id.btn_cancel)
//        private Button btCancel;
//
//    }


    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case INIT_SPEECH:
                // 读取内容
                if (!TextUtils.isEmpty(speakContent))
                    mTts.startSpeaking(speakContent, null);
                break;
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
                CToast.showShort(_this, R.string.login_user_not_activte);
                break;
            // 没有登录人员
            case NO_LOGIN_USER:
                CToast.showShort(_this, R.string.login_no_user);
                break;
            //登录成功
            case USER_LOGIN_SUCCESS:
                loginSystem();
                break;
            //更新APP
            case CoreConfig.INSTALL_APP_CODE:
                UpdateUtils.showInstallNewApkDialog(_this, mUpdateFile);
                FileUtils.deleteAllFiles(new File(Config.LOGFOLDER));
                break;
            case LOAD_DATA:
                arrayAdapter.clear();
                arrayAdapter.addAll(usersName);
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


    /**
     * 进入系统
     */
    private void loginSystem() {
        String username = "";
        String userAccout = "";
        if (mCurrentUserOne != null && mCurrentUserTwo != null) {
            username = mCurrentUserOne.username + Config.COMMA_SEPARATOR + mCurrentUserTwo.username;
            userAccout = mCurrentUserOne.account + Config.COMMA_SEPARATOR + mCurrentUserTwo.account;

        } else if (mCurrentUserOne != null) {
            username = mCurrentUserOne.username;
            userAccout = mCurrentUserOne.account;
        } else if (mCurrentUserTwo != null) {
            username = mCurrentUserTwo.username;
            userAccout = mCurrentUserTwo.account;
        } else {
            return;
        }
        PreferencesUtils.put(_this, Config.CURRENT_LOGIN_USER, username);
        PreferencesUtils.put(_this, Config.CURRENT_LOGIN_ACCOUNT, userAccout);
        //保存登录班组和账号
        PreferencesUtils.put(_this, Config.CURRENT_DEPARTMENT_ID, mCurrentUserOne.dept_id);
        Intent intent = new Intent(_this, LauncherActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    /**
     * 语音播报
     *
     * @param content
     */
    protected void speak(String content) {
        speakContent = content;
        mHandler.sendEmptyMessage(INIT_SPEECH);
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
        CustomApplication.getInstance().initApp();
        checkUpdateVersion(Config.DOWNLOAD_APP_FOLDER, null, "0");
    }
}
