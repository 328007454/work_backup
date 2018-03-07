package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlSbgzjlGzlbdzjl._TABLE_NAME_)
public class SbjcGztzjlSbgzjlGzlbdzjl{
	public static final String _TABLE_NAME_="sbjc_gztzjl_sbgzjl_gzlbdzjl";

	public static final String _PK_NAME_ = "id";

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id  ;
	
	/***/
	public static final String SBGZJL_ID = "sbgzjl_id";
	@Column(name = SBGZJL_ID)
	public String sbgzjlId  ;
	
	/**故障录波器名称*/
	public static final String GZ_GZLBQMC = "gz_gzlbqmc";
	@Column(name = GZ_GZLBQMC)
	public String gzGzlbqmc  ;
	
	/***/
	public static final String GZ_GZLBQMC_K = "gz_gzlbqmc_k";
	@Column(name = GZ_GZLBQMC_K)
	public String gzGzlbqmcK  ;
	
	/**故障录波分析*/
	public static final String GZ_GZLBFX = "gz_gzlbfx";
	@Column(name = GZ_GZLBFX)
	public String gzGzlbfx  ;
	
	/**故障录波测距*/
	public static final String GZ_GZLBCJ = "gz_gzlbcj";
	@Column(name = GZ_GZLBCJ)
	public String gzGzlbcj  ;

	/***/
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public int dlt  =0 ;


}
