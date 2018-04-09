package com.cnksi.inspe.adapter.entity;

import com.cnksi.inspe.db.entity.DeviceTypeEntity;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 15:59
 */
public class DeviceTypeCheckEntity {
    public DeviceTypeEntity entity;
    //类型ID
    public int typeId;
    //类型Name
    public String typeName;
    //是否被选中，true被选中，false未选中
    public boolean isChecked = false;

    public DeviceTypeCheckEntity() {
    }

    public DeviceTypeCheckEntity(DeviceTypeEntity entity, int typeId, String typeName) {
        this.entity = entity;
        this.typeId = typeId;
        this.typeName = typeName;
    }
}
