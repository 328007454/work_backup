package com.cnksi.sjjc.sync;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cnksi.core.common.DeviceUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.ksynclib.KNConfig;
import com.cnksi.sjjc.BuildConfig;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cnksi.sjjc.CustomApplication.getDbManager;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/3/15 11:14
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class KSyncConfig {
    public static final String DEPT_ID = "dept_id";
    final static KSyncConfig instance = new KSyncConfig();
    private String dept_id;
    private Set<String> uploadFolder = new HashSet<>();
    private Set<String> downFolder = new HashSet<>();

    private KSyncConfig() {
        //避免程序Crash之后失去了班组信息。
        dept_id = PreferencesUtils.getString(CustomApplication.getAppContext(), DEPT_ID, "-1");
    }

    public static KSyncConfig getInstance() {
        return instance;
    }

    public String getDept_id() {
        return dept_id;
    }

    public KSyncConfig setDept_id(String dept_id) {
        this.dept_id = dept_id;
        PreferencesUtils.put(CustomApplication.getAppContext(), DEPT_ID, dept_id);
        initFolder();
        return this;
    }

    private void startSync(Context context, Intent intent) {
        intent.putExtra(DEPT_ID, getDept_id());
        context.startActivity(intent);
    }

    public void startNetWorkSync(Context context) {
        PreferencesUtils.put(context, "SYNC_WAY", true);
        Intent intent = new Intent(context, NetWorkSyncActivity.class);
        startSync(context, intent);
    }

    public void startUsbWorkSync(Context context) {
        PreferencesUtils.put(context, "SYNC_WAY", false);
        Intent intent = new Intent(context, UsbSyncActivity.class);
        startSync(context, intent);
    }

    public KNConfig getKNConfig(Context context) {
        initFolder();
        String deviceId = DeviceUtils.getSerialNumber(context);
        KNConfig config = new KNConfig(context, Config.DATABASE_NAME, Config.DATABASE_FOLDER, Config.SYNC_APP_ID,
                Config.SYNC_URL, deviceId, getDbManager().getDatabase(), Config.SYNC_BASE_FOLDER);
        config.configDebug(BuildConfig.DEBUG);
        config.configDownFolder(getFolderString(downFolder));
        config.configUploadFolder(getFolderString(uploadFolder));
        config.configDynicParam("dept_id", dept_id);
        config.configDownFile(isHaveDept());
        config.configUploadFile(isHaveDept());
        return config;
    }

    public boolean isHaveDept() {
        return !(TextUtils.isEmpty(dept_id) || dept_id.equals("-1"));
    }

    private String getFolderString(Set<String> strings) {
        if (strings.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void initFolder() {
        downFolder.clear();
        uploadFolder.clear();
        downFolder.add("lib");
        downFolder.add("lib/wt");
        downFolder.add("signimg");
        downFolder.add("download");


        uploadFolder.add("signimg");
        uploadFolder.add("video");
        uploadFolder.add("audio");
        uploadFolder.add("log");
        uploadFolder.add("error");
        SqlInfo info = new SqlInfo("SELECT folder_name FROM bdz where dlt=0 and dept_id=?;");
        info.addBindArg(new KeyValue("dept_id", dept_id));
        try {
            List<DbModel> models = CustomApplication.getDbManager().findDbModelAll(info);
            if (models != null) {
                for (DbModel model : models) {
                    String path = model.getString("folder_name");
                    if (!TextUtils.isEmpty(path)) {
                        uploadFolder.add(path);
                        downFolder.add(path);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        //增加下载APK文件夹
        info = new SqlInfo("select short_name_pinyin from city");
        try {
            DbModel model = CustomApplication.getDbManager().findDbModelFirst(info);
            if (model != null) {
                downFolder.add("admin/" + model.getString("short_name_pinyin") + "/apk");
                downFolder.add("admin/" + model.getString("short_name_pinyin") + "/gqj");
                uploadFolder.add("admin/" + model.getString("short_name_pinyin") + "/gqj");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public String getDownFolderString() {
        return getFolderString(downFolder);
    }

    public String getUploadFolderString() {
        return getFolderString(uploadFolder);
    }

    public Set<String> getDownFolder() {
        return downFolder;
    }

    public Set<String> getUploadFolder() {
        return uploadFolder;
    }
}
