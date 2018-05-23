package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.Logs;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 19:24
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class LogsService extends BaseService<Logs> {
    protected LogsService() {
        super(Logs.class);
    }

    final static LogsService instance=new LogsService();

    public static LogsService getInstance() {
        return instance;
    }


    public void saveOrUpdateQuiet(List<Logs> t)  {
        try {
            saveOrUpdate(t);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
