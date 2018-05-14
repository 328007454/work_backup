package com.cnksi.nari;


import android.database.Cursor;
import android.text.TextUtils;

import com.cnksi.bdzinspection.daoservice.PlacedService;
import com.cnksi.bdzinspection.model.Report;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.cnksi.nari.model.BDPackage;
import com.cnksi.nari.utils.GuidUtil;
import com.cnksi.nari.utils.NariDataManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Calendar.DAY_OF_WEEK;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/28 14:27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DataUtils {


    private DbUtils dbUtils;

    private BDPackage bdPackage;

    //巡视计划ID
    private String XSJHID;
    //巡视变电站名称
    private String BDZNAME;
    //巡视班组ID
    private String XSBZID;
    //巡视班组名称
    private String XSBZNAME;
    //巡视记录ID
    private String ZNXSJLID;
    //用户名
    private String ACCOUNT;
    //用户中文名称
    String CURRENT_USERNAME;
    //用户PMSID
    String CURRENT_USERID;
    //作业文本索引信息
    private DbModel ZYWB_SYXX;
    //巡视记录（站内)
    private DbModel ZNXSJH;
    //巡视记录范围（站内)
    private DbModel ZNXSJHFW;
    //站内巡视周期
    private DbModel ZNXSZQ;

    private DbModel USER;


    private Report report;
    private String XSRYIDS;
    private String XSRYNAMES;

    public DataUtils(DbModel account, String xsryIds, String xsryNames, BDPackage bdPackage, Report report) throws DbException {
        this.USER = account;
        this.XSRYIDS = xsryIds;
        this.XSRYNAMES = xsryNames;
        this.XSJHID = bdPackage.pmsJhid;
        this.bdPackage = bdPackage;
        this.report = report;
        dbUtils = NariDataManager.getDbManager(new File(bdPackage.getDatabasePath()));
        init();
    }

    private void init() throws DbException {
        ZYWB_SYXX = dbUtils.findDbModelFirst(new SqlInfo("SELECT * FROM 'T_YJ_DWYJ_BZHZY_ZYWB_SYXX' where GZRWID='" + XSJHID + "';"));
        ZNXSJH = dbUtils.findDbModelFirst(new SqlInfo("SELECT * FROM 'T_YJ_DWYJ_XS_ZNXSJH' where OBJ_ID='" + XSJHID + "';"));
        ZNXSJHFW = dbUtils.findDbModelFirst(new SqlInfo("SELECT * FROM T_YJ_DWYJ_XS_ZNXSJHFW where JHID='" + XSJHID + "';"));
        ZNXSZQ = dbUtils.findDbModelFirst(new SqlInfo("SELECT * FROM T_YJ_DWYJ_XS_ZNXSZQ WHERE OBJ_ID='" + ZNXSJH.getString("ZQID") + "'"));
        XSBZNAME = ZNXSJH.getString("XSBZMC");

        XSBZID = ZNXSJH.getString("XSBZ");
        BDZNAME = ZNXSJH.getString("BDZMC");
        CURRENT_USERID = USER.getString("pms_id");
        CURRENT_USERNAME = USER.getString("pms_name");
        ACCOUNT = USER.getString("pms_account");
    }


    public void Build(String fileName) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement("Entries");
        document.setRootElement(root);
        Element e1 = BuildZNXSJL();
        root.add(BuildZNXSJLFW());
        root.add(e1);
        root.add(BuildSYML());
        root.add(BuildSYXX());
        root.add(BuildZNXSJH());
        for (Element element : BuildXSJG()) {
            root.add(element);
        }
        try {
            if (ZNXSZQ != null)
                root.add(BuildZNXSZQ());
        } catch (Exception e) {
            e.printStackTrace();
        }
        root.add(BuildYXRZJL());
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = new FileWriter(fileName);
        writer.write(document.getRootElement().asXML().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
        writer.close();
    }


    ///STEP1 T_YJ_DWYJ_XS_ZNXSJL
    private Element BuildZNXSJL() {
        String table = "T_YJ_DWYJ_XS_ZNXSJL";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "I", table, ACCOUNT);
        //巡视记录(变电)ID
        setChildE(e, "OBJ_ID", ZNXSJLID = GuidUtil.nextSequence(), false);
        //巡视计划(站内)ID
        setChildE(e, "XSJHID", XSJHID, false);
        //34：交流330kV 33：交流220kV 32：交流110kV 31：交流72.5kV 30：交流66kV 25：交流35kV 24：交流20kV 23：交流15.75kV 22：交流10kV 72：直流1500V 71：直流750V 70：直流600V 60：直流220V 56：直流110V 55：直流48V
        setChildE(e, "DYDJ", ZYWB_SYXX.getString("DYDJ"), false);
        //变电站
        setChildE(e, "BDZ", ZNXSJH.getString("BDZ"), false);
        //变电站名称
        setChildE(e, "BDZMC", ZNXSJH.getString("BDZMC"), false);
        //间隔单元
        setChildE(e, "JGDY", ZNXSJH.getString("JGDY"), false);
        //间隔单元名称
        setChildE(e, "JGDYMC", ZNXSJH.getString("JGDYMC"), false);
        //巡视设备
        setChildE(e, "XSSB", ZNXSJH.getString("XSSB"), false);
        //巡视设备名称
        setChildE(e, "XSSBMC", ZNXSJH.getString("XSSBMC"), false);
        //设备类型
        setChildE(e, "SBLX", ZNXSJH.getString("SBLX"), false);
        //巡视范围
        setChildE(e, "XSFW", ZNXSJH.getString("XSFW"), false);
        //01:正常巡视02:特殊巡视03:例行巡视04:夜间巡视05:故障巡视06:全面巡视07:专业巡视08:熄灯巡视09:监察巡视
        setChildE(e, "XSLX", ZNXSJH.getString("XSLX"), false);
        //巡视班组
        setChildE(e, "XSBZ", XSBZID, false);
        //巡视班组名称
        setChildE(e, "XSBZMC", XSBZNAME, false);
        //巡视人员IDS
        setChildE(e, "XSRYIDS", XSRYIDS, false);
        //巡视人员名称
        setChildE(e, "XSRYMC", XSRYNAMES, false);
        //重点巡视设备数量
        setChildE(e, "ZDXSSBSL", ZNXSJH.getString("ZDXSSBSL"), false);
        //巡视结果
        setChildE(e, "XSJG", "正常", false);
        //计划巡视时间
        setChildE(e, "JHXSSJ", ZNXSJH.getString("JHXSSJ"), false);
        //巡视开始时间  report.start_time
        setChildE(e, "XSKSSJ", report.starttime, false);
        //巡视结束时间  report.end_time
        setChildE(e, "XSJSSJ", report.endtime, false);
        //(01	晴02	阴03	多云04	小雨05	中雨06	大雨07	暴雨08	雷10	霜冻11	小雪12	中雪13	大雪14	雨夹雪15	雾16	浓雾17	冰冻18	雹灾19	大风20	热带风暴21	强热带风暴22	龙卷风23	飑线风24	台风25	沙尘暴26	扬尘27	洪水28	泥石流29	地震31	暴雪32	冻雨33	霾99	其他34	雾霾)
//        setChildE(e, "TQ", TQEnum.findCode(report.tq), true);
        setChildE(e, "TQ", report.tq, true);
        //气温 report.temp...
        setChildE(e, "QW", report.temperature, false);
        //发现缺陷数
        setChildE(e, "FXQXS", null, false);
        //发现隐患数
        setChildE(e, "FXYHS", null, false);
        //巡视是否完成是为'1',否为'0'
        setChildE(e, "XSSFWC", "1", false);
        //巡视未完成原因
        setChildE(e, "XSWWCYY", null, false);
        //登记人 取第一登录人
        setChildE(e, "DJR", CURRENT_USERID, false);
        //巡视人数
        setChildE(e, "XSRS", "1", false);
        //登记人名称
        setChildE(e, "DJRMC", CURRENT_USERNAME, false);
        //登记时间 当前时间
        setChildE(e, "DJSJ", DateUtils.getCurrentLongTime(), false);
        //备注 report。InspectionRemark
        setChildE(e, "BZ", report.inspectionRemark, false);
        //故障ID
        setChildE(e, "GZID", null, true);
        //故障名称
        setChildE(e, "GZMC", null, true);
        //交接班主键 T_YJ_DWYJ_YXRZ_JJBJL.OBJID  使用班组ID可查询
        setChildE(e, "JJBZJ", findJJBZJ(XSBZID), false);
        //
        setChildE(e, "XSNR", report.inspectionContent, false);
        //null
        setChildE(e, "FJSL", null, true);
        //专业分类
        setChildE(e, "ZYFL", "2", false);
        //是否归档
        setChildE(e, "SFGD", "否", false);
        //巡视日期 report.startTime
        setChildE(e, "XSRQ", report.starttime, true);
        //是否使用巡检仪器巡视 1:是，0:否
        setChildE(e, "SFSYXJYQXS", "1", true);
        //特殊巡视类型(01:大风后、02:雷雨后、03:冰雪、04:冰雹后、05:雾霾、06:新投运、07:重新投运、08:设备缺陷发展、09:设备过负载、10:负载剧增、11:超温、12:发热、13:系统冲击、14:跳闸、15:法定节假日、16:保供电、17:电网可靠性下降、18:风险时段)
        setChildE(e, "TSXSLX", null, true);
        //发现的问题
        setChildE(e, "FXWT", null, false);
        //问题处理情况
        setChildE(e, "WTCLQK", null, false);
        //变电站重要等级(01:一类变电站;02:二类变电站;03:三类变电站;04:四类变电站)
        setChildE(e, "BDZZYDJ", ZNXSJH.getString("BDZZYDJ"), false);
        //是否移动作业
        setChildE(e, "SFYDZY", "1", false);
        return e;
    }

    ///STEP2 T_YJ_DWYJ_XS_ZNXSJLFW
    private Element BuildZNXSJLFW() {
        String table = "T_YJ_DWYJ_XS_ZNXSJLFW";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "I", table, ACCOUNT);
        //巡视记录范围(站内)ID
        setChildE(e, "OBJ_ID", GuidUtil.nextSequence(), false);
        //记录id
        setChildE(e, "JLID", ZNXSJLID, false);
        //变电站id
        setChildE(e, "BDZID", ZNXSJH.getString("BDZ"), false);
        //范围类型
        setChildE(e, "FWLX", ZNXSJHFW.getString("FWLX"), false);
        //所选范围
        setChildE(e, "SXFW", ZNXSJHFW.getString("SXFW"), false);
        // 34：交流330kV 33：交流220kV 32：交流110kV 31：交流72.5kV 30：交流66kV 25：交流35kV 24：交流20kV 23：交流15.75kV 22：交流10kV 72：直流1500V 71：直流750V 70：直流600V 60：直流220V 56：直流110V 55：直流48V
        setChildE(e, "DYDJ", ZNXSJHFW.getString("DYDJ"), false);
        //变电站名称
        setChildE(e, "BDZMC", ZNXSJHFW.getString("BDZMC"), false);
        //所选范围id
        setChildE(e, "SXFWID", ZNXSJHFW.getString("SXFWID"), false);
        return e;
    }

    ///STEP3 T_YJ_DWYJ_BZHZY_ZYWB_SYML
    private Element BuildSYML() {
        String table = "T_YJ_DWYJ_BZHZY_ZYWB_SYML";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "I", table, ACCOUNT);
        //作业文本私有目录ID
        setChildE(e, "OBJ_ID", GuidUtil.nextSequence(), false);
        //作业文本ID
        setChildE(e, "ZYWBID", ZYWB_SYXX.getString("OBJ_ID"), false);
        //父目录
        setChildE(e, "SJMLID", "724E8E2B2AFF8080814B7216E4014B724E8E240011", false);
        //排序
        setChildE(e, "PX", "0", false);
        //作业文本型式
        setChildE(e, "ZYWBXS", "1", false);
        //作业文本型式名称
        setChildE(e, "ZYWBXSMC", "索引信息", false);
        //目录显示名称
        setChildE(e, "MLXSMC", "索引信息", false);
        //是否在编制时显示:0:否,1:是
        setChildE(e, "SFZWBBZSXS", "", true);
        //要素编号
        setChildE(e, "YSLX", "35", false);
        return e;
    }

    ///STEP5 T_YJ_DWYJ_BZHZY_ZYWB_SYXX
    private Element BuildSYXX() {
        String table = "T_YJ_DWYJ_BZHZY_ZYWB_SYXX";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "U", table, ACCOUNT);
        //
        if (TextUtils.isEmpty(ZYWB_SYXX.getString("LCSLID")))
            setChildE(e, "LCSLID", "", true);
        else setChildE(e, "LCSLID", ZYWB_SYXX.getString("LCSLID"), true);
        //作业文本名称
        setChildE(e, "ZYWBMC", ZYWB_SYXX.getString("ZYWBMC"), false);
        //null
        setChildE(e, "ZYDM", ZYWB_SYXX.getString("ZYDM"), false);
        //批准人批准人为作业文本审核流程中最后一个用户(完结流程的用户)
        setChildE(e, "PZR", ZYWB_SYXX.getString("PZR"), false);
        //实际工作开始时间 report.start_time
        setChildE(e, "SJKSSJ", report.starttime, false);
        //
        setChildE(e, "BZRWDID", ZYWB_SYXX.getString("BZRWDID"), true);
        //计划工作开始时间
        setChildE(e, "JHKSSJ", ZYWB_SYXX.getString("JHKSSJ"), false);
        //
        setChildE(e, "ZYLX", ZYWB_SYXX.getString("ZYLX"), false);
        //
        setChildE(e, "BZDWMC", ZYWB_SYXX.getString("BZDWMC"), false);
        //批准日期作业文本审批流程结束的日期
        setChildE(e, "PZRQ", ZYWB_SYXX.getString("PZRQ"), false);
        //
        setChildE(e, "SBXS", ZYWB_SYXX.getString("SBXS"), true);
        //
        setChildE(e, "ZYBZMC", ZYWB_SYXX.getString("ZYBZMC"), false);
        //作业文本索引信息ID
        setChildE(e, "OBJ_ID", ZYWB_SYXX.getString("OBJ_ID"), false);
        //
        setChildE(e, "GZCYMC", ZYWB_SYXX.getString("GZCYMC"), false);
        //线路或变电站的ID
        setChildE(e, "GZDD", ZYWB_SYXX.getString("GZDD"), false);
        //编制人
        setChildE(e, "BZR", ZYWB_SYXX.getString("BZR"), false);
        //是否转化为范本:0:否,1:是
        setChildE(e, "SFZHWFB", ZYWB_SYXX.getString("SFZHWFB"), true);
        //// TODO: 2017/7/28  
        //编制方式
        setChildE(e, "BZFS", ZYWB_SYXX.getString("BDFS"), true);
        //
        setChildE(e, "GZRWDID", ZYWB_SYXX.getString("GZRWDID"), true);
        //状态:01:草稿、02:审核中、03:审核完成、04:已执行、05:已评估、06:已作废、07:已转范本
        setChildE(e, "ZT", "04", false);
        //范本转化人
        setChildE(e, "FBZHR", ZYWB_SYXX.getString("FBZHR"), true);
        //
        setChildE(e, "SBLX", ZYWB_SYXX.getString("SBLX"), true);
        //工作负责人
        setChildE(e, "ZFZR", ZYWB_SYXX.getString("ZFZR"), false);
        //null
        setChildE(e, "ZYBZT", ZYWB_SYXX.getString("ZYBZT"), true);
        //工作总结
        setChildE(e, "GZZJ", ZYWB_SYXX.getString("GZZJ"), true);
        //作业文本编制入口
        setChildE(e, "RWLY", ZYWB_SYXX.getString("RWLY"), false);
        //
        setChildE(e, "PZRMC", ZYWB_SYXX.getString("PZRMC"), false);
        //
        setChildE(e, "FBZHRMC", ZYWB_SYXX.getString("FBZHRMC"), true);
        //工作任务单
        setChildE(e, "GZRWID", ZYWB_SYXX.getString("GZRWID"), false);
        //范本转化时间
        setChildE(e, "FBZHSJ", ZYWB_SYXX.getString("FBZHSJ"), true);
        //实际工作结束时间 report.end_time
        setChildE(e, "SJJSSJ", report.endtime, false);
        //编制单位
        setChildE(e, "BZDW", ZYWB_SYXX.getString("BZDW"), false);
        //计划工作结束时间
        setChildE(e, "JHJSSJ", ZYWB_SYXX.getString("JHJSSJ"), false);
        //工作成员
        setChildE(e, "GZCY", ZYWB_SYXX.getString("GZCY"), true);
        //
        setChildE(e, "DYDJ", ZYWB_SYXX.getString("DYDJ"), false);
        //
        setChildE(e, "BZGQ", ZYWB_SYXX.getString("BZGQ"), false);
        //评估意见
        setChildE(e, "PGYJ", ZYWB_SYXX.getString("PGYJ"), true);
        //评估结果:01:优、02:良、03:中、04:差
        setChildE(e, "PGJG", ZYWB_SYXX.getString("PGJG"), true);
        //编制时间
        setChildE(e, "BZSJ", ZYWB_SYXX.getString("BZSJ"), false);
        //超期原因
        setChildE(e, "CQYY", ZYWB_SYXX.getString("CQYY"), false);
        //
        setChildE(e, "GZDDMC", ZYWB_SYXX.getString("GZDDMC"), false);
        //是否多班组卡:0:否,1:是
        setChildE(e, "SFDBZK", ZYWB_SYXX.getString("SFDBZK"), true);
        //作业设备
        setChildE(e, "ZYSB", ZYWB_SYXX.getString("ZYSB"), false);
        //关联多班组卡ID
        setChildE(e, "GLDBZK", ZYWB_SYXX.getString("GLDBZK"), true);
        //作业文本型式
        setChildE(e, "ZYWBXS", ZYWB_SYXX.getString("ZYWBXS"), false);
        //null
        setChildE(e, "BZ", report.inspectionRemark, false);
        //
        setChildE(e, "GZFZRMC", ZYWB_SYXX.getString("GZFZRMC"), true);
        //离线作业文本包ID
        setChildE(e, "LXZYWBID", ZYWB_SYXX.getString("LXZYWBID"), true);
        //
        setChildE(e, "BZRMC", ZYWB_SYXX.getString("BZRMC"), false);
        //配合班组
        setChildE(e, "PHBZ", ZYWB_SYXX.getString("PHBZ"), true);
        //null
        setChildE(e, "PHBZMC", ZYWB_SYXX.getString("PHBZMC"), false);
        //作业班组
        setChildE(e, "ZYBZ", ZYWB_SYXX.getString("ZYBZ"), false);
        return e;
    }

    ///STEP6  T_YJ_DWYJ_XS_ZNXSJH
    ///数据来源 T_YJ_DWYJ_XS_ZNXSJH 修改字段`JHZT`,`SFWC`
    private Element BuildZNXSJH() {
        String table = "T_YJ_DWYJ_XS_ZNXSJH";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "U", table, ACCOUNT);
        //间隔单元
        setChildE(e, "JGDY", ZNXSJH.getString("JGDY"), true);
        //巡视班组
        setChildE(e, "XSBZ", ZNXSJH.getString("XSBZ"), false);
        //变电站
        setChildE(e, "BDZ", ZNXSJH.getString("BDZ"), false);
        //01:编制02：已发布03：执行中04：已执行
        setChildE(e, "JHZT", "03", false);
        //负责人名称
        setChildE(e, "FZRMC", ZNXSJH.getString("FZRMC"), false);
        //
        setChildE(e, "XSNR", ZNXSJH.getString("XSNR"), false);
        //周期ID
        setChildE(e, "ZQID", ZNXSJH.getString("ZQID"), true);
        //巡视计划(站内)ID
        setChildE(e, "OBJ_ID", ZNXSJH.getString("OBJ_ID"), false);
        //编制人
        setChildE(e, "BZR", ZNXSJH.getString("BZR"), false);
        //设备类型
        setChildE(e, "SBLX", ZNXSJH.getString("SBLX"), true);
        //巡视班组名称
        setChildE(e, "XSBZMC", ZNXSJH.getString("XSBZMC"), false);
        //负责人
        setChildE(e, "FZR", ZNXSJH.getString("FZR"), false);
        //特殊巡视类型(01:大风后、02:雷雨后、03:冰雪、04:冰雹后、05:雾霾、06:新投运、07:重新投运、08:设备缺陷发展、09:设备过负载、10:负载剧增、11:超温、12:发热、13:系统冲击、14:跳闸、15:法定节假日、16:保供电、17:电网可靠性下降、18:风险时段)
        setChildE(e, "TSXSLX", ZNXSJH.getString("TSXSLX"), true);
        //合并后计划ID
        setChildE(e, "HBHJHID", ZNXSJH.getString("HBHJHID"), true);
        //null
        setChildE(e, "ZYFLS", ZNXSJH.getString("ZYFLS"), false);
        //是否有路线图有的话为T,否则为F
        setChildE(e, "SFYLXT", ZNXSJH.getString("SFYLXT"), true);
        //是否完成
        setChildE(e, "SFWC", "1", false);
        //间隔单元名称
        setChildE(e, "JGDYMC", ZNXSJH.getString("JGDYMC"), true);
        //重点巡视设备数量
        setChildE(e, "ZDXSSBSL", ZNXSJH.getString("ZDXSSBSL"), true);
        //变电站名称
        setChildE(e, "BDZMC", ZNXSJH.getString("BDZMC"), false);
        //01:正常巡视02:特殊巡视03:例行巡视04:夜间巡视05:故障巡视06:全面巡视07:专业巡视08:熄灯巡视09:监察巡视
        setChildE(e, "XSLX", ZNXSJH.getString("XSLX"), false);
        //巡视设备名称
        setChildE(e, "XSSBMC", ZNXSJH.getString("XSSBMC"), true);
        //计划巡视时间
        setChildE(e, "JHXSSJ", ZNXSJH.getString("JHXSSJ"), false);
        //巡视设备
        setChildE(e, "XSSB", ZNXSJH.getString("XSSB"), true);
        // 34：交流330kV 33：交流220kV 32：交流110kV 31：交流72.5kV 30：交流66kV 25：交流35kV 24：交流20kV 23：交流15.75kV 22：交流10kV 72：直流1500V 71：直流750V 70：直流600V 60：直流220V 56：直流110V 55：直流48V
        setChildE(e, "DYDJ", ZNXSJH.getString("DYDJ"), false);
        //设备安全码
        setChildE(e, "SBAQM", ZNXSJH.getString("SBAQM"), true);
        //编制时间
        setChildE(e, "BZSJ", ZNXSJH.getString("BZSJ"), false);
        //是否合并计划是为T,否为F
        setChildE(e, "SFHBJH", ZNXSJH.getString("SFHBJH"), false);
        //巡视范围
        setChildE(e, "XSFW", ZNXSJH.getString("XSFW"), true);
        //变电站重要等级(01:一类变电站;02:二类变电站;03:三类变电站;04:四类变电站)
        setChildE(e, "BDZZYDJ", ZNXSJH.getString("BDZZYDJ"), false);
        //备注
        setChildE(e, "BZ", ZNXSJH.getString("BZ"), true);
        //编制人名称
        setChildE(e, "BZRMC", ZNXSJH.getString("BZRMC"), false);
        //离线作业包状态
        setChildE(e, "LXZYBZT", ZNXSJH.getString("LXZYBZT"), false);
        return e;
    }

    ///STEP7 T_YJ_DWYJ_BZHZY_ZYWB_XSJG
    ///生成巡视结果记录。数据来源T_YJ_DWYJ_BZHZY_ZYWB_XSJG，修改字段`XSSJ`,`XSJG`,`XSR`。
    private List<Element> BuildXSJG() {
        String table = "T_YJ_DWYJ_BZHZY_ZYWB_XSJG";
        Map<String, String> modelMap = PlacedService.getInstance().findPmsPlaced(report.reportid);
        List<Element> rs = new ArrayList<>();
        try {
            Cursor cursor = dbUtils.execQuery("select * from " + table + " where ZYWBID='" + ZYWB_SYXX.getString("OBJ_ID") + "'");
            while (cursor.moveToNext()) {
                Element e = DocumentHelper.createElement("Entry");
                setEntryAttr(e, "U", table, ACCOUNT);

                int colunmCount = cursor.getColumnCount();
                int index = cursor.getColumnIndex("XSDDID");
                String XSSJ = null;
                if (index > 0) {
                    String spid = cursor.getString(index);
                    XSSJ = modelMap.get(spid);
                }
                if (!TextUtils.isEmpty(XSSJ)) {
                    for (int i = 0; i < colunmCount; i++) {
                        String name = cursor.getColumnName(i);
                        String txt = cursor.getString(i);

                        if ("XSSJ".equals(name))
                            txt = XSSJ; //placed.create_time
                        if ("XSJG".equals(name)) txt = "正常";
                        if ("XSR".equals(name)) txt = CURRENT_USERNAME;
                        setChildE(e, name, txt, false);
                    }
                    rs.add(e);
                }
            }
        } catch (DbException e1) {
            e1.printStackTrace();
        }
        return rs;
    }

    //STEP8 T_YJ_DWYJ_XS_ZNXSZQ
    private Element BuildZNXSZQ() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = Integer.parseInt(ZNXSZQ.getString("XSZQ"));
        String zqdw = ZNXSZQ.getString("ZQDW");
        Date xsdqsjDate = sdf.parse(ZNXSZQ.getString("XSDQSJ"));
        Date scxssjDate = sdf.parse(report.endtime);
        String new_xsdqsj = sdf.format(addDate(scxssjDate, count, zqdw));
        if (!isUpdateCycle(scxssjDate, xsdqsjDate, -count, zqdw)) {
            return null;
        }

        String table = "T_YJ_DWYJ_XS_ZNXSZQ";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "U", table, ACCOUNT);
        setChildE(e, "OBJ_ID", ZNXSZQ.getString("OBJ_ID"));
        setChildE(e, "DYDJ", ZNXSZQ.getString("DYDJ"));
        setChildE(e, "BDZ", ZNXSZQ.getString("BDZ"));
        setChildE(e, "BDZMC", ZNXSZQ.getString("BDZMC"));
        setChildE(e, "JGDY", ZNXSZQ.getString("JGDY"));
        setChildE(e, "JGDYMC", ZNXSZQ.getString("JGDYMC"));
        setChildE(e, "XSSB", ZNXSZQ.getString("XSSB"));
        setChildE(e, "XSSBMC", ZNXSZQ.getString("XSSBMC"));
        setChildE(e, "XSFW", ZNXSZQ.getString("XSFW"));
        setChildE(e, "XSLX", ZNXSZQ.getString("XSLX"));
        setChildE(e, "XSBZ", ZNXSZQ.getString("XSBZ"));
        setChildE(e, "XSBZMC", ZNXSZQ.getString("XSBZMC"));
        setChildE(e, "XSZQ", ZNXSZQ.getString("XSZQ"));
        setChildE(e, "ZQDW", ZNXSZQ.getString("ZQDW"));
        setChildE(e, "SCXSSJ", report.endtime);
        setChildE(e, "TQBJTS", ZNXSZQ.getString("TQBJTS"));
        setChildE(e, "XSDQSJ", new_xsdqsj);
        setChildE(e, "ZTPJJG_ID", ZNXSZQ.getString("ZTPJJG_ID"));
        setChildE(e, "ZDXSYY", ZNXSZQ.getString("ZDXSYY"));
        setChildE(e, "BZ", ZNXSZQ.getString("BZ"));
        setChildE(e, "SBLX", ZNXSZQ.getString("SBLX"));
        setChildE(e, "BZR", ZNXSZQ.getString("BZR"));
        setChildE(e, "BZRMC", ZNXSZQ.getString("BZRMC"));
        setChildE(e, "BZSJ", ZNXSZQ.getString("BZSJ"));
        setChildE(e, "XSNR", ZNXSZQ.getString("XSNR"));
        setChildE(e, "JLXSSJ", ZNXSZQ.getString("JLXSSJ"));
        setChildE(e, "JLDQSJ", ZNXSZQ.getString("JLDQSJ"));
        return e;
    }

    //STEP9 T_YJ_DWYJ_YXRZ_YXRZJL
    private Element BuildYXRZJL() {
        String table = "T_YJ_DWYJ_YXRZ_YXRZJL";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "I", table, ACCOUNT);
        //运行日志记录_ID
        setChildE(e, "OBJ_ID", GuidUtil.nextSequence(), false);
        //交接班主键
        setChildE(e, "JJBZJ", findJJBZJ(XSBZID), false);
        //关联记录类型  T_YJ_DWYJ_YXRZ_YXJLGSPZ表可查询
        setChildE(e, "GLJLLX", "402881be4105b38a014105bebd9c0002", false);
        //关联记录ID
        setChildE(e, "GLJL_ID", ZNXSJLID, false);
        //记录日期
        setChildE(e, "JLRQ", DateUtils.getCurrentLongTime(), false);
        //记录内容
        setChildE(e, "JLNR", buildLog(), false);
        //是否写记事
        setChildE(e, "SFJS", "1", false);
        //记录人ID
        setChildE(e, "JLRID", CURRENT_USERID, false);
        //记录人名称
        setChildE(e, "JLRMC", CURRENT_USERNAME, false);
        //单位或变电站ID
        setChildE(e, "DWHBDZID", XSBZID, false);
        //单位或变电站名称
        setChildE(e, "DWHBDZMC", XSBZNAME, false);
        //发生时间 report.start_time
        setChildE(e, "FSSJ", report.starttime, false);
        //故障信息关联
        setChildE(e, "GZXXGL", null, true);
        //是否完成
        setChildE(e, "SFWC", null, true);
        //记录单位
        setChildE(e, "JLDW", null, true);
        //票状态
        setChildE(e, "PZT", null, true);
        //null
        setChildE(e, "ZT", null, true);
        return e;
    }


    //  private Element GenerateQXCommonAttr()

    //STEP10 T_YJ_DWYJ_QXJL
    private List<Element> BuildQXJL() {
        String table = "T_YJ_DWYJ_QXJL";
        Element e = DocumentHelper.createElement("Entry");
        setEntryAttr(e, "I", table, ACCOUNT);
        //具体部件
        setChildE(e, "JTBJ", "", true);
        //流程实例ID
        setChildE(e, "LCSLID", "", true);
        //01：护线员发现、02：人工巡视、03机器人巡视、05：在线监测、07监控告警、08基建遗留、09：检修试验、10：带点监测、13评价检测、04：航巡、11故障、06：调度监控、12家族性缺陷
        setChildE(e, "FXLYLX", "02", false);
        //附件数量
        setChildE(e, "FJSL", "", true);
        //状态量编码
        setChildE(e, "ZTLBM", "", true);
        //所属上级 设备为间隔ID
        setChildE(e, "SSSJ", "555870A8-8952-3096-E043-C0A80B013096-35410", false);
        //建议检修类别
        setChildE(e, "JYJXLB", "", true);
        //发现人ID
        setChildE(e, "FXRID", CURRENT_USERID, false);
        //对应缺陷标准库
        setChildE(e, "QXMS", "", false);
        //生产厂家
        setChildE(e, "SCCJ", "西安熔断器制造公司", false);
        //发现班组
        setChildE(e, "FXBZ", XSBZNAME, false);
        //缺陷记录_ID
        setChildE(e, "OBJ_ID", GuidUtil.nextSequence(), false);
        //扣分值
        setChildE(e, "KFZ", "", true);
        //处理详情
        setChildE(e, "CLXQ", "", true);
        //专业细分
        setChildE(e, "ZYXF", "", true);
        //检修建议
        setChildE(e, "JXJY", "", true);
        //延期原因
        setChildE(e, "YQYY", "", true);
        //发现人单位
        setChildE(e, "FXRDW", "乐山运维检修部(检修分公司)", false);
        //消缺人ID
        setChildE(e, "XQRID", "", true);
        //验收人ID
        setChildE(e, "YSRID", "", true);
        //设备类型
        setChildE(e, "SBLX", "0309", false);
        //缺陷描述名称
        setChildE(e, "QXMSMC", "", false);
        //验收单位
        setChildE(e, "YSDW", "", true);
        //登记单位
        setChildE(e, "DJDW", "乐山运维检修部(检修分公司)", false);
        //验收班组ID
        setChildE(e, "YSBZID", "", true);
        //发现日期
        setChildE(e, "FXRQ", "2017-08-02 08:51:06", false);
        //建议检修时间
        setChildE(e, "JYJXSJ", "", true);
        //缺陷部位
        setChildE(e, "QXBW", "", false);
        //等级班组
        setChildE(e, "DJBZ", "变电运维一班", false);
        //验收时间
        setChildE(e, "YSSJ", "", true);
        //SFLYYDZD
        setChildE(e, "SFLYYDZD", "1", false);
        //责任原因
        setChildE(e, "ZRYY", "", true);
        //是否已转隐患
        setChildE(e, "SFYZYH", "0", false);
        //遗留问题
        setChildE(e, "YLWT", "", true);
        //缺陷性质
        setChildE(e, "QXXZ", "03", false);
        //发现来源表名
        setChildE(e, "FXLYBM", "T_YJ_DWYJ_XS_ZNXSJL", false);
        //是否已消缺
        setChildE(e, "SFXQ", "0", false);
        //扣分说明
        setChildE(e, "KFSM", "", true);
        //消缺人
        setChildE(e, "XQR", "", true);
        //典型特征
        setChildE(e, "DXTZ", "", false);
        //验收意见
        setChildE(e, "YSYJ", "", true);
        //缺陷内容
        setChildE(e, "QXNR", "35kV全福变电站35kV站用变高压熔断器", false);
        //登记人
        setChildE(e, "DJR", "胡亮", false);
        //汇报调度情况
        setChildE(e, "HBDDQK", "", false);
        //01一次、02保护、03自动化、04其他
        setChildE(e, "ZYXZ", "", true);
        //登记时间
        setChildE(e, "DJSJ", "2017-08-02 08:51:28", false);
        //技术原因
        setChildE(e, "JSYY", "", true);
        //状态量
        setChildE(e, "ZTL", "", true);
        //缺陷所在单位
        setChildE(e, "QXSZDW", "16F37FCECCCF0ED1E0530100007F2F45", false);
        //是否延期消缺
        setChildE(e, "SFYQXQ", "0", false);
        //缺陷编号 自动生成
        setChildE(e, "QXBH", "QX20170802444", false);
        //1：是 0：否
        setChildE(e, "SFPWSB", "0", false);
        //等级单位ID
        setChildE(e, "DJDWID", "16F37FCECCC90ED1E0530100007F2F45", false);
        //验收班组
        setChildE(e, "YSBZ", "", true);
        //(37 交流1000kV36 交流750kV35 交流500kV34 交流330kV33 交流220kV32 交流110kV31 交流72.5kV30 交流66kV25 交流35kV24 交流20kV23 交流15.75kV22 交流10kV21 交流6kV20 交流3kV15 交流2500V14 交流3000V13 交流1500V12 交流750V11 交流600V10 交流1000V（含1140V）09 交流660V08 交流380V（含400V）07 交流220V06 交流110V05 交流48V04 交流36V03 交流24V02 交流12V01 交流6V86 直流1000kV85 直流800kV84 直流660kV83 直流500kV82 直流400kV88 直流320kV87 直流200kV81 直流125kV80 直流120kV78 直流50kV77 直流30kV73 直流3000V72 直流1500V
        setChildE(e, "DYDJ", "25", false);
        //消缺班组ID
        setChildE(e, "XQBZID", "", true);
        //部件类型
        setChildE(e, "BJLX", "", false);
        //对应标准缺陷设备类型名称
        setChildE(e, "DYBZQXSBLXMC", "", true);
        //设备双重名称
        setChildE(e, "SBSCMC", "", false);
        //消缺单位
        setChildE(e, "XQDW", "", true);
        //家族性缺陷ID
        setChildE(e, "JZXQXID", "", true);
        //01电站 02 线路
        setChildE(e, "DZORXL", "55532245-32EB-505E-E043-C0A80B01505E-01104", false);
        //缺陷状态
        setChildE(e, "QXZT", "缺陷登记", false);
        //发现人
        setChildE(e, "FXR", "胡亮", false);
        //对应标准缺陷设备类型
        setChildE(e, "DYBZQXSBLX", "0309", false);
        //发现班组ID
        setChildE(e, "FXBZID", "16F37FCECCCF0ED1E0530100007F2F45", false);
        //护线员姓名
        setChildE(e, "HXYXM", "", true);
        //消缺单位ID
        setChildE(e, "XQDWID", "", true);
        //发现来源ID
        setChildE(e, "FXLYID", "ED1B931D-EB27-40DB-BC96-97E199AD936C-00031", false);
        //投运日期
        setChildE(e, "TYRQ", "2000-12-11 00:00:00", false);
        //01：电站 02 线路
        setChildE(e, "ZXLX", "02", false);
        //验收人
        setChildE(e, "YSR", "", true);
        //部件种类
        setChildE(e, "BJZL", "", false);
        //验收是否合格
        setChildE(e, "YSSFHG", "0", false);
        //分类依据名称
        setChildE(e, "FLYJMC", "", false);
        //验收单位ID
        setChildE(e, "YSDWID", "", true);
        //监控告警级别
        setChildE(e, "JKGJJB", "", true);
        //是否已验收
        setChildE(e, "SFYYS", "0", false);
        //消缺日期
        setChildE(e, "XQRQ", "", true);
        //发现人单位ID
        setChildE(e, "FXRDWID", "16F37FCECCC90ED1E0530100007F2F45", false);
        //评价结果
        setChildE(e, "PJJG", "", true);
        //电站或者线路名称
        setChildE(e, "DZORXLMC", "35kV全福变电站", false);
        //登记人ID
        setChildE(e, "DJRID", "E3FA8F8801F12D91E040B00AD3034D6C", false);
        //设备种类
        setChildE(e, "SBZL", "", false);
        //备注
        setChildE(e, "BZ", "", false);
        //消缺班组
        setChildE(e, "XQBZ", "", true);
        //缺陷主设备
        setChildE(e, "QXZSB", "2997AC73-CB27-4A71-A6E6-6DF000481E1F-13267", false);
        //等级班组ID
        setChildE(e, "DJBZID", "16F37FCECCCF0ED1E0530100007F2F45", false);
        //1：是 0：否
        setChildE(e, "SFJZXQX", "0", false);
        //汇报监控情况
        setChildE(e, "HBJKQK", "", false);
        //分类依据
        setChildE(e, "FLYJ", "", false);
        //缺陷主设备名称
        setChildE(e, "QXZSBMC", "35kV站用变高压熔断器", false);
        //设备型号
        setChildE(e, "SBXH", "RN2-35", false);
        return new ArrayList<>();
    }

    private String buildLog() {
        //report.start_time
        return XSBZNAME + "的" + XSRYNAMES + "对" + BDZNAME + "于" + DateUtils.getCurrentLongTime() + "进行了" + bdPackage.inspectionType;
    }

    private String findJJBZJ(String bzId) {
        try {
            DbModel model = dbUtils.findDbModelFirst(new SqlInfo("SELECT OBJ_ID FROM T_YJ_DWYJ_YXRZ_JJBJL where JJDWID='" + bzId + "';"));
            if (model != null) return model.getString("OBJ_ID");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void setEntryAttr(Element e, String action, String tableName, String username) {
        e.addAttribute("action", action);
        e.addAttribute("tableName", tableName);
        e.addAttribute("modifyUserName", username);
    }

    private void setChildE(Element element, String name, String txt, boolean isEmpty) {
        Element e = element.addElement(name);
        if (TextUtils.isEmpty(txt) && isEmpty) e.addAttribute("isEmpty", "true");
        else e.setText(StringUtils.EmptyTo(txt, ""));
    }

    private void setChildE(Element element, String name, String txt) {
        setChildE(element, name, txt, txt == null);
    }

    private Date addDate(Date date, int day, String zqdw) throws ParseException {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        if ("01".equals(zqdw)) {
            ca.add(Calendar.DAY_OF_MONTH, day);
        } else if ("02".equals(zqdw)) {
            ca.add(Calendar.MONTH, day);
            Calendar d1 = Calendar.getInstance();
            d1.setTime(date);
            if (isMonthEnd(date)) {
                int i = ca.getActualMaximum(Calendar.DAY_OF_MONTH) - d1.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (i > 0) {
                    ca.add(Calendar.DAY_OF_MONTH, i);
                }
            }
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if ("03".equals(zqdw)) {
            int d;
            ca.add(DAY_OF_WEEK, day * 7);
            if (ca.get(DAY_OF_WEEK) == 1) {
                d = -6;
            } else {
                d = 2 - ca.get(DAY_OF_WEEK);
            }
            ca.add(DAY_OF_WEEK, d);
            ca.add(DAY_OF_WEEK, 6);
        } else if ("04".equals(zqdw)) {
            ca.add(Calendar.YEAR, day);
            ca.set(Calendar.DAY_OF_YEAR, ca.getActualMaximum(Calendar.DAY_OF_YEAR));
        }
        ca.set(Calendar.HOUR_OF_DAY, 23);
        ca.set(Calendar.MINUTE, 59);
        ca.set(Calendar.SECOND, 59);
        return ca.getTime();
    }


    private boolean isMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DATE) == cal.getActualMaximum(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    public boolean isUpdateCycle(Date scxssjDate, Date xsdqsjDate, int day, String zqdw) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(xsdqsjDate);
        if ("01".equals(zqdw)) {
            ca.add(Calendar.DAY_OF_MONTH, day);
        } else if ("02".equals(zqdw)) {
            ca.add(Calendar.MONTH, day);
            Calendar d1 = Calendar.getInstance();
            d1.setTime(xsdqsjDate);
            if (isMonthEnd(xsdqsjDate)) {
                int i = ca.getActualMaximum(Calendar.DAY_OF_MONTH) - d1.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (i > 0) {
                    ca.add(Calendar.DAY_OF_MONTH, i);
                }
            }
        } else if ("03".equals(zqdw)) {
            ca.add(Calendar.DAY_OF_MONTH, day * 7);
        } else if ("04".equals(zqdw)) {
            ca.add(Calendar.YEAR, day);
        }
        return scxssjDate.after(ca.getTime());
    }

}
