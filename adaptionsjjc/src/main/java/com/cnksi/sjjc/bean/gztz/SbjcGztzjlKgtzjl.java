package com.cnksi.sjjc.bean.gztz;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlKgtzjl._TABLE_NAME_)
public class SbjcGztzjlKgtzjl {
    public static final String _TABLE_NAME_ = "sbjc_gztzjl_kgtzjl";

    public static final String _PK_NAME_ = "id";

    /***/
    public static final String ID = "id";
    /**
     * 所属报告ID
     */
    public static final String REPORTID = "reportid";
    /***/
    public static final String BDZID = "bdzid";
    /**
     * 断路器编号
     */
    public static final String DLQBH = "dlqbh";
    /**
     * 断路器编号
     */
    public static final String DLQMC = "dlqmc";
    /**
     * 设备相别
     */
    public static final String SBXB = "sbxb";
    /**
     * 是否动作：Y（是）/N（否）
     */
    public static final String SFDZ = "sfdz";
    /**
     * 动作评价：正动（zd）/误动（wd）
     */
    public static final String DZPJ = "dzpj";
    /**
     * 重合闸动作情况
     */
    public static final String CHZDZQK = "chzdzqk";
    /**
     * 各项跳闸次数（json格式）
     */
    public static final String GXTZCS = "gxtzcs";
    /**
     * 故障电流
     */
    public static final String GZDL = "gzdl";
    /**
     * 二次故障电流
     */
    public static final String ECGZDL = "ecgzdl";
    /**
     * 恢复送电时间
     */
    public static final String HFSDSJ = "hfsdsj";
    /**
     * 最后一次大修时间
     */
    public static final String ZHYCDXSJ = "zhycdxsj";
    /**
     * 保护装置型号
     */
    public static final String BHZZXH = "bhzzxh";
    /**
     * 启动时间
     */
    public static final String QDSJ = "qdsj";
    /**
     * 保护动作时间
     */
    public static final String BHDZSJ = "bhdzsj";
    /**
     * 保护原件类型
     */
    public static final String BHYJLX = "bhyjlx";
    /**
     * 保护动作情况
     */
    public static final String BHDZQK = "bhdzqk";
    /**
     * 断路器检查情况
     */
    public static final String DLQJCQK = "dlqjcqk";
    /**
     * 备注
     */
    public static final String BZ = "bz";
    /***/
    public static final String CREATE_TIME = "create_time";
    /***/
    public static final String DLT = "dlt";
    @Column(name = ID, isId = true)
    public String id;
    @Column(name = REPORTID)
    public String reportid;
    @Column(name = BDZID)
    public String bdzid;
    @Column(name = DLQBH)
    public String dlqbh;
    @Column(name = DLQMC)
    public String dlqmc;
    @Column(name = SBXB)
    public String sbxb;
    @Column(name = SFDZ)
    public String sfdz;
    @Column(name = DZPJ)
    public String dzpj;
    @Column(name = CHZDZQK)
    public String chzdzqk;
    @Column(name = GXTZCS)
    public String gxtzcs;
    @Column(name = GZDL)
    public String gzdl;
    @Column(name = ECGZDL)
    public String ecgzdl;
    @Column(name = HFSDSJ)
    public String hfsdsj;
    @Column(name = ZHYCDXSJ)
    public String zhycdxsj;
    @Column(name = BHZZXH)
    public String bhzzxh;
    @Column(name = QDSJ)
    public String qdsj;
    @Column(name = BHDZSJ)
    public String bhdzsj;
    @Column(name = BHYJLX)
    public String bhyjlx;
    @Column(name = BHDZQK)
    public String bhdzqk;
    @Column(name = DLQJCQK)
    public String dlqjcqk;
    @Column(name = BZ)
    public String bz;
    @Column(name = CREATE_TIME)
    public String createTime;
    @Column(name = DLT)
    public int dlt = 0;


    public static SbjcGztzjlKgtzjl create(String currentReportId, String bdzid) {
        SbjcGztzjlKgtzjl sbjcGztzjlKgtzjl = new SbjcGztzjlKgtzjl();
        sbjcGztzjlKgtzjl.id= FunctionUtil.getPrimarykey();
        sbjcGztzjlKgtzjl.bdzid = bdzid;
        sbjcGztzjlKgtzjl.reportid = currentReportId;
        sbjcGztzjlKgtzjl.createTime = DateUtils.getCurrentLongTime();
        return sbjcGztzjlKgtzjl;
    }
}
