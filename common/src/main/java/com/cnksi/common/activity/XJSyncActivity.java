package com.cnksi.common.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.common.CommonApplication;
import com.cnksi.common.KSyncConfig;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.model.Department;
import com.cnksi.core.utils.StringUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = (LinearLayout) findViewById(com.cnksi.ksynclib.R.id.tv_download).getParent().getParent().getParent();

        String dept_id = KSyncConfig.getInstance().getDept_id();
        String deptName = "";
        if (!"-1".equals(dept_id)) {
            Department department = DepartmentService.getInstance().findDepartmentById(dept_id);
            if (department != null) {
                deptName = StringUtils.NullToDefault(department.name, department.dept_name, department.pms_name, dept_id);
            }
        }
        TextView textView = new TextView(this);
        textView.setText("当前班组:" + deptName);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        int padding = AutoUtils.getPercentHeightSize(36);
        textView.setPadding(padding, 0, padding, padding / 2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(Color.WHITE);
        layout.addView(textView);

        textView = new TextView(this);
        Object sqlite = CommonApplication.getInstance().getDbManager().getDatabase();
        if (sqlite instanceof SQLiteDatabase) {
            textView.setText("数据版本号:" + ((SQLiteDatabase) sqlite).getVersion());
        } else {
            textView.setText("数据版本号:" + ((android.database.sqlite.SQLiteDatabase) sqlite).getVersion());
        }

        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(padding, 0, padding, padding / 2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(Color.WHITE);
        layout.addView(textView);
    }

}
