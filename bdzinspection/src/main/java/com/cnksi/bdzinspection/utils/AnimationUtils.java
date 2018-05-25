package com.cnksi.bdzinspection.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;

public class AnimationUtils {

    /**
     * 启动打印小票动画
     */
    public static void translateAnimRun(final View view, float... values) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translate", values).setDuration(2800);
        anim.start();
        anim.addUpdateListener(animation -> {
            float cVal = (Float) animation.getAnimatedValue();
            view.setTranslationY(cVal);
        });
    }

    /**
     * 动画集合
     */
    public static ValueAnimator animationSet(final View view, float... values) {
        ValueAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", values);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setDuration(1200);
        scaleY.start();
        return scaleY;
    }
}
