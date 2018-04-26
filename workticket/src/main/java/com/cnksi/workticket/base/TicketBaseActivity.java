package com.cnksi.workticket.base;

import com.cnksi.core.activity.BaseCoreActivity;

/**
 * Created by Mr.K on 2018/4/23.
 */

public abstract class  TicketBaseActivity extends BaseCoreActivity {

    public abstract int getLayoutResId();


    public  abstract void initUI();


    public  abstract void initData() ;
}
