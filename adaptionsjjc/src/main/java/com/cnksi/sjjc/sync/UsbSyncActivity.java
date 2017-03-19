package com.cnksi.sjjc.sync;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.ksynclib.KNConfig;
import com.cnksi.ksynclib.KSync;
import com.cnksi.ksynclib.KUsbSync;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Department;
import com.cnksi.sjjc.databinding.ActivityUsbSyncBinding;
import com.cnksi.sjjc.service.DepartmentService;

import static com.cnksi.ksynclib.activity.KSyncAJActivity.DELETE_FINISHED;

/**
 * USB桌面同步
 * <p>
 * TODO 完成界面
 */
public class UsbSyncActivity extends AppCompatActivity implements View.OnClickListener {

    private UsbSyncActivity currentActivity;

    ActivityUsbSyncBinding binding;

    KHandler handler = new KHandler();
    KUsbSync usbSync = null;
    KSync kSync = null;
    KNConfig config = null;
    String dept_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsbSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentActivity = this;
        //打开8891端口
        initKsync();
    }

    private void initKsync() {

        config = KSyncConfig.getInstance().getKNConfig(this);
        kSync = new KSync(config, handler);
        usbSync = KUsbSync.open(config);
        dept_id = KSyncConfig.getInstance().getDept_id();
        String deptName = "无";
        if (!"-1".equals(dept_id)) {
            Department department = DepartmentService.getInstance().findDepartmentById(dept_id);
            if (department != null)
                deptName = StringUtils.BlankToDefault(department.name, department.dept_name, department.pms_name, dept_id);
        }
        binding.tvDept.setText("当前班组： " + deptName);
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacks(null);
        KUsbSync.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ibtn_sync_menu) {
            SyncMenuUtils.showMenuPopWindow(currentActivity, binding.ibtnSyncMenu, R.array.UsbSyncMenuArray, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i) {
                        case 0://网络同步方式
                            SyncMenuUtils.changeSync(currentActivity);
                            break;
                        case 1://删除数据库
                            SyncMenuUtils.dropDb(currentActivity, kSync, handler);
                            break;
                        case 2: // 删除文件
                            SyncMenuUtils.deleteBakFile(currentActivity, kSync, handler);
                            break;
                        case 3:
                            SyncMenuUtils.deleteOtherDepartmentData(currentActivity, handler, dept_id);
                            break;
                    }
                }
            });
        }
    }

    public class KHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            Log.d("KHandler", String.valueOf(msg.obj));
            switch (msg.what) {
                case DELETE_FINISHED:
                    Toast.makeText(currentActivity, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
                    CustomerDialog.dismissProgress();
                    break;
            }
        }
    }
}
