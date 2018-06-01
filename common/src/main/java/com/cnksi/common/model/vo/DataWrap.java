package com.cnksi.common.model.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/5/18 17:10
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DataWrap<T1, T2> {
    private T1 obj;
    private List<T2> childs;

    public DataWrap(T1 obj) {
        this.obj = obj;
    }

    public DataWrap(T1 obj, List<T2> childs) {
        this.obj = obj;
        this.childs = childs;
    }

    public List<T2> getChildList() {
        return childs;
    }

    public void setChildList(List<T2> childs) {
        this.childs = childs;
    }

    public void addChild(T2 child) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(child);
    }

    public T2 get(int position) {
        return childs.get(position);
    }

    public T1 getObj() {
        return obj;
    }

    public void setObj(T1 obj) {
        this.obj = obj;
    }

    public int getChildCount() {
        return childs == null ? 0 : childs.size();
    }
}
