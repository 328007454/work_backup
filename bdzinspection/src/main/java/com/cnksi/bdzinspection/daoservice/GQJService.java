package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.GQJ;
import com.cnksi.bdzinspection.model.Process;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 17:41
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GQJService extends BaseService<GQJ> {
    protected GQJService() {
        super(GQJ.class);
    }

    final static GQJService instance=new GQJService();

    public static GQJService getInstance() {
        return instance;
    }


    public String findProParentID(String value) {

        String sql = "select ifnull(parent_id,id) as id from project where id='" + value + "'";
        try {
            return findDbModelFirst(new SqlInfo(sql)).getString("id");

        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 根据pro_id 查询工器具
     *
     * @return
     */
    public List<GQJ> findGqjById(String value) {
        List<GQJ> processes = null;

        try {
            processes =selector().and(Process.PRO_ID, "=", findProParentID(value)).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return processes;
    }
}
