package com.cnksi.inspe.entity;

import com.cnksi.inspe.base.BaseEntity;
import com.cnksi.inspe.type.PlustekType;

/**
 * 精益化评价实体类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 21:14
 */

public class InspectePlustekEntity extends BaseEntity {
    /**
     * 类型ID
     */
    public PlustekType type;
    /**
     * ico资源ID
     */
    public int resId;
    /**
     * 类型描述
     */
    public String title;

    public InspectePlustekEntity(PlustekType type, int resId, String desc) {
        this.type = type;
        this.resId = resId;
        this.title = desc;
    }


}
