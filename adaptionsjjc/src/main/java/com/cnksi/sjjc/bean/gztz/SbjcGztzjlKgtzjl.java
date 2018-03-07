package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlKgtzjl._TABLE_NAME_)
public class SbjcGztzjlKgtzjl{
	public static final String _TABLE_NAME_="sbjc_gztzjl_kgtzjl";

	public static final String _PK_NAME_ = "id";

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id  ;
	
	/**所属报告ID*/
	public static final String REPORTID = "reportid";
	@Column(name = REPORTID)
	public String reportid  ;
	
	/***/
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid  ;
	
	/**断路器编号*/
	public static final String DLQBH = "dlqbh";
	@Column(name = DLQBH)
	public String dlqbh  ;
	
	/**设备相别*/
	public static final String SBXB = "sbxb";
	@Column(name = SBXB)
	public String sbxb  ;
	
	/**是否动作：Y（是）/N（否）*/
	public static final String SFDZ = "sfdz";
	@Column(name = SFDZ)
	public String sfdz  ;
	
	/**动作评价：正动（zd）/误动（wd）*/
	public static final String DZPJ = "dzpj";
	@Column(name = DZPJ)
	public String dzpj  ;
	
	/**重合闸动作情况*/
	public static final String CHZDZQK = "chzdzqk";
	@Column(name = CHZDZQK)
	public String chzdzqk  ;
	
	/**各项跳闸次数（json格式）*/
	public static final String GXTZCS = "gxtzcs";
	@Column(name = GXTZCS)
	public String gxtzcs  ;
	
	/**故障电流*/
	public static final String GZDL = "gzdl";
	@Column(name = GZDL)
	public String gzdl  ;
	
	/**二次故障电流*/
	public static final String ECGZDL = "ecgzdl";
	@Column(name = ECGZDL)
	public String ecgzdl  ;
	
	/**恢复送电时间*/
	public static final String HFSDSJ = "hfsdsj";
	@Column(name = HFSDSJ)
	public String hfsdsj  ;
	
	/**最后一次大修时间*/
	public static final String ZHYCDXSJ = "zhycdxsj";
	@Column(name = ZHYCDXSJ)
	public String zhycdxsj  ;
	
	/**保护装置型号*/
	public static final String BHZZXH = "bhzzxh";
	@Column(name = BHZZXH)
	public String bhzzxh  ;
	
	/**启动时间*/
	public static final String QDSJ = "qdsj";
	@Column(name = QDSJ)
	public String qdsj  ;
	
	/**保护动作时间*/
	public static final String BHDZSJ = "bhdzsj";
	@Column(name = BHDZSJ)
	public String bhdzsj  ;
	
	/**保护原件类型*/
	public static final String BHYJLX = "bhyjlx";
	@Column(name = BHYJLX)
	public String bhyjlx  ;
	
	/**保护动作情况*/
	public static final String BHDZQK = "bhdzqk";
	@Column(name = BHDZQK)
	public String bhdzqk  ;
	
	/**断路器检查情况*/
	public static final String DLQJCQK = "dlqjcqk";
	@Column(name = DLQJCQK)
	public String dlqjcqk  ;
	
	/**备注*/
	public static final String BZ = "bz";
	@Column(name = BZ)
	public String bz  ;
	
	/***/
	public static final String CREATE_TIME = "create_time";
	@Column(name = CREATE_TIME)
	public String createTime  ;
	
	/***/
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public int dlt  =0 ;
	
	
}
