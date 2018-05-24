package com.cnksi.bdzinspection.daoservice;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.model.StandardSpecial;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author lyndon
 */
public class StandardSpecialService extends BaseService<StandardSpecial> {

    public static StandardSpecialService mInstance;

    private StandardSpecialService() {
        super(StandardSpecial.class);
    }

    public static StandardSpecialService getInstance() {
        if (mInstance == null) {
            mInstance = new StandardSpecialService();
        }
        return mInstance;
    }

    public List<StandardSpecial> getStandardSpecial(String insepctionType) {
        // SELECT group_concat(bigid,",") from (SELECT DISTINCT(bigid) as bigid from standard_special where kind=?)
        try {
            Selector selector = selector().and(StandardSpecial.KIND, "=", insepctionType);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
