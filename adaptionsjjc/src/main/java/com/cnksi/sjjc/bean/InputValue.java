package com.cnksi.sjjc.bean;

import android.text.TextUtils;

/**
 * Created by ksi-android on 2016/5/10.
 */
public class InputValue {

  public   String name;
  public    String type;
    public  String unit;
    public  String value;

    public InputValue()
    {}

    public boolean IsDate()
    {
        if (TextUtils.isEmpty(type)) {
            return false;
        }
        else if ("date".equalsIgnoreCase(type))
        {
            return true;
        }else{
            return  false;
        }
    }

}
