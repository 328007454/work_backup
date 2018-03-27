package com.cnksi.inspe.entity;

/**
 * 扣分标准实体类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/26 15:48
 */

public class InspeScoreEntity {
    /**
     * 扣分描述
     */
    public String content;
    /**
     * 扣分值
     */
    public float score;

    public InspeScoreEntity() {
    }

    public InspeScoreEntity(String content) {
        this.content = content;
    }
}
