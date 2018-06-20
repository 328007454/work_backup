package com.cnksi.common.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.common.CommonApplication;
import com.cnksi.common.KSyncConfig;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.model.Department;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.ksynclib.KSync;
import com.cnksi.ksynclib.activity.KSyncAJActivity;
import com.zhy.autolayout.utils.AutoUtils;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/6/11 16:57
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class XJSyncActivity extends KSyncAJActivity {

    private String mSyncAppId;
    private TextView textViewDept;
    private TextView textViewVersion;
    private String deptName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = (LinearLayout) findViewById(com.cnksi.ksynclib.R.id.tv_download).getParent().getParent().getParent();
        mSyncAppId = getIntent().getStringExtra(SYNC_APPID_KEY);
        textViewDept = new TextView(this);
        textViewDept.setGravity(Gravity.CENTER_VERTICAL);
        setDeptInfor();
        int padding = AutoUtils.getPercentHeightSize(36);
        textViewDept.setPadding(padding, 0, padding, padding / 2);
        textViewDept.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textViewDept.setTextColor(Color.WHITE);
        layout.addView(textViewDept);

        textViewVersion = new TextView(this);
        Object sqlite = CommonApplication.getInstance().getDbManager().getDatabase();
        if (sqlite instanceof SQLiteDatabase) {
            textViewVersion.setText("数据版本号:" + ((SQLiteDatabase) sqlite).getVersion());
        } else {
            textViewVersion.setText("数据版本号:" + ((android.database.sqlite.SQLiteDatabase) sqlite).getVersion());
        }

        textViewVersion.append("\t\t 当前APPID：" + mSyncAppId);

        textViewVersion.setGravity(Gravity.CENTER_VERTICAL);
        textViewVersion.setPadding(padding, 0, padding, padding / 2);
        textViewVersion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textViewVersion.setTextColor(Color.WHITE);
        layout.addView(textViewVersion);
        SystemConfig.init();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case KSync.SYNC_DOWN_DATA_SUCCESS:
                if (!CommonApplication.getInstance().getKSyncMap().get(mSyncAppId).getKnConfig().isDownFile()) {
                    ToastUtils.showMessage("数据下载成功");
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            case KSync.SYNC_UP_DATA_SUCCESS:
                if (!CommonApplication.getInstance().getKSyncMap().get(mSyncAppId).getKnConfig().isUploadFile()) {
                    ToastUtils.showMessage("数据上传成功");
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            case KSyncAJActivity.DELETE_FINISHED:
                break;
            default:
        }
    }

    private void setDeptInfor() {
        String dept_id = KSyncConfig.getInstance().getDept_id();
        if (!"-1".equals(dept_id)) {
            Department department = DepartmentService.getInstance().findDepartmentById(dept_id);
            if (department != null) {
                deptName = StringUtils.NullToDefault(department.name, department.dept_name, department.pms_name, dept_id);
            }
            KSyncConfig.getInstance().getKNConfig(this);
        } else {
            deptName = "未登录";
        }
        textViewDept.setText("当前班组:" + deptName);
    }
}
