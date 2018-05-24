package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.emnu.ToolStatus;
import com.cnksi.bdzinspection.model.OperateToolResult;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 13:52
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class OperateToolResultService extends BaseService<OperateToolResult> {
    protected OperateToolResultService() {
        super(OperateToolResult.class);
    }
    final static OperateToolResultService instance=new OperateToolResultService();

    public static OperateToolResultService getInstance() {
        return instance;
    }
    public List<OperateToolResult> finAllResults(String deptId, String toolId, String bdzId) {
        try {
            Selector selector = selector().and(OperateToolResult.GQJ_ID, "=", toolId).and(OperateToolResult.DLT, "=", "0").and(OperateToolResult.OPERATION, "=", ToolStatus.test.name()).orderBy(OperateToolResult.CREATETIME, true);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
