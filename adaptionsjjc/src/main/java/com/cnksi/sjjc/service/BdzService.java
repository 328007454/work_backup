package com.cnksi.sjjc.service;

import com.cnksi.sjjc.bean.Bdz;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lyndon
 * @version 1.0
 * @date 2016/7/7
 */
public class BdzService extends BaseService<Bdz> {
    private static BdzService instance;

    public static BdzService getInstance() {
        if (null == instance)
            instance = new BdzService();
        return instance;
    }

    public List<String> findUpdloadPictureFolder() {
        List<String> folder = new ArrayList<>();
        try {
            SqlInfo sqlInfo = new SqlInfo("select distinct(folder_name) from bdz where dlt='0'");
            List<DbModel> folderList = findDbModelAll(sqlInfo);
            if (null != folderList && !folderList.isEmpty()) {
                for (DbModel model : folderList)
                    folder.add(model.getString("folder_name"));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return folder;
    }
}
