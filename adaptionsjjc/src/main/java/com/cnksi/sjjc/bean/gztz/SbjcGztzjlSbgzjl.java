package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlSbgzjl._TABLE_NAME_)
public class SbjcGztzjlSbgzjl{
	public static final String _TABLE_NAME_="sbjc_gztzjl_sbgzjl";

	public static final String _PK_NAME_ = "id";

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id  ;
	
	/**所属报告ID*/
	public static final String REPORTID = "reportid";
	@Column(name = REPORTID)
	public String reportid  ;
	
	/**是否站内故障：Y/N*/
	public static final String SFZNGZ = "sfzngz";
	@Column(name = SFZNGZ)
	public String sfzngz  ;
	
	/**故障发生时间*/
	public static final String GZFSSJ = "gzfssj";
	@Column(name = GZFSSJ)
	public String gzfssj  ;
	
	/**故障电压等级*/
	public static final String GZDYDJ = "gzdydj";
	@Column(name = GZDYDJ)
	public String gzdydj  ;
	
	/**故障电压等级key值*/
	public static final String GZDYDJ_K = "gzdydj_k";
	@Column(name = GZDYDJ_K)
	public String gzdydjK  ;
	
	/**故障类型*/
	public static final String GZLX = "gzlx";
	@Column(name = GZLX)
	public String gzlx  ;
	
	/***/
	public static final String GZLX_K = "gzlx_k";
	@Column(name = GZLX_K)
	public String gzlxK  ;
	
	/**故障时段天气*/
	public static final String GZSDTQ = "gzsdtq";
	@Column(name = GZSDTQ)
	public String gzsdtq  ;
	
	/**故障是否越级（Y/N）*/
	public static final String GZSFYJ = "gzsfyj";
	@Column(name = GZSFYJ)
	public String gzsfyj  ;
	
	/**故障类别*/
	public static final String GZLB = "gzlb";
	@Column(name = GZLB)
	public String gzlb  ;
	
	/***/
	public static final String GZLB_K = "gzlb_k";
	@Column(name = GZLB_K)
	public String gzlbK  ;
	
	/**是否跳闸*/
	public static final String SFTZ = "sftz";
	@Column(name = SFTZ)
	public String sftz  ;
	
	/**是否停运*/
	public static final String SFTY = "sfty";
	@Column(name = SFTY)
	public String sfty  ;
	
	/**停运范围*/
	public static final String TYFW = "tyfw";
	@Column(name = TYFW)
	public String tyfw  ;
	
	/***/
	public static final String TYFW_K = "tyfw_k";
	@Column(name = TYFW_K)
	public String tyfwK  ;
	
	/**故障停运设备*/
	public static final String GZTYSB = "gztysb";
	@Column(name = GZTYSB)
	public String gztysb  ;
	
	/***/
	public static final String GZTYSB_K = "gztysb_k";
	@Column(name = GZTYSB_K)
	public String gztysbK  ;
	
	/**故障简题*/
	public static final String GZJT = "gzjt";
	@Column(name = GZJT)
	public String gzjt  ;
	
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
