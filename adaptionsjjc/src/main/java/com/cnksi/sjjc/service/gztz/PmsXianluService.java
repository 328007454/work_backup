package com.cnksi.sjjc.service.gztz;

import com.cnksi.sjjc.bean.gztz.PmsXianlu;
import com.cnksi.sjjc.service.BaseService;

import org.xutils.common.util.KeyValue;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/12 21:56
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class PmsXianluService extends BaseService<PmsXianlu> {
    protected PmsXianluService() {
        super(PmsXianlu.class);
    }

    static final PmsXianluService instance = new PmsXianluService();

    public static PmsXianluService getInstance() {
        return instance;
    }

    public List<KeyValue> findXianluByBdz(String bdzId) {
        List<KeyValue> keyValues = new ArrayList<>();
        try {
            List<PmsXianlu> xianlus = selector().and(PmsXianlu.BDZID, "=", bdzId).findAll();
            if (xianlus != null) {
                for (PmsXianlu xianlu : xianlus) {
                    keyValues.add(new KeyValue(xianlu.pmsId, xianlu.name));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return keyValues;
    }
}
