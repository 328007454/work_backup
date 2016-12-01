package com.cnksi.sjjc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.activity.GetSendLetterActivity;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ironGe on 2016/6/13.
 */
public class GetSendLetterFragment extends BaseCoreFragment {
    /**
     *
     */
    @ViewInject(R.id.iv_show_pic)
    public ImageView ivPic;

    @ViewInject(R.id.tv_pic_num)
    public TextView tvNum;

    /**
     * 当前照片地址名字
     */
    private String currentImageName;
    /**
     * 照片列表
     */
    private ArrayList<String> mImageList = new ArrayList<String>();

    private final static int ACTION_IMAGE = 0x300;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_send_letter, container, false);
        x.view().inject(view);
        initData();
        return view;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initData() {

    }

    @Event({R.id.iv_take_pic, R.id.iv_show_pic})
    private void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.iv_take_pic:
                FunctionUtils.takePicture(getActivity(), currentImageName = FunctionUtils.getCurrentImageName(), Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
                break;
            case R.id.iv_show_pic:
                if (mImageList != null && !mImageList.isEmpty()) {
                    ArrayList<String> mImageUrlList = new ArrayList<String>();
                    for (String string : mImageList) {
                        mImageUrlList.add(Config.RESULT_PICTURES_FOLDER + string);
                    }
                    ((BaseActivity) getActivity()).showImageDetails(getActivity(), mImageUrlList, true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == mCurrentActivity.RESULT_OK) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    mImageList.add(currentImageName);
                    String pictureContent = DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8);
                    ((GetSendLetterActivity) mCurrentActivity).drawCircle(Config.RESULT_PICTURES_FOLDER + currentImageName, pictureContent);
                    break;
                case LOAD_DATA:
                    showThumbPic();
                    break;
                default:
                    break;
            }
        }
    }

    private void showThumbPic() {
        if (mImageList != null && mImageList.size() > 1) {
            tvNum.setVisibility(View.VISIBLE);
            tvNum.setText(String.valueOf(mImageList.size()));
        } else {
            tvNum.setVisibility(View.GONE);
        }
        int newWidth = getResources().getDimensionPixelSize(R.dimen.new_defect_photo_height);
        if (mImageList != null && !mImageList.isEmpty()) {
            ivPic.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + mImageList.get(0), newWidth, newWidth));
        } else {
            ivPic.setImageBitmap(null);
        }
    }

}
