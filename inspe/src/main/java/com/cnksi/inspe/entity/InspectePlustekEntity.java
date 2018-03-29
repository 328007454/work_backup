package com.cnksi.inspe.entity;

import com.cnksi.inspe.base.BaseEntity;

/**
 * 精益化检查实体类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 21:14
 */

public class InspectePlustekEntity extends BaseEntity {
    /**
     * 类型ID
     */
    public int id;
    /**
     * ico资源ID
     */
    public int resId;
    /**
     * 类型描述
     */
    public String title;

    public InspectePlustekEntity(int id, int resId, String desc) {
        this.id = id;
        this.resId = resId;
        this.title = desc;
    }


}
