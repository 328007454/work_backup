package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.Tool;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 19:05
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ToolService extends BaseService<Tool> {
    protected ToolService() {
        super(Tool.class);
    }
    final  static ToolService instance=new ToolService();

    public static ToolService getInstance() {
        return instance;
    }


    public List<Tool> findByInspectionType(String type) throws DbException {
        return selector().and(Tool.INSPECTION, "=", type).findAll();
    }
}
