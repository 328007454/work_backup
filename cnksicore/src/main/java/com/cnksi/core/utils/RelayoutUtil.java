/*
 *  Copyright (C) 2015 Chengdu TimelyHelp Network Technology Co., Ltd.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cnksi.core.utils;

import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;

public class RelayoutUtil {

    public final static String TAG = "ReLayoutUtil";

    /**
     * 递归缩放view
     *
     * @param view 缩放控件
     */
    public static void reLayoutViewHierarchy(View view) {
        if (view != null) {
            scaleView(view);
            if (view instanceof ViewGroup) {
                try {
                    Field field = ViewGroup.class.getDeclaredField("mChildren");
                    field.setAccessible(true);
                    View[] childRen = (View[]) field.get(view);
                    if (null != childRen) {
                        for (View child : childRen)
                            reLayoutViewHierarchy(child);
                    }
                } catch (NoSuchFieldException e) {
                    Log.e(TAG, "缩放控件没有找到mChildren属性", e);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "缩放控件,Field不允许访问", e);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "缩放控件View不是一个有效的实例", e);
                }
            }
        }
    }

    /**
     * 缩放控件不考虑递归
     *
     * @param view 缩放控件
     */
    public static void scaleView(View view) {
        if (null != view) {
            // 如果是TextView的实例,缩放字体大小
            if (view instanceof TextView)
                scaleTextSize((TextView) view);
            // 重设padding
            if (!(view.getBackground() instanceof NinePatchDrawable)) {
                int leftPadding = NumberUtil.convertFloatToInt(view.getPaddingLeft() * DisplayUtil.getInstance().getScale());
                int rightPadding = NumberUtil.convertFloatToInt(view.getPaddingRight() * DisplayUtil.getInstance().getScale());
                int topPadding = NumberUtil.convertFloatToInt(view.getPaddingTop() * DisplayUtil.getInstance().getScale());
                int bottomPadding = NumberUtil.convertFloatToInt(view.getPaddingBottom() * DisplayUtil.getInstance().getScale());
                view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
            }
            // 重设layoutparams
            relayoutLayoutParams(view.getLayoutParams());

        }
    }

    /**
     * 缩放设置layoutParams
     *
     * @param layoutParams
     */
    public static void relayoutLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (null != layoutParams) {
            // 重新设置宽度高度
            if (layoutParams.width > 0)
                layoutParams.width = NumberUtil.convertFloatToInt(layoutParams.width * DisplayUtil.getInstance().getScale());
            if (layoutParams.height > 0)
                layoutParams.height = NumberUtil.convertFloatToInt(layoutParams.height * DisplayUtil.getInstance().getScale());
            // 重设间距
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                if (marginLayoutParams.leftMargin > 0)
                    marginLayoutParams.leftMargin = NumberUtil.convertFloatToInt(marginLayoutParams.leftMargin * DisplayUtil.getInstance().getScale());
                if (marginLayoutParams.rightMargin > 0)
                    marginLayoutParams.rightMargin = NumberUtil.convertFloatToInt(marginLayoutParams.rightMargin * DisplayUtil.getInstance().getScale());
                if (marginLayoutParams.topMargin > 0)
                    marginLayoutParams.topMargin = NumberUtil.convertFloatToInt(marginLayoutParams.topMargin * DisplayUtil.getInstance().getScale());
                if (marginLayoutParams.bottomMargin > 0)
                    marginLayoutParams.bottomMargin = NumberUtil.convertFloatToInt(marginLayoutParams.bottomMargin * DisplayUtil.getInstance().getScale());
            }
        }
    }

    /**
     * 缩放字体大小
     *
     * @param view 缩放控件
     */
    public static void scaleTextSize(TextView view) {
        float textSize = view.getTextSize();
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * DisplayUtil.getInstance().getTextScale());
    }


}
