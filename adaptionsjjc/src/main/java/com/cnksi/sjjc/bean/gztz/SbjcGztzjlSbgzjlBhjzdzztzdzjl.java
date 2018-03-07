package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlSbgzjlBhjzdzztzdzjl._TABLE_NAME_)
public class SbjcGztzjlSbgzjlBhjzdzztzdzjl{
	public static final String _TABLE_NAME_="sbjc_gztzjl_sbgzjl_bhjzdzztzdzjl";

	public static final String _PK_NAME_ = "id";

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id  ;
	
	/**设备故障id*/
	public static final String SBGZJL_ID = "sbgzjl_id";
	@Column(name = SBGZJL_ID)
	public String sbgzjlId  ;
	
	/**设备名称*/
	public static final String BH_SBMC = "bh_sbmc";
	@Column(name = BH_SBMC)
	public String bhSbmc  ;
	
	/***/
	public static final String BH_SBMC_K = "bh_sbmc_k";
	@Column(name = BH_SBMC_K)
	public String bhSbmcK  ;
	
	/**动作时间*/
	public static final String BH_DZSJ = "bh_dzsj";
	@Column(name = BH_DZSJ)
	public String bhDzsj  ;
	
	/**原因及检查情况*/
	public static final String BH_YYJJCQK = "bh_yyjjcqk";
	@Column(name = BH_YYJJCQK)
	public String bhYyjjcqk  ;
	
	/**断路器跳闸情况*/
	public static final String BH_DLQTZQK = "bh_dlqtzqk";
	@Column(name = BH_DLQTZQK)
	public String bhDlqtzqk  ;
	
	/**备注*/
	public static final String BH_BZ = "bh_bz";
	@Column(name = BH_BZ)
	public String bhBz  ;


	/***/
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public int dlt  =0 ;

}
