package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DiscoverHoleAdapter;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by han on 2016/6/13.
 * 发现孔洞
 */
public class DiscoverHoleActivity extends BaseActivity {
    @ViewInject(R.id.ll_container_pic)
    private LinearLayout mLLRootContainer;

    //位置集合
    @ViewInject(R.id.tv_discoverhole_position)
    private TextView tvHolePosition;
    //填写详细位置
    @ViewInject(R.id.et_input_inforposition)
    private EditText etPosition;
    //发现孔洞的照片
    @ViewInject(R.id.img_discoverhole_pic)
    private ImageView imgHole;
    //孔洞照片数量
    @ViewInject(R.id.tv_hole_num)
    private TextView tvHoleNum;
    //问题描述
    @ViewInject(R.id.et_input_problem)
    private TextView etInputProblem;


    //所有孔洞位置集合
    private List<String> areaList;
    private ListView positionLv;
    private Dialog positionDialog;

    private static final int TAKEPIC_REQUEST = LOAD_DATA + 1;
    private static final int VIDEO_REQUEST = TAKEPIC_REQUEST + 1;
    private static final int REFRESH_UI = VIDEO_REQUEST + 1;
    private String imgName;
    private ArrayList<String> picList = new ArrayList<>();

    private Bitmap bmPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_discover_hole);
        getIntentValue();
        initUI();
        initData();
    }

    private void initUI() {
        tvTitle.setText("发现孔洞");
        tvHoleNum.setVisibility(View.GONE);
    }

    private void initData() {
        areaList = Arrays.asList(getResources().getStringArray(R.array.AnimalPositionArray));
        mHandler.sendEmptyMessage(LOAD_DATA);
    }


    private void initPostionDialog() {
        int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
        int dialogHeight = areaList.size() > 8 ? DisplayUtil.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        ViewHolder holder = new ViewHolder(this, mLLRootContainer, R.layout.content_list_dialog, false);
        positionLv = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, "请选择孔洞位置");
        DiscoverHoleAdapter adapter = new DiscoverHoleAdapter(this, areaList, R.layout.dialog_content_child_item);
        //设置adapter的listView点击事件
        adapter.setItemClickListener(new ItemClickListener<String>() {

            @Override
            public void itemClick(View v, String s, int position) {
                tvHolePosition.setText(s);
                tvHolePosition.setTextColor(_this.getResources().getColor(R.color.green_color));
                positionDialog.dismiss();
            }

            @Override
            public void itemLongClick(View v, String s, int position) {

            }
        });
        positionLv.setAdapter(adapter);
        positionDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                initPostionDialog();
                break;
            default:
                break;
        }

    }

    @Event({R.id.position_container, R.id.iv_take_pic, R.id.img_discoverhole_pic, R.id.btn_save})
    private void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.position_container:
                if (positionDialog != null) {
                    positionDialog.show();
                }
                break;
            case R.id.iv_take_pic:
                FunctionUtils.takePicture(this, imgName = FunctionUtil.getCurrentImageName(_this), Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);
                break;
            case R.id.img_discoverhole_pic:
                showImageDetails(_this, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(picList, Config.RESULT_PICTURES_FOLDER), true, false);
                break;
            case R.id.btn_save:
                String picAll = StringUtils.ArrayListToString(picList);
                if (TextUtils.isEmpty(tvHolePosition.getText().toString())) {
                    CToast.showShort(_this, "请选择发现位置");
                    return;
                }
                if (TextUtils.isEmpty(etPosition.getText().toString())) {
                    CToast.showShort(_this, "请输入详细位置");
                    return;
                }
//                if (TextUtils.isEmpty(picAll)) {
//                    CToast.showShort(_this, "请拍摄孔洞照片");
//                    return;
//                }
                if (TextUtils.isEmpty(etInputProblem.getText().toString())) {
                    CToast.showShort(_this, "请输入问题描述");
                    return;
                }
                HoleRecord holeRecord = new HoleRecord(currentReportId, currentBdzId, currentBdzName, tvHolePosition.getText().toString(),
                        etPosition.getText().toString(), picAll,etInputProblem.getText().toString());
                try {
                    db.save(holeRecord);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK);
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bm = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKEPIC_REQUEST) {
                File file = new File(Config.RESULT_PICTURES_FOLDER, imgName);
                if (file.exists()) {
                    BitmapUtil.compressImage(file.getAbsolutePath(), 3);
                    String pictureContent = DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8) + "\n" + tvHolePosition.getText()
                            + etPosition.getText().toString() + "\n" + PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_USER, "");
                    drawCircle(Config.RESULT_PICTURES_FOLDER + imgName, pictureContent);
                    picList.add(imgName);
                }

            } else if (requestCode == CANCEL_RESULT_LOAD_IMAGE) {
                ArrayList<String> cancleImagList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                for (String imageUrl : cancleImagList) {
                    picList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                }
                showPicture();
            } else if (requestCode == LOAD_DATA) {
                bm = BitmapUtil.getOptimizedBitmap(Config.RESULT_PICTURES_FOLDER + imgName);
                imgHole.setImageBitmap(bm);
                showPicture();
            }
        }
    }

    public void drawCircle(String pictureName, String pictureContent) {
        Intent intent = new Intent(_this, DrawCircleImageActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_NAME, pictureName);
        intent.putExtra(Config.PICTURE_CONTENT, pictureContent);
        startActivityForResult(intent, LOAD_DATA);
    }

    private void showPicture() {
        if (picList.size() != 0) {
            imgName = picList.get(0);
            bmPicture = BitmapUtil.getOptimizedBitmap(Config.RESULT_PICTURES_FOLDER + imgName);
            if (picList.size() == 1) {
                tvHoleNum.setVisibility(View.GONE);
            } else if (picList.size() != 0 && picList.size() > 1) {
                tvHoleNum.setVisibility(View.VISIBLE);
                tvHoleNum.setText(picList.size() + "");
            }
            imgHole.setVisibility(View.VISIBLE);
            imgHole.setImageBitmap(bmPicture);
        } else if (picList.size() == 0) {
            tvHoleNum.setVisibility(View.GONE);
            imgHole.setVisibility(View.GONE);
        }
    }

}
