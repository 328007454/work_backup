package com.cnksi.sjjc.bean.gztz;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjl._TABLE_NAME_)
public class SbjcGztzjl {
    public static final String _TABLE_NAME_ = "sbjc_gztzjl";

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

    /***/
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;

    /**
     * 断路器编号
     */
    public static final String DLQBH = "dlqbh";
    @Column(name = DLQBH)
    public String dlqbh;

    /**
     * 断路器名称
     */
    public static final String DLQMC = "dlqmc";
    @Column(name = DLQMC)
    public String dlqmc;

    /**
     * 断路器跳闸情况
     */
    public static final String BH_DLQTZQK = "bh_dlqtzqk";
    @Column(name = BH_DLQTZQK)
    public String bhDlqtzqk;

    /**
     * 原因及检查情况
     */
    public static final String BH_YYJJCQK = "bh_yyjjcqk";
    @Column(name = BH_YYJJCQK)
    public String bhYyjjcqk;

    /**
     * 设备相别
     */
    public static final String SBXB = "sbxb";
    @Column(name = SBXB)
    public String sbxb;
    /**
     * 设备相别
     */
    public static final String SBXBK = "sbxb_k";
    @Column(name = SBXBK)
    public String sbxbK;

    /**
     * 是否动作：Y（是）/N（否）
     */
    public static final String SFDZ = "sfdz";
    @Column(name = SFDZ)
    public String sfdz;

    /**
     * 动作评价
     */
    public static final String DZPJ = "dzpj";
    @Column(name = DZPJ)
    public String dzpj;

    /**
     * 是否站内故障：Y/N
     */
    public static final String SFZNGZ = "sfzngz";
    @Column(name = SFZNGZ)
    public String sfzngz;

    /**
     * 故障发生时间
     */
    public static final String GZFSSJ = "gzfssj";
    @Column(name = GZFSSJ)
    public String gzfssj;

    /**
     * 故障电压等级
     */
    public static final String GZDYDJ = "gzdydj";
    @Column(name = GZDYDJ)
    public String gzdydj;

    /**
     * 故障电压等级key值
     */
    public static final String GZDYDJ_K = "gzdydj_k";
    @Column(name = GZDYDJ_K)
    public String gzdydjK;

    /**
     * 故障类型
     */
    public static final String GZLX = "gzlx";
    @Column(name = GZLX)
    public String gzlx;

    /***/
    public static final String GZLX_K = "gzlx_k";
    @Column(name = GZLX_K)
    public String gzlxK;

    /**
     * 故障时段天气
     */
    public static final String GZSDTQ = "gzsdtq";
    @Column(name = GZSDTQ)
    public String gzsdtq;
    /**
     * 故障时段天气
     */
    public static final String GZSDTQK = "gzsdtq_k";
    @Column(name = GZSDTQK)
    public String gzsdtqK;

    /**
     * 故障是否越级（Y/N）
     */
    public static final String GZSFYJ = "gzsfyj";
    @Column(name = GZSFYJ)
    public String gzsfyj;

    /**
     * 故障类别
     */
    public static final String GZLB = "gzlb";
    @Column(name = GZLB)
    public String gzlb;

    /***/
    public static final String GZLB_K = "gzlb_k";
    @Column(name = GZLB_K)
    public String gzlbK;

    /**
     * 是否跳闸
     */
    public static final String SFTZ = "sftz";
    @Column(name = SFTZ)
    public String sftz;

    /**
     * 是否停运
     */
    public static final String SFTY = "sfty";
    @Column(name = SFTY)
    public String sfty;

    /**
     * 停运范围
     */
    public static final String TYFW = "tyfw";
    @Column(name = TYFW)
    public String tyfw;

    /***/
    public static final String TYFW_K = "tyfw_k";
    @Column(name = TYFW_K)
    public String tyfwK;

    /**
     * 故障停运设备
     */
    public static final String GZTYSB = "gztysb";
    @Column(name = GZTYSB)
    public String gztysb;

    /***/
    public static final String GZTYSB_K = "gztysb_k";
    @Column(name = GZTYSB_K)
    public String gztysbK;

    /**
     * 断路器检查情况
     */
    public static final String DLQJCQK = "dlqjcqk";
    @Column(name = DLQJCQK)
    public String dlqjcqk;

    /**
     * 开关跳闸备注
     */
    public static final String KGTZ_BZ = "kgtz_bz";
    @Column(name = KGTZ_BZ)
    public String kgtzBz;

    /**
     * 重合闸动作情况
     */
    public static final String CHZDZQK = "chzdzqk";
    @Column(name = CHZDZQK)
    public String chzdzqk;
    /***/
    public static final String CHZDZQK_K = "chzdzqk_k";
    @Column(name = CHZDZQK_K)
    public String chzdzqkK  ;
    /**
     * 各项跳闸次数（json格式）
     */
    public static final String GXTZCS = "gxtzcs";
    @Column(name = GXTZCS)
    public String gxtzcs;

    /**
     * 累计跳闸次数（json格式）
     */
    public static final String LJTZCS = "ljtzcs";
    @Column(name = LJTZCS)
    public String ljtzcs;

    /**
     * 故障电流
     */
    public static final String GZDL = "gzdl";
    @Column(name = GZDL)
    public String gzdl;
    /**累计值*/
    public static final String LJZ = "ljz";
    @Column(name = LJZ)
    public String ljz  ;
    /**
     * 二次故障电流
     */
    public static final String ECGZDL = "ecgzdl";
    @Column(name = ECGZDL)
    public String ecgzdl;

    /**
     * 恢复送电时间
     */
    public static final String HFSDSJ = "hfsdsj";
    @Column(name = HFSDSJ)
    public String hfsdsj;

    /**
     * 最后一次大修时间
     */
    public static final String ZHYCDXSJ = "zhycdxsj";
    @Column(name = ZHYCDXSJ)
    public String zhycdxsj;

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
     * 保护名称
     */
    public static final String BHMC = "bhmc";
    @Column(name = BHMC)
    public String bhmc;

    /***/
    public static final String BHMC_K = "bhmc_k";
    @Column(name = BHMC_K)
    public String bhmcK;

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
     * 动作保护附件（现场照片）
     */
    public static final String DZBH_FJ = "dzbh_fj";
    @Column(name = DZBH_FJ)
    public String dzbhFj;

    /**
     * 动作保护备注
     */
    public static final String DZBH_BZ = "dzbh_bz";
    @Column(name = DZBH_BZ)
    public String dzbhBz;

    /**
     * 故障录波器名称
     */
    public static final String GZ_GZLBQMC = "gz_gzlbqmc";
    @Column(name = GZ_GZLBQMC)
    public String gzGzlbqmc;

    /***/
    public static final String GZ_GZLBQMC_K = "gz_gzlbqmc_k";
    @Column(name = GZ_GZLBQMC_K)
    public String gzGzlbqmcK;

    /**
     * 故障录波分析
     */
    public static final String GZ_GZLBFX = "gz_gzlbfx";
    @Column(name = GZ_GZLBFX)
    public String gzGzlbfx;

    /**
     * 故障录波测距
     */
    public static final String GZ_GZLBCJ = "gz_gzlbcj";
    @Column(name = GZ_GZLBCJ)
    public String gzGzlbcj;

    /**
     * 保护动作情况
     */
    public static final String BHDZQK = "bhdzqk";
    @Column(name = BHDZQK)
    public String bhdzqk;

    /**
     * 故障简题
     */
    public static final String GZJT = "gzjt";
    @Column(name = GZJT)
    public String gzjt;

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
     * 创建日期
     */
    public static final String CREATE_TIME = "create_time";
    @Column(name = CREATE_TIME)
    public String createTime;

    /***/
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public int dlt = 0;


    public static SbjcGztzjl create(String currentReportId, String currentBdzId) {
        SbjcGztzjl rs = new SbjcGztzjl();
        rs.bdzid = currentBdzId;
        rs.id = FunctionUtil.getPrimarykey();
        rs.reportid = currentReportId;
        rs.createTime = DateUtils.getCurrentLongTime();
        return rs;
    }
}
