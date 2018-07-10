package com.cnksi.common.model.vo;

public class DefectInfo {
        public String deviceId;
        public String defectLevel;
        public String defectCount;
        public String spid;

        /**
         * @param deviceId
         * @param defectLevel
         * @param defectCount
         */
        public DefectInfo(String deviceId, String defectLevel, String defectCount,String spid) {
            super();
            this.deviceId = deviceId;
            this.defectLevel = defectLevel;
            this.defectCount = defectCount;
            this.spid = spid;
        }

    }