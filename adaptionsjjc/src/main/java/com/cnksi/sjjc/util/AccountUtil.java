package com.cnksi.sjjc.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cnksi.common.utils.DateCalcUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author kkk on 2018/1/19.
 */

public class AccountUtil {

    private static AccountUtil mUtilInstance;
    private HashMap<String, Integer> accountTimes = new HashMap<>();
    private int times;
    private static final int ERRORINPUTMAX = 3;

    public static AccountUtil getUtilInstance() {
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
        String oldTime = PreferencesUtils.get(account, "");
        if (TextUtils.isEmpty(oldTime)) {
            return false;
        } else {
            if (!DateCalcUtils.timeNormal(oldTime)) {
                return false;
            }
            boolean larger = DateUtils.compareDate(oldTime, DateCalcUtils.getNowPre(1, TimeUnit.HOURS,DateUtils.yyyy_MM_dd_HH_mm_ss), DateUtils.yyyy_MM_dd_HH_mm_ss);
            if (larger) {
                ToastUtils.showMessage("您的帐号已被锁定,请于一个小时之后重试");
            } else {
                PreferencesUtils.remove(account);
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
        ToastUtils.showMessage("密码错误,您还有" + (ERRORINPUTMAX - times) + "次输入密码的机会");
        if (times >= ERRORINPUTMAX) {
            PreferencesUtils.put(account, com.cnksi.core.utils.DateUtils.getCurrentLongTime());
            return true;
        } else {
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }
        }
        return false;
    }
}
