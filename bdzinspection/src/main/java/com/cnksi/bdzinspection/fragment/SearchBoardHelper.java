package com.cnksi.bdzinspection.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.databinding.XsFragmentKeyboardBinding;
import com.cnksi.bdzinspection.utils.ScreenUtils;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/31 15:55
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SearchBoardHelper {
    XsFragmentKeyboardBinding binding;
    Activity activity;
    Vibrator vibrator;
    private boolean isShowAllKey = false;
    private OnKeyChangeListener keyChangeListener;
    private View.OnClickListener keyClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            vibrator.vibrate(50);
            String searchKey = binding.tvKeyboardInput.getText().toString();
            int i = v.getId();
            if (i == R.id.tv_keyboard_item) {
                searchKey = searchKey + ((TextView) v).getText().toString();

                // 点击字母A显示展开关闭键盘
            } else if (i == R.id.tv_keyboard_words) {
                showAllKeyBord(!isShowAllKey);
                return;
                // 删除字符
            } else if (i == R.id.ibtn_keyboard_delete) {
                if (searchKey.length() == 0) return;
                searchKey = searchKey.substring(0, searchKey.length() - 1);

            }
            binding.tvKeyboardWords.setText(searchKey);
            if (keyChangeListener != null) {
                keyChangeListener.onKeyChange(searchKey);
            }
        }
    };

    public SearchBoardHelper(Activity activity) {
        this.activity = activity;
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        binding = XsFragmentKeyboardBinding.inflate(activity.getLayoutInflater());
        init();
    }

    private void init() {
        // 添加键盘布局

        // 计算键盘宽度
        int screenWidth = ScreenUtils.getScreenWidth(activity);
        int paddingLeft = getResources().getDimensionPixelSize(R.dimen.xs_keyboard_horizontal_space);
        int keyWidth = (screenWidth - 2 * paddingLeft - 9 * paddingLeft) / 10;
        // 添加键盘按键
        addKey(getResources().getStringArray(R.array.XS_WordsNumberArray), binding.llKeyboardNumContainer, paddingLeft, keyWidth);
        addKey(getResources().getStringArray(R.array.XS_WordsOneArray), binding.llKeyboardWordsOneContainer, paddingLeft, keyWidth);
        addKey(getResources().getStringArray(R.array.XS_WordsTwoArray), binding.llKeyboardWordsTwoContainer, paddingLeft, keyWidth);
        addKey(getResources().getStringArray(R.array.XS_WordsThreeArray), binding.llKeyboardWordsThreeContainer, paddingLeft, keyWidth);

        binding.ibtnKeyboardDelete.setOnClickListener(keyClickListener);
        binding.tvKeyboardWords.setOnClickListener(keyClickListener);
    }

    /**
     * 添加键盘输入按键
     */
    private void addKey(String[] keyWord, LinearLayout keyWordLayout, int paddingLeft, int keyWidth) {
        keyWordLayout.setGravity(Gravity.CENTER);
        int count = keyWord.length;
        for (int i = 0; i < count; i++) {
            ViewHolder keyHolder = new ViewHolder(activity, null, R.layout.xs_keyboard_button, false);
            keyHolder.setText(R.id.tv_keyboard_item, keyWord[i]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(keyWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (0 != i)
                params.setMargins(paddingLeft, 0, 0, 0);
            keyWordLayout.addView(keyHolder.getRootView(), params);
            keyHolder.getView(R.id.tv_keyboard_item).setOnClickListener(keyClickListener);
        }

    }

    private Resources getResources() {
        return activity.getResources();
    }

    public void bindLayout(LinearLayout layout, int index) {
        layout.addView(binding.getRoot(), index);
    }

    /**
     * 设置键盘显示全部还是不现实全部
     */
    private void showAllKeyBord(boolean showAllKeyBord) {
        binding.llKeyboardWordsOneContainer.setVisibility(showAllKeyBord ? View.VISIBLE : View.GONE);
        binding.llKeyboardWordsTwoContainer.setVisibility(showAllKeyBord ? View.VISIBLE : View.GONE);
        binding.llKeyboardWordsThreeContainer.setVisibility(showAllKeyBord ? View.VISIBLE : View.GONE);
        isShowAllKey = showAllKeyBord;
        keyChangeListener.keyBoardStatusChange(isShowAllKey);
    }


    public void setKeyChangeListener(OnKeyChangeListener keyChangeListener) {
        this.keyChangeListener = keyChangeListener;
    }

    public interface OnKeyChangeListener {
        void onKeyChange(String str);

        void keyBoardStatusChange(boolean isExpand);
    }


}
