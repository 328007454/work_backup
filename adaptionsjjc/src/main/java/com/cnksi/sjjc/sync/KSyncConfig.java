package com.cnksi.sjjc.sync;

import android.content.Context;
import android.content.Intent;

import com.cnksi.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/3/15 11:14
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class KSyncConfig {
    final static KSyncConfig instance = new KSyncConfig();

    public static KSyncConfig getInstance() {
        return instance;
    }

    private String dept_id;
    private List<String> uploadFolder;


    public String getDept_id() {
        return dept_id;
    }

    public KSyncConfig setDept_id(String dept_id) {
        this.dept_id = dept_id;
        return this;
    }

    public List<String> getUploadFolder() {
        return uploadFolder == null ? new ArrayList<String>() : uploadFolder;
    }

    public KSyncConfig setUploadFolder(List<String> uploadFolder) {
        this.uploadFolder = uploadFolder;
        return this;
    }

    private List<String> downFolder;

    public List<String> getDownFolder() {
        return downFolder == null ? new ArrayList<String>() : downFolder;
    }

    public KSyncConfig setDownFolder(List<String> downFolder) {
        this.downFolder = downFolder;
        return this;
    }

    private void startSync(Context context, Intent intent) {
        intent.putExtra("dept_id", getDept_id());
        intent.putExtra("down_folder", StringUtils.ArrayListToString(getDownFolder()));
        intent.putExtra("upload_folder", StringUtils.ArrayListToString(getUploadFolder()));
        context.startActivity(intent);
    }

    public void startNetWorkSync(Context context) {
        Intent intent = new Intent(context, NetWorkSyncActivity.class);
        startSync(context, intent);
    }

    public void startUsbWorkSync(Context context) {
        Intent intent = new Intent(context, UsbSyncActivity.class);
        startSync(context, intent);
    }


}
