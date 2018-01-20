package com.cnksi.sjjc.util;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.PreferencesUtils;

import java.util.HashMap;

/**
 * @author kkk on 2018/1/19.
 */

public class AccountUtil {

    private static AccountUtil mUtilInstance;
    private HashMap<String, Integer> accountTimes = new HashMap<>();
    private int times;
    private static final int ERRORINPUTMAX = 3;

    public static AccountUtil getmUtilInstance() {
        if (null == mUtilInstance) {
            mUtilInstance = new AccountUtil();
        }
        return mUtilInstance;
    }

    /**
     * 判断账户是否需要解锁
     *
     * @param context
     * @param account
     * @return
     */
    public boolean JudgeAccountBlocked(Context context, String account) {
        String oldTime = PreferencesUtils.getString(context, account, "");
        if (TextUtils.isEmpty(oldTime)) {
            return false;
        } else {
            if (!com.cnksi.core.utils.DateUtils.timeNormal(oldTime)) {
                return false;
            }
            boolean larger = com.cnksi.core.utils.DateUtils.compareDate(oldTime, com.cnksi.core.utils.DateUtils.getPreHour(1, CoreConfig.dateFormat2), CoreConfig.dateFormat2);

            if (larger) {
                CToast.showShort(context, "您的帐号已被锁定,请稍后重试");
            } else {
                PreferencesUtils.remove(context, account);
            }
            return larger;
        }
    }

    /**
     * 是否锁定账户
     *
     * @param account
     * @return
     */

    public boolean preBlockAccount(Context context, String account, View view) {
        if (!accountTimes.keySet().contains(account)) {
            accountTimes.clear();
            times = 1;
        } else if (accountTimes.keySet().contains(account)) {
            times = accountTimes.get(account) + 1;
        }
        accountTimes.put(account, times);
        CToast.showShort(context, "密码错误,您还有" + (ERRORINPUTMAX - times) + "输入密码的机会");
        if (times >= ERRORINPUTMAX) {
            PreferencesUtils.put(context, account, com.cnksi.core.utils.DateUtils.getCurrentLongTime());
            return true;
        } else {
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }
        }

        return false;
    }
}
