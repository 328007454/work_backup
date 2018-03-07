package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlBhdzjl._TABLE_NAME_)
public class SbjcGztzjlBhdzjl {
    public static final String _TABLE_NAME_ = "sbjc_gztzjl_bhdzjl";

    public static final String _PK_NAME_ = "id";

    /***/
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id;

    /**
     * 所属报告ID
     */
    public static final String REPORTID = "reportid";
    @Column(name = REPORTID)
    public String reportid;

    /**
     * 保护动作时间
     */
    public static final String BHDZSJ = "bhdzsj";
    @Column(name = BHDZSJ)
    public String bhdzsj;

    /**
     * 整定区
     */
    public static final String ZDQ = "zdq";
    @Column(name = ZDQ)
    public String zdq;

    /***/
    public static final String SBMC = "sbmc";
    @Column(name = SBMC)
    public String sbmc;

    /**
     * 设备名称
     */
    public static final String SBMC_K = "sbmc_k";
    @Column(name = SBMC_K)
    public String sbmcK;

    /**
     * 保护设备名称
     */
    public static final String BHSBMC = "bhsbmc";
    @Column(name = BHSBMC)
    public String bhsbmc;

    /***/
    public static final String BHSBMC_K = "bhsbmc_k";
    @Column(name = BHSBMC_K)
    public String bhsbmcK;

    /**
     * 装置型号
     */
    public static final String ZZXH = "zzxh";
    @Column(name = ZZXH)
    public String zzxh;

    /**
     * 动作评价
     */
    public static final String DZPJ = "dzpj";
    @Column(name = DZPJ)
    public String dzpj;

    /**
     * 联动开关情况
     */
    public static final String LDKGQK = "ldkgqk";
    @Column(name = LDKGQK)
    public String ldkgqk;

    /**
     * 与其他保护配合（Y/N)
     */
    public static final String YQTBHPH = "yqtbhph";
    @Column(name = YQTBHPH)
    public String yqtbhph;

    /**
     * 与录波、子站配合
     */
    public static final String YLBZZPH = "ylbzzph";
    @Column(name = YLBZZPH)
    public String ylbzzph;

    /**
     * 与监控系统配合
     */
    public static final String YJKXTPH = "yjkxtph";
    @Column(name = YJKXTPH)
    public String yjkxtph;

    /**
     * 检查日期
     */
    public static final String JCRQ = "jcrq";
    @Column(name = JCRQ)
    public String jcrq;

    /**
     * 检查人
     */
    public static final String JCR = "jcr";
    @Column(name = JCR)
    public String jcr;

    /***/
    public static final String JCR_K = "jcr_k";
    @Column(name = JCR_K)
    public String jcrK;

    /**
     * 备注
     */
    public static final String BZ = "bz";
    @Column(name = BZ)
    public String bz;

    /***/
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public int dlt = 0;


}
