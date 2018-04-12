package com.cnksi.bdzinspection.daoservice;

import java.util.List;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.StandardSpecial;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

/**
 *
 * @author lyndon
 *
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
		Selector selector = Selector.from(StandardSpecial.class).where(StandardSpecial.KIND, "=", insepctionType);
		try {
			return XunshiApplication.getDbUtils().findAll(selector);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

}
