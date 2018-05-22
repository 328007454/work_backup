package com.cnksi.common.enmu;

/**
 * 巡视类型
 */
public enum InspectionType {
    SBXS("设备巡视"),
    GZP("工作票"),
    operation("运维一体化"),
    full("全面巡检"),
    day("日常巡检"),
    routine("例行巡视"),
    special("特殊巡检"),
    special_wind("大风天气特殊巡检"),
    battery("蓄电池测试"),
    newproduct("设备新投巡检"),
    special_guzhang("事故后特殊巡检"),
    accept("设备验收巡检"),
    special_fog("大雾天气特殊巡检"),
    special_snow("下雪天气特殊巡检"),
    special_thunderstorm("雷雨后特殊巡检"),
    special_hyperthermia("高温天气特殊巡检"),
    special_nighttime("夜间巡视"),
    special_xideng("熄灯巡视"),
    infrared("红外线测温"),
    switchover("定期切换试验"),
    maintenance("定期维护"),
    switchover_01("监控系统音响报警试验检查"),
    switchover_02("录音系统试验"),
    switchover_03("火灾报警系统试验"),
    switchover_04("电子围栏报警试验"),
    switchover_05("事故照明交直流切换 "),
    switchover_06("变压器冷却器工作电源切换"),
    switchover_07("UPS逆变电源切换"),
    switchover_08("漏电保护装置试验"),
    switchover_09("视频监控系统试验"),
    switchover_10("保护、录波、测距、监控系统校时"),
    switchover_11("微机防误装置试验"),
    switchover_12("变压器备用、辅助、工作风机切换"),
    switchover_13("设备驱潮装置检查试验"),
    switchover_15("抽风装置试验"),
    switchover_16("高频通道测试"),
    switchover_17("站用变电源切换试验"),
    switchover_18("直流系统交流断电试验"),
    maintenance_01("全站卫生清洁"),
    maintenance_02("交、直流空开、保险检查"),
    maintenance_03("全站照明检查"),
    maintenance_04("消防设施检查、清洁"),
    maintenance_05("蓄电池清扫"),
    maintenance_06("备品、备件、工器具、仪表清洁"),
    maintenance_07("安全工器具、防护用品检查、维护"),
    maintenance_08("通讯设备检查"),
    maintenance_09("防小动物孔洞检查"),
    maintenance_10("设备构架、接地装置锈蚀检查"),
    maintenance_11("五防系统维护"),
    maintenance_12("电缆沟翻查、防火封堵检查"),
    maintenance_13("给、排水系统检查、维护"),
    maintenance_14("保护压板及定值核对"),
    maintenance_15("机构箱（端子箱）门活页、锁具加油"),
    maintenance_16("检查更新设备标志"),
    maintenance_17("空调维护"),
    maintenance_18("各抽风机卫生清扫"),
    maintenance_19("更换老鼠药、沾鼠板"),
    exclusive("专项巡视"),
    professional("专业巡视"),
    exclusive_01("红外线测温"),
    exclusive_02("防火封堵专项检查"),
    exclusive_03("继电保护定置专项核对"),
    exclusive_04("防污闪专项检查"),
    exclusive_05("开关柜隐患专项排查"),
    exclusive_06("GIS隐患专项排查"),
    exclusive_07("防小动物专项检查"),
    exclusive_08("五防系统专项检查"),

    SBJC("数据抄录"),
    NEWSBJC("数据抄录"),
    //    SBJC_01("红外测温"),
    SBJC_02("保护屏红外热像检测"),
    SBJC_03("室内温湿度记录"),
    SBJC_04("差动保护差流记录"),
    SBJC_05("交直流变压器分接开关调整记录"),
    SBJC_06("压力检查"),
    SBJC_06_gas("空气压力检测"),
    SBJC_06_sf6("SF6气体压力检测"),
    SBJC_06_water("液体压力检测"),
    SBJC_07("避雷器动作次数"),
    SBJC_08("避雷器在线监测"),
    SBJC_09("防小动物措施检查"),
    SBJC_10("蓄电池检测记录"),
    SBJC_11("蓄电池内阻检测记录"),
    SBJC_12("收发信机测试"),
    SBJC_13("设备测温"),
    SBJC_KGGZTZJL("开关故障跳闸"),
    JYHYS("精益化验收"),
    JYHPJ("精益化评价"),
    TJWT("教育培训");


    public final String value;

    InspectionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }


    public static InspectionType get(String value) {
        return InspectionType.valueOf(value);
    }


}