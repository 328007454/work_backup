package com.cnksi.common.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.common.R;
import com.cnksi.common.databinding.FragmentKeyboardBinding;
import com.cnksi.common.databinding.KeyboardButtonBinding;
import com.cnksi.core.utils.ScreenUtils;


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
    private FragmentKeyboardBinding binding;
    private TextView txtKeyWord;
    private boolean isCharMode;
    private keyWordChangeListener listener;
    private View.OnClickListener keyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mVibrator.vibrate(50);
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
                if (TextUtils.isEmpty(oldKey)) {
                    return;
                } else {
                    newKey = oldKey.substring(0, oldKey.length() - 1);
                }

            }
            txtKeyWord.setText(newKey);
            if (listener != null) {
                listener.onChange(v, oldKey, newKey);
            }
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
        binding=FragmentKeyboardBinding.inflate(mActivity.getLayoutInflater());

        txtKeyWord = binding.tvKeyboardInput;
        rootLayout.addView(binding.getRoot(), 0);
        // 计算键盘宽度
        int screenWidth = ScreenUtils.getScreenWidth(mActivity);
        int paddingLeft = mActivity.getResources().getDimensionPixelSize(R.dimen.xs_keyboard_horizontal_space);
        int keyWidth = (screenWidth - 2 * paddingLeft - 9 * paddingLeft) / 10;
        // 添加键盘按键
        addKey(mActivity.getResources().getStringArray(R.array.WordsNumberArray), binding.llKeyboardNumContainer, paddingLeft, keyWidth);
        addKey(mActivity.getResources().getStringArray(R.array.WordsOneArray), binding.llKeyboardWordsOneContainer, paddingLeft, keyWidth);
        addKey(mActivity.getResources().getStringArray(R.array.WordsTwoArray), binding.llKeyboardWordsTwoContainer, paddingLeft, keyWidth);
        addKey(mActivity.getResources().getStringArray(R.array.WordsThreeArray), binding.llKeyboardWordsThreeContainer, paddingLeft, keyWidth);
        binding.ibtnKeyboardDelete.setOnClickListener(keyClickListener);
        binding.tvKeyboardWords.setOnClickListener(keyClickListener);
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
            KeyboardButtonBinding keyBinding=KeyboardButtonBinding.inflate(mActivity.getLayoutInflater());
            keyBinding.tvKeyboardItem.setText( keyWord[i]);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(keyWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (0 != i) {
                params.setMargins(paddingLeft, 0, 0, 0);
            }
            keyWordLayout.addView(keyBinding.getRoot(), params);
            keyBinding.tvKeyboardItem.setOnClickListener(keyClickListener);
        }
        showAllKeyBord(false);
    }

    /**
     * 设置键盘显示全部还是不现实全部
     */
    private void showAllKeyBord(boolean clickCharacter) {
        binding.llKeyboardWordsOneContainer.setVisibility(clickCharacter ? View.VISIBLE : View.GONE);
        binding.llKeyboardWordsTwoContainer.setVisibility(clickCharacter ? View.VISIBLE : View.GONE);
        binding.llKeyboardWordsThreeContainer.setVisibility(clickCharacter ? View.VISIBLE : View.GONE);
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
