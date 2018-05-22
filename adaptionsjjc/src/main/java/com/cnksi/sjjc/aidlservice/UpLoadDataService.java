package com.cnksi.sjjc.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.cnksi.sjjc.util.StringUtils;
import com.cnksi.ksynclib.KNConfig;
import com.cnksi.ksynclib.KSync;
import com.cnksi.sjjc.UpDataToReportManager;
import com.cnksi.common.model.Department;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.sjjc.sync.KSyncConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kkk on 2017/10/13.
 */

public class UpLoadDataService extends Service {
    private static final String TAG = "UDS";
    private KNConfig config;
    private KSync ksync;
    boolean isSyncFile;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler handler = new Handler();
    private Binder mBinder = new UpDataToReportManager.Stub() {
        @Override
        public void upDataWithUpPMS() throws RemoteException {
            Log.d("TAG","开始上传数据");
            upLoad();
        }
    };

    private void upLoad() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ksync.uploadData();
                if (config.isUploadFile()) {
                    isSyncFile = true;
                    if (TextUtils.isEmpty(config.getUploadFolder())) {
                        ksync.uploadFile();
                    } else {
                        ksync.uploadFile(config.getUploadFolder());
                    }
                }
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initKsync();
        Log.d("TAG", "绑定成功");
    }

    private String dept_id;
    private String deptName;
    private void initKsync() {
        config = KSyncConfig.getInstance().getKNConfig(getApplicationContext());
        ksync = new KSync(config, handler);
        dept_id = KSyncConfig.getInstance().getDept_id();
        if (!"-1".equals(dept_id)) {
            Department department = DepartmentService.getInstance().findDepartmentById(dept_id);
            if (department != null) {
                deptName = StringUtils.BlankToDefault(department.name, department.dept_name, department.pms_name, dept_id);
            }
        }
        String url = config.getUrl();
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains(":")) {
            url = url.substring(0, url.indexOf(":"));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
