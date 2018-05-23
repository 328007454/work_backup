package com.cnksi.common.model.vo;

public class DefectInfo {
        public String deviceId;
        public String defectLevel;
        public String defectCount;

        /**
         * @param deviceId
         * @param defectLevel
         * @param defectCount
         */
        public DefectInfo(String deviceId, String defectLevel, String defectCount) {
            super();
            this.deviceId = deviceId;
            this.defectLevel = defectLevel;
            this.defectCount = defectCount;
        }

    }