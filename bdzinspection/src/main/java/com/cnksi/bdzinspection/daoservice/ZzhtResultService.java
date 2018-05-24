package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.zzht.ZzhtResult;
import com.cnksi.common.daoservice.BaseService;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 15:26
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ZzhtResultService extends BaseService<ZzhtResult> {
    protected ZzhtResultService() {
        super(ZzhtResult.class);
    }

    private final  static  ZzhtResultService instance=new ZzhtResultService();

    public static ZzhtResultService getInstance() {
        return instance;
    }
}
