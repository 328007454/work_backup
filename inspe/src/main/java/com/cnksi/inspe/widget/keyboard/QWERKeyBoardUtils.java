package com.cnksi.inspe.widget.keyboard;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.inspe.R;


/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/5 9:01
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
final public class QWERKeyBoardUtils {
    private Activity mActivity;
    private Vibrator mVibrator;
    private ViewHolder keyBordHolder;
    private TextView txtKeyWord;
    private boolean isCharMode;
    private keyWordChangeListener listener;
    private View.OnClickListener keyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mVibrator.vibrate(50);
            String oldKey = txtKeyWord.getText().toString();
            String newKey = "";
            int i = v.getId();
            if (i == R.id.tv_keyboard_item) {
                newKey = oldKey + ((TextView) v).getText().toString();
                // 点击字母A显示展开关闭键盘
            } else if (i == R.id.tv_keyboard_words) {
                showAllKeyBord(!isCharMode);
                return;
                // 删除字符
            } else if (i == R.id.ibtn_keyboard_delete) {
                if (TextUtils.isEmpty(oldKey)) return;
                else newKey = oldKey.substring(0, oldKey.length() - 1);

            }
            txtKeyWord.setText(newKey);
            if (listener != null)
                listener.onChange(v, oldKey, newKey);
        }
    };

    public QWERKeyBoardUtils(Activity mActivity) {
        this.mActivity = mActivity;
        mVibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 初始化键盘布局
     *
     * @param rootLayout
     * @param listener
     */
    public void init(LinearLayout rootLayout, final keyWordChangeListener listener) {
        this.listener = listener;
        // 添加键盘布局
        keyBordHolder = new ViewHolder(mActivity, null, R.layout.inspe_keyboard, false);
        txtKeyWord = keyBordHolder.getView(R.id.tv_keyboard_input);
        rootLayout.addView(keyBordHolder.getRootView(), 0);
        // 计算键盘宽度
        int screenWidth = ScreenUtils.getScreenWidth(mActivity);
        int paddingLeft = mActivity.getResources().getDimensionPixelSize(R.dimen.inspe_9px);
        int keyWidth = (screenWidth - 2 * paddingLeft - 9 * paddingLeft) / 10;
        // 添加键盘按键
        addKey(mActivity.getResources().getStringArray(R.array.Inspe_WordsNumberArray), keyBordHolder.getView(R.id.ll_keyboard_num_container), paddingLeft, keyWidth);
        addKey(mActivity.getResources().getStringArray(R.array.Inspe_WordsOneArray), keyBordHolder.getView(R.id.ll_keyboard_words_one_container), paddingLeft, keyWidth);
        addKey(mActivity.getResources().getStringArray(R.array.Inspe_WordsTwoArray), keyBordHolder.getView(R.id.ll_keyboard_words_two_container), paddingLeft, keyWidth);
        addKey(mActivity.getResources().getStringArray(R.array.Inspe_WordsThreeArray), keyBordHolder.getView(R.id.ll_keyboard_words_three_container), paddingLeft, keyWidth);
        keyBordHolder.getView(R.id.ibtn_keyboard_delete).setOnClickListener(keyClickListener);
        keyBordHolder.getView(R.id.tv_keyboard_words).setOnClickListener(keyClickListener);
    }

    public void setListener(keyWordChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 添加键盘输入按键
     */
    private void addKey(String[] keyWord, LinearLayout keyWordLayout, int paddingLeft, int keyWidth) {
        keyWordLayout.setGravity(Gravity.CENTER);
        int count = keyWord.length;
        for (int i = 0; i < count; i++) {
            ViewHolder keyHolder = new ViewHolder(mActivity, null, R.layout.inspe_keyboard_button, false);
            keyHolder.setText(R.id.tv_keyboard_item, keyWord[i]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(keyWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (0 != i)
                params.setMargins(paddingLeft, 0, 0, 0);
            keyWordLayout.addView(keyHolder.getRootView(), params);
            keyHolder.getView(R.id.tv_keyboard_item).setOnClickListener(keyClickListener);
        }
        showAllKeyBord(false);
    }

    /**
     * 设置键盘显示全部还是不现实全部
     */
    private void showAllKeyBord(boolean clickCharacter) {
        keyBordHolder.getView(R.id.ll_keyboard_words_one_container).setVisibility(clickCharacter ? View.VISIBLE : View.GONE);
        keyBordHolder.getView(R.id.ll_keyboard_words_two_container).setVisibility(clickCharacter ? View.VISIBLE : View.GONE);
        keyBordHolder.getView(R.id.ll_keyboard_words_three_container).setVisibility(clickCharacter ? View.VISIBLE : View.GONE);
        isCharMode = clickCharacter;
    }

    public void setKeyWord(String s) {
        txtKeyWord.setText(s);
    }

    public interface keyWordChangeListener {
        /**
         * 当关键字发生改变时会调用此接口
         *
         * @param oldKey 老的Key
         * @param newKey 新的key
         */
        void onChange(View v, String oldKey, String newKey);
    }

    public boolean isCharMode() {
        return isCharMode;
    }
}
