package com.cnksi.sjjc.sync;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.IndexService;
import com.cnksi.sjjc.service.ModifyRecordService;

import org.xutils.DbManager;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;

public class DataSync extends BaseActivity {

    public static final int SYNC_FINISH = 0x003;
    public static final int SYNC_MESSAGE = 0x002;
    public static final int SYNC_EXCEPTION = 0x004;
    // socket
    private ServerSocket serverSocket;
    // 显示同步进度
    @ViewInject(R.id.tv_progressBar_text)
    private TextView tvProgressBarText;
    @ViewInject(R.id.progressBar1)
    private ProgressBar mProgressBar;
    @ViewInject(R.id.progressBar2)
    private ProgressBar progressBar2;

    // 需要上传的数据库
    private DbManager dbManager = null;
    private String syncDataFrom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_sync);
        x.view().inject(this);

        initData();
    }

    protected void initData() {
        syncDataFrom = getIntent().getStringExtra(Config.SYNC_COME_FROM);
        CustomerDialog.showProgress(this, "正在准备上传数据，请稍等...");
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                generateUploadData();
                mHandler.sendEmptyMessage(LOAD_DATA);
                // 清除所有的缓存文件
                FileUtils.deleteAllCache(getApplicationContext());
                // 删除一周之前的备份文件
                long oneWeekAgoTime = Long.valueOf(((long) 2 * (long) 24 * 60 * 60 * 1000));
                FileUtils.deleteBakFiles(Config.BAK_FOLDER, oneWeekAgoTime);
                // 拷贝备份文件
                // FileUtils.copyDirectory(Config.UPLOAD_DATABASE_FOLDER, getApplicationBakFolder(), true);
            }
        });
    }

    private void generateUploadData() {
        if (dbManager == null) {
            dbManager = x.getDb(new DbManager.DaoConfig().setDbDir(new File(Config.UPLOAD_DATABASE_FOLDER)).setDbName(Config.UPLOAD_DATABASE_NAME));
        } // 生成需要上传的数据
        IndexService.getInstance().generateUploadData(dbManager);
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case SYNC_FINISH:
//                if (syncDataFrom.equalsIgnoreCase(Config.LOGACTIVITY_TO_SYNC)) {
//                    if (ScreenManager.getScreenManager().hasActivity(LoginActivity.class)) {
//                        ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
//                    } else {
//                        startActivity(new Intent(this, LoginActivity.class));
//                        this.finish();
//                    }
//                } else if(syncDataFrom.equalsIgnoreCase(Config.LAUNCHERACTIVITY_TO_SYNC)) {
//                    //Launcher界面数据同步完成后再返回启动跳转的界面
//                    if (ScreenManager.getScreenManager().hasActivity(LauncherActivity.class)) {
//                        ScreenManager.getScreenManager().popAllActivityExceptOne(LauncherActivity.class);
//                    } else {
//                        startActivity(new Intent(this, LauncherActivity.class));
//                        this.finish();
//                    }
//                }

                if (bakUploadDb()) {
                    ModifyRecordService.getInstance().deleteAllModifyRecord();
                }
                CToast.showLong(getApplicationContext(),"即将重启程序...");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomApplication.getInstance().restartApp();
                    }
                },500);

                break;
            case SYNC_MESSAGE:
                tvProgressBarText.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                tvProgressBarText.setText(msg.obj.toString());
                break;
            case LOAD_DATA:
                // 开启数据同步
                CustomerDialog.dismissProgress();
                mProgressBar.setVisibility(View.VISIBLE);
                try {
                    if (serverSocket == null || serverSocket.isClosed()) {
                        serverSocket = new ServerSocket(8891);
                        mFixedThreadPoolExecutor.execute(new SynchronizeThread(serverSocket, mHandler));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 备份数据库
     */
    public boolean bakUploadDb() {
        return FileUtils.copyFile(Config.UPLOAD_DATABASE_FOLDER + Config.UPLOAD_DATABASE_NAME, Config.BAK_FOLDER + "db/" + DateUtils.getCurrentLongTime());
    }

    @Override
    protected void onDestroy() {
        colseSocket();
        if (dbManager != null) {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DeviceService.getInstance().refreshDeviceHasCopy();
            }
        });
        super.onDestroy();
    }

    private void colseSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 系统备份文件
     *
     * @return
     */
    public static String getApplicationBakFolder() {
        Calendar calendar = Calendar.getInstance();
        String path = calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_"
                + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE);
        return Config.BAK_FOLDER + "/" + path;
    }

    // private void createIndex(){
    //
    // create index spacing_index on spacing(bdzid);
    //
    // create index device_index on device(bdzid,spid,name_pinyin)
    //
    // create index device_part_index on device_part(deviceid)
    //
    // create index device_standards_index on device_standards(duid,kind)
    //
    // create index defect_define_index on defect_define(staid)
    //
    // }
}
