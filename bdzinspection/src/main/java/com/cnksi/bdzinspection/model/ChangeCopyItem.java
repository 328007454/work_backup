package com.cnksi.bdzinspection.model;

/**
 * Created by han on 2017/5/14.
 */

public class ChangeCopyItem {
    private CopyItem item;
    private String val;
    private String val_a;
    private String val_b;
    private String val_c;
    private String val_o;
    private String val_special;
    private int type;
    private CopyResult result;
    private int parentPositoin;
    private int version;

    public ChangeCopyItem(CopyResult originResult, CopyItem item, String val, String val_a, String val_b, String val_c, String val_o) {
        this.item = item;
        this.val = val;
        this.val_a = val_a;
        this.val_b = val_b;
        this.val_c = val_c;
        this.val_o = val_o;
        this.result = originResult;
    }

    public int getParentPositoin() {
        return parentPositoin;
    }

    public void setParentPositoin(int parentPositoin) {
        this.parentPositoin = parentPositoin;
    }

    public ChangeCopyItem(CopyItem item, int type, int parentPositoin) {
        this.item = item;
        this.type = type;

    }

    public ChangeCopyItem(CopyResult result, CopyItem item, String val, String val_a, String val_b, String val_c, String val_o, int version) {
        this.item = item;
        this.val = val;
        this.val_a = val_a;
        this.val_b = val_b;
        this.val_c = val_c;
        this.val_o = val_o;
        this.result = result;
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    /**
     * 通过数据结构转化，将原来的item对应的抄录字段分别判断是否需要重新赋值，并且判断是否全部置为N
     *
     * @param changeCopyItem 抄录项状态已改变
     */

    public boolean setCopyItemVal(ChangeCopyItem changeCopyItem) {
        CopyItem copyItem = changeCopyItem.getItem();
        if ("Y".equalsIgnoreCase(changeCopyItem.val)) {
            copyItem.val = "N";
            changeCopyItem.val = "N";
        } else if ("Y".equalsIgnoreCase(changeCopyItem.val_a)) {
            copyItem.val_a = "N";
            changeCopyItem.val_a = "N";
        } else if ("Y".equalsIgnoreCase(changeCopyItem.val_b)) {
            copyItem.val_b = "N";
            changeCopyItem.val_b = "N";
        } else if ("Y".equalsIgnoreCase(changeCopyItem.val_c)) {
            copyItem.val_c = "N";
            changeCopyItem.val_c = "N";
        } else if ("Y".equalsIgnoreCase(changeCopyItem.val_o)) {
            copyItem.val_o = "N";
            changeCopyItem.val_o = "N";
        }
        if ("N".equalsIgnoreCase(copyItem.val) && "N".equalsIgnoreCase(copyItem.val_a) && "N".equalsIgnoreCase(copyItem.val_b) &&
                "N".equalsIgnoreCase(copyItem.val_c) && "N".equalsIgnoreCase(copyItem.val_o)) {
            copyItem.dlt = 1;
            if (copyItem.version == -1&&"new".equalsIgnoreCase(copyItem.remark)) {//如果该项是新增的抄录项返回false,代表不需要将该项添加到需要保存的数据中去
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CopyResult getResult() {
        return result;
    }

    public void setResult(CopyResult result) {
        this.result = result;
    }

    public CopyItem getItem() {
        return item;
    }

    public void setItem(CopyItem item) {
        this.item = item;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getVal_a() {
        return val_a;
    }

    public void setVal_a(String val_a) {
        this.val_a = val_a;
    }

    public String getVal_b() {
        return val_b;
    }

    public void setVal_b(String val_b) {
        this.val_b = val_b;
    }

    public String getVal_c() {
        return val_c;
    }

    public void setVal_c(String val_c) {
        this.val_c = val_c;
    }

    public String getVal_o() {
        return val_o;
    }

    public void setVal_o(String val_o) {
        this.val_o = val_o;
    }

    public String getVal_special() {
        return val_special;
    }

    public void setVal_special(String val_special) {
        this.val_special = val_special;
    }
}
