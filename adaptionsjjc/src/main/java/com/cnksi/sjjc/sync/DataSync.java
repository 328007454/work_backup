package com.cnksi.sjjc.sync;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.databinding.ActivityDataSyncBinding;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.IndexService;
import com.cnksi.sjjc.service.ModifyRecordService;
import com.cnksi.sjjc.util.FileUtil;

import org.xutils.DbManager;
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
    // 需要上传的数据库
    private DbManager dbManager = null;
    private String syncDataFrom = null;
    private ActivityDataSyncBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(_this, R.layout.activity_data_sync);
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {
        syncDataFrom = getIntent().getStringExtra(Config.SYNC_COME_FROM);
        CustomerDialog.showProgress(this, "正在准备上传数据，请稍等...");
        ExecutorManager.executeTaskSerially(() -> {
            generateUploadData();
            mHandler.sendEmptyMessage(LOAD_DATA);
            // 清除所有的缓存文件
            FileUtils.deleteAllCache(getApplicationContext());
            // 删除一周之前的备份文件
            long oneWeekAgoTime = ((long) 2 * (long) 24 * 60 * 60 * 1000);
            FileUtil.deleteBakFiles(Config.BAK_FOLDER, oneWeekAgoTime);
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
                if (bakUploadDb()) {
                    ModifyRecordService.getInstance().deleteAllModifyRecord();
                }
                ToastUtils.showMessageLong("即将重启程序...");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomApplication.getInstance().restartApp();
                    }
                }, 500);

                break;
            case SYNC_MESSAGE:
                binding.tvProgressBarText.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.VISIBLE);
                binding.tvProgressBarText.setText(msg.obj.toString());
                break;
            case LOAD_DATA:
                // 开启数据同步
                CustomerDialog.dismissProgress();
                binding.progressBar1.setVisibility(View.VISIBLE);
                try {
                    if (serverSocket == null || serverSocket.isClosed()) {
                        serverSocket = new ServerSocket(8891);
                        ExecutorManager.executeTaskSerially(new SynchronizeThread(serverSocket, mHandler));
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
        ExecutorManager.executeTaskSerially(() -> DeviceService.getInstance().refreshDeviceHasCopy());
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
