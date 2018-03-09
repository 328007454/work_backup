package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlBhdzjl._TABLE_NAME_)
public class SbjcGztzjlBhdzjl{
	public static final String _TABLE_NAME_="sbjc_gztzjl_bhdzjl";

	public static final String _PK_NAME_ = "id";

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id  ;
	
	/**所属报告ID*/
	public static final String REPORTID = "reportid";
	@Column(name = REPORTID)
	public String reportid  ;
	
	/**故障跳闸id*/
	public static final String GZTZJL_ID = "gztzjl_id";
	@Column(name = GZTZJL_ID)
	public String gztzjlId  ;
	
	/***/
	public static final String SBMC = "sbmc";
	@Column(name = SBMC)
	public String sbmc  ;
	
	/**设备名称*/
	public static final String SBMC_K = "sbmc_k";
	@Column(name = SBMC_K)
	public String sbmcK  ;
	
	/**保护设备名称*/
	public static final String BH_SBMC = "bh_sbmc";
	@Column(name = BH_SBMC)
	public String bhSbmc  ;
	
	/**设备名称*/
	public static final String BH_SBMC_K = "bh_sbmc_k";
	@Column(name = BH_SBMC_K)
	public String bhSbmcK  ;
	
	/**启动时间*/
	public static final String BHQDSJ = "bhqdsj";
	@Column(name = BHQDSJ)
	public String bhqdsj  ;
	
	/**保护原件类型及动作时间*/
	public static final String BHYJLXJDZSJ = "bhyjlxjdzsj";
	@Column(name = BHYJLXJDZSJ)
	public String bhyjlxjdzsj  ;
	
	/***/
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public int dlt  =0 ;
	
	
}
