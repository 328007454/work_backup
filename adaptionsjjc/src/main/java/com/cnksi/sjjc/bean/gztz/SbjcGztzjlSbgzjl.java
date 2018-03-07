package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlSbgzjl._TABLE_NAME_)
public class SbjcGztzjlSbgzjl {
    public static final String _TABLE_NAME_ = "sbjc_gztzjl_sbgzjl";

    public static final String _PK_NAME_ = "id";

    /***/
    public static final String ID = "id";
    /**
     * 所属报告ID
     */
    public static final String REPORTID = "reportid";
    /**
     * 是否站内故障：Y/N
     */
    public static final String SFZNGZ = "sfzngz";
    /**
     * 故障发生时间
     */
    public static final String GZFSSJ = "gzfssj";
    /**
     * 故障电压等级
     */
    public static final String GZDYDJ = "gzdydj";
    /**
     * 故障电压等级key值
     */
    public static final String GZDYDJ_K = "gzdydj_k";
    /**
     * 故障类型
     */
    public static final String GZLX = "gzlx";
    /***/
    public static final String GZLX_K = "gzlx_k";
    /**
     * 故障时段天气
     */
    public static final String GZSDTQ = "gzsdtq";
    /**
     * 故障是否越级（Y/N）
     */
    public static final String GZSFYJ = "gzsfyj";
    /**
     * 故障类别
     */
    public static final String GZLB = "gzlb";
    /***/
    public static final String GZLB_K = "gzlb_k";
    /**
     * 是否跳闸
     */
    public static final String SFTZ = "sftz";
    /**
     * 是否停运
     */
    public static final String SFTY = "sfty";
    /**
     * 停运范围
     */
    public static final String TYFW = "tyfw";
    /***/
    public static final String TYFW_K = "tyfw_k";
    /**
     * 故障停运设备
     */
    public static final String GZTYSB = "gztysb";
    /***/
    public static final String GZTYSB_K = "gztysb_k";
    /**
     * 故障简题
     */
    public static final String GZJT = "gzjt";
    /**
     * 断路器跳闸情况
     */
    public static final String BHDLQTZQK = "bh_dlqtzqk";
    /**
     * 原因及检查情况
     */
    public static final String BHYYJJCQK = "bh_yyjjcqk";
    /**
     * 故障录波器名称
     */
    public static final String GZ_GZLBQMC = "gz_gzlbqmc";
    /***/
    public static final String GZ_GZLBQMC_K = "gz_gzlbqmc_k";
    /**
     * 故障录波分析
     */
    public static final String GZ_GZLBFX = "gz_gzlbfx";
    /**
     * 故障录波测距
     */
    public static final String GZ_GZLBCJ = "gz_gzlbcj";
    /***/
    public static final String DLT = "dlt";
    @Column(name = ID, isId = true)
    public String id;
    @Column(name = REPORTID)
    public String reportid;
    @Column(name = SFZNGZ)
    public String sfzngz;
    @Column(name = GZFSSJ)
    public String gzfssj;
    @Column(name = GZDYDJ)
    public String gzdydj;
    @Column(name = GZDYDJ_K)
    public String gzdydjK;
    @Column(name = GZLX)
    public String gzlx;
    @Column(name = GZLX_K)
    public String gzlxK;
    @Column(name = GZSDTQ)
    public String gzsdtq;
    @Column(name = GZSFYJ)
    public String gzsfyj;
    @Column(name = GZLB)
    public String gzlb;
    @Column(name = GZLB_K)
    public String gzlbK;
    @Column(name = SFTZ)
    public String sftz;
    @Column(name = SFTY)
    public String sfty;
    @Column(name = TYFW)
    public String tyfw;
    @Column(name = TYFW_K)
    public String tyfwK;
    @Column(name = GZTYSB)
    public String gztysb;
    @Column(name = GZTYSB_K)
    public String gztysbK;
    @Column(name = GZJT)
    public String gzjt;
    @Column(name = BHDLQTZQK)
    public String bh_dlqtzqk;
    @Column(name = BHYYJJCQK)
    public String bh_yyjjcqk;
    @Column(name = GZ_GZLBQMC)
    public String gzGzlbqmc;
    @Column(name = GZ_GZLBQMC_K)
    public String gzGzlbqmcK;
    @Column(name = GZ_GZLBFX)
    public String gzGzlbfx;
    @Column(name = GZ_GZLBCJ)
    public String gzGzlbcj;
    @Column(name = DLT)
    public int dlt = 0;

    public static SbjcGztzjlSbgzjl create(String reportid) {
        SbjcGztzjlSbgzjl rs = new SbjcGztzjlSbgzjl();
        rs.reportid = reportid;
        return rs;
    }
}
