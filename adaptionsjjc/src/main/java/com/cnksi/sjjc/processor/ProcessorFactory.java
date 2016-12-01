package com.cnksi.sjjc.processor;

import com.cnksi.sjjc.enmu.InspectionType;

/**
 * Created by wastrel on 2016/6/14.
 */
public class ProcessorFactory {
    public static CopyDataInterface getProcessor(String inspection,String reportId)
    {
        if (InspectionType.SBJC_06_sf6.name().equals(inspection))
        {
            return new PressSF6Processor(reportId);
        }else if (InspectionType.SBJC_06_gas.name().equals(inspection))
        {
            return new PressGasProcessor(reportId);
        }else if (InspectionType.SBJC_06_water.name().equals(inspection))
        {
            return new PressWaterProcessor(reportId);
        }else if (InspectionType.SBJC_07.name().equals(inspection))
        {
            return new ArresterActionProcessor(reportId);
        }else if (InspectionType.SBJC_08.name().equals(inspection))
        {
            return new ArresterCheckOLProcessor(reportId);
        }else{
            throw new RuntimeException("不支持的巡检类型");
        }
    }

}
