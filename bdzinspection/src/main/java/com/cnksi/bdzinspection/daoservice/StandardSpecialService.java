package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.common.model.StandardSpecial;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author lyndon
 */
public class StandardSpecialService {

    public static StandardSpecialService mInstance;

    private StandardSpecialService() {
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
            Selector selector = XunshiApplication.getDbUtils().selector(StandardSpecial.class).where(StandardSpecial.KIND, "=", insepctionType);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
