package com.cnksi.core.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.cnksi.core.R;


public class ScreenUtil {

    // Screen Params
    public static final int BASE_SCREEN_WIDTH = 1080;
    public static final int BASE_SCREEN_HEIGHT = 1920;

    public static float sScale = 1;

    /**
     * Set the screen scale value
     *
     * @param context current activity
     */
    public static void setScale(Activity context) {
        int width;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        sScale = (float) width / BASE_SCREEN_WIDTH;
    }

    public static ViewGroup.LayoutParams scaleParams(ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new RuntimeException("params not's null");
        }
        if (params.width > 0) {
            params.width = getScalePxValue(params.width);
        }

        if (params.height > 0) {
            params.height = getScalePxValue(params.height);
        }

        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) params;
            lp.topMargin = getScalePxValue(lp.topMargin);
            lp.leftMargin = getScalePxValue(lp.leftMargin);
            lp.bottomMargin = getScalePxValue(lp.bottomMargin);
            lp.rightMargin = getScalePxValue(lp.rightMargin);
        }

        return params;
    }

    /**
     * Scale the view
     *
     * @param view      The scale view
     */
    public static void scaleProcess(View view) {
        if (view == null) return;

        Object isScale = view.getTag(R.id.is_scale_tag);
        if (isScale instanceof Boolean) {
            if ((Boolean) isScale == true) {
                return;
            }
        }

        // set padding
        int left = getScalePxValue(view.getPaddingLeft());
        int top = getScalePxValue(view.getPaddingTop());
        int right = getScalePxValue(view.getPaddingRight());
        int bottom = getScalePxValue(view.getPaddingBottom());

        view.setPadding(left, top, right, bottom);

        // set drawable
        if (view instanceof TextView) {
            Drawable[] drawables = ((TextView) view).getCompoundDrawables();
            setCompoundDrawablesWithIntrinsicBounds((TextView) view, drawables[0], drawables[1], drawables[2], drawables[3]);

            ((TextView) view).setCompoundDrawablePadding(getScalePxValue(((TextView) view).getCompoundDrawablePadding()));
        }

        view.setLayoutParams(scaleParams(view.getLayoutParams()));
    }

    private static void setCompoundDrawablesWithIntrinsicBounds(TextView view, Drawable left, Drawable top, Drawable right, Drawable bottom) {

        if (left != null) {
            left.setBounds(0, 0, ScreenUtil.getScalePxValue(left.getIntrinsicWidth()), ScreenUtil.getScalePxValue(left.getIntrinsicHeight()));
        }
        if (right != null) {
            right.setBounds(0, 0, ScreenUtil.getScalePxValue(right.getIntrinsicWidth()), ScreenUtil.getScalePxValue(right.getIntrinsicHeight()));
        }
        if (top != null) {
            top.setBounds(0, 0, ScreenUtil.getScalePxValue(top.getIntrinsicWidth()), ScreenUtil.getScalePxValue(top.getIntrinsicHeight()));
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, ScreenUtil.getScalePxValue(bottom.getIntrinsicWidth()), ScreenUtil.getScalePxValue(bottom.getIntrinsicHeight()));
        }

        view.setCompoundDrawables(left, top, right, bottom);
    }


    /**
     * Scale the textview's font size
     *
     * @param view
     */
    public static void scaleProcessTextSize(TextView view) {
        if (view == null) return;
        float size = view.getTextSize();
        size *= sScale;
        // Size's unit use pixel,so param unit use TypedValue.COMPLEX_UNIT_PX.
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * Scale the textview
     *
     * @param view      The scale view
     * @return
     */
    public static void scaleProcessTextView(TextView view) {
        if (view == null) return;
        scaleProcess(view);
        scaleProcessTextSize(view);
    }

    public static int getScalePxValue(int value) {
        if (value <= 4) {
            return value;
        }
        return (int) Math.ceil(sScale * value);
    }

    public static void initScale(View v) {
        if (v != null) {
            if (v instanceof ViewGroup) {
                sacleViewGroup((ViewGroup) v);
            } else {
                scaleView(v);
            }
        }
    }

    private static void sacleViewGroup(ViewGroup viewGroup) {

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                sacleViewGroup((ViewGroup) view);
            }
            scaleView(view);
        }

    }

    private static void scaleView(View view) {

        if (view instanceof TextView) {
            ScreenUtil.scaleProcessTextView((TextView) view);
            return;
        }

        scaleProcess(view);
    }


    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }
}
