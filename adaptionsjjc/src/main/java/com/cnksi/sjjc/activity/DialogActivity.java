//package com.cnksi.sjjc.activity;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.cnksi.core.application.CoreApplication;
//import com.cnksi.core.utils.BitmapUtil;
//import com.cnksi.core.utils.CToast;
//import com.cnksi.core.utils.FunctionUtils;
//import com.cnksi.core.utils.ScreenUtils;
//import com.cnksi.sjjc.Config;
//import com.cnksi.sjjc.CustomApplication;
//import com.cnksi.sjjc.R;
//import com.cnksi.sjjc.bean.AcceptCardItem;
//import com.cnksi.sjjc.bean.AcceptReportItem;
//import com.cnksi.sjjc.util.DialogUtils;
//import com.cnksi.sjjc.util.MediaRecorderUtils;
//import com.cnksi.sjjc.util.StringUtils;
//import com.cnksi.sjjc.util.VideoRecordUtils;
//
//import org.xutils.ex.DbException;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//
///**
// * Created by han on 2016/5/11.
// * 验收拍照界面
// */
//public class DialogActivity extends Activity {
//    //图片文件名
//    private String imgName = null;
//    //录音文件名
//    private String audioFileName = null;
//    //录像文件名
//    private String videoName;
//    // 录音的Dialog
//    private Dialog mRecordAudioDialg = null;
//    //显示照片
//    @ViewInject(R.id.iv_show_take_picture)
//    private ImageView imgShow;
//    //显示照片数量
//    @ViewInject(R.id.tv_pic_num)
//    private TextView tvPicNum;
//    //显示录音时间长度
//    @ViewInject(R.id.tv_audio)
//    private TextView tvAudio;
//    //显示视频缩略图
//    @ViewInject(R.id.img_video)
//    private ImageView imgVideo;
//    //显示视频时间
//    @ViewInject(R.id.tv_time)
//    private TextView tvVideoTime;
//    //显示视频标志图片
//    @ViewInject(R.id.iv_video_biaozhi)
//    private ImageView ivBiaoZhi;
//    //控制视频缩略图
//    @ViewInject(R.id.re_container)
//    private RelativeLayout reContainer;
//
//    private ExecutorService mFixedThreadPoolExecutor = CoreApplication.getExcutorService();
//
//    //取消选择的图片
//    public static final int CANCEL_RESULT_LOAD_IMAGE = 0x10;
//
//    private static final int TAKEPIC_REQUEST = 0x001;
//    private static final int VIDEO_REQUEST = TAKEPIC_REQUEST + 1;
//    private static final int LOAD_DATA = TAKEPIC_REQUEST + 1;
//    private static final int REFRESH_UI = LOAD_DATA+1;
//    private ArrayList<String> listPic = new ArrayList<String>();
//    //视频缩略图
//    private Bitmap videoBitmap;
//    private AcceptReportItem aReportItem;
//    private AcceptCardItem mAcceptCardItem;
//    private String reportId;
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case LOAD_DATA:
//                    reContainer.setVisibility(View.VISIBLE);
//                    imgVideo.setImageBitmap(videoBitmap);
//                    String length = VideoRecordUtils.getFormatTime(VideoRecordUtils.getVideoLength(Config.VIDEO_FOLDER + videoName));
//                    tvVideoTime.setText(length);
//                    imgVideo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(DialogActivity.this, PlayVideoActivity.class);
//                            intent.putExtra(Config.VIDEO_PATH, Config.VIDEO_FOLDER + videoName);
//                            DialogActivity.this.startActivity(intent);
//                        }
//                    });
//                    break;
//                case REFRESH_UI:
//                    refreshUi();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    /**
//     * 查询数据刷新UI
//     * */
//    private void refreshUi(){
//        if (aReportItem == null) {
//            aReportItem = new AcceptReportItem();
//            aReportItem.isNormal = -2;
//        } else {
//            String allPic = aReportItem.itemImages;
//            if (!TextUtils.isEmpty(allPic)&&""!=allPic) {
//                String[] pic = new String[]{};
//                pic = allPic.split(",");
//                tvPicNum.setText(pic.length + "");
//                for (int i = 0; i < pic.length; i++) {
//                    listPic.add(pic[i]);
//                    imgName = pic[i];
//                    showPicture();
//                }
//            }else{
//                imgShow.setVisibility(View.GONE);
//            }
//            videoName = aReportItem.itemVideo;
//            audioFileName = aReportItem.itemAudio;
//            if (!TextUtils.isEmpty(audioFileName)) {
//                initAudioLayout();
//            }
//            if (!TextUtils.isEmpty(videoName)) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        videoBitmap = VideoRecordUtils.getVideoThumbnail(Config.VIDEO_FOLDER + videoName);
//                        handler.sendEmptyMessage(LOAD_DATA);
//                    }
//                }).start();
//            }
//        }
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_dialog_activity);
//        x.view().inject(this);
//        initData();
//
//        imgShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showImageDetails(DialogActivity.this, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(listPic, Config.PICTURES_FOLDER), true);
//            }
//        });
//    }
//
//    private void initData() {
//        mAcceptCardItem = (AcceptCardItem) getIntent().getExtras().getSerializable(Config.ACCPETCARD_ITEM);
//        reportId = getIntent().getExtras().getString(Config.REPORT_ID);
//        mFixedThreadPoolExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    aReportItem = CustomApplication.getYanShouDbManager().selector(AcceptReportItem.class).where(AcceptReportItem.REPORT_ITEM_ID, "=", mAcceptCardItem.itemId).and(AcceptReportItem.REPORT_ID, "=", reportId).findFirst();
//                    handler.sendEmptyMessage(REFRESH_UI);
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
//
//    @Event({R.id.ibtn_take_picture, R.id.img_record, R.id.img_lx, R.id.btn_finish, R.id.tv_audio})
//    private void onViewClick(View view) {
//        switch (view.getId()) {
//            case R.id.ibtn_take_picture:
//                FunctionUtils.takePicture(this, imgName = FunctionUtils.getCurrentImageName(), Config.PICTURES_FOLDER, TAKEPIC_REQUEST);
//                break;
//            case R.id.img_record:
//                if (!MediaRecorderUtils.getInstance().isRecording()) {
//                    audioFileName = StringUtils.getCurrentAudioName(UUID.randomUUID().toString());
//                    MediaRecorderUtils.getInstance().startRecordAudio(Config.AUDIO_FOLDER + audioFileName);
//                    showRecordAudioDialog();
//                } else {
//                    CToast.showShort(this, "当前正在录音");
//                }
//                break;
//            case R.id.img_lx:
//                VideoRecordUtils.takeVideo(this, videoName = StringUtils.getCurrentVideoName(UUID.randomUUID().toString()), Config.VIDEO_FOLDER, VIDEO_REQUEST);
//                break;
//            case R.id.btn_finish:
//                Intent intent = new Intent();
//                intent.putExtra(Config.VIDEO_NAME, videoName);
//                intent.putExtra(Config.AUDIO_NAME, audioFileName);
//                intent.putStringArrayListExtra(Config.PIC_NAME, listPic);
//                if (bm != null || videoBitmap != null || !TextUtils.isEmpty(tvAudio.getText().toString())) {
//                    setResult(RESULT_OK, intent);
//                }
//                aReportItem.reportItemId = mAcceptCardItem.itemId;
//                aReportItem.reportId = reportId;
//                aReportItem.standerItemId = mAcceptCardItem.cardId;
//                aReportItem.itemVideo = videoName;
//                aReportItem.itemAudio = audioFileName;
//                if (listPic != null && listPic.size() > 0) {
//                    aReportItem.itemImages = com.cnksi.core.utils.StringUtils.ArrayListToString(listPic);
//                }else{
//                    aReportItem.itemImages="";
//                }
//                try {
//                    CustomApplication.getYanShouDbManager().saveOrUpdate(aReportItem);
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
//                this.finish();
//                break;
//            case R.id.tv_audio:
//                if (MediaRecorderUtils.getInstance().isPlaying()) {
//                    // 正在播放，并且点击的是相同的文件 则停止播放
//                    MediaRecorderUtils.getInstance().stopPlayAudio();
//                    CToast.showShort(this, "停止播放");
//                } else if (MediaRecorderUtils.getInstance().isPlaying()) {
//                    // 正在播放，并且点击的是不相同的文件 则停止播放 继续播放不同的文件
//                    MediaRecorderUtils.getInstance().stopPlayAudio();
//                    startPlayAudio(audioFileName);
//                } else {
//                    // 没有播放，开始播放
//                    startPlayAudio(audioFileName);
//                }
//            default:
//                break;
//        }
//    }
//
//
//    /**
//     * 播放音频文件
//     */
//    public void startPlayAudio(String audioFileName) {
//        MediaRecorderUtils.getInstance().startPlayAudio(Config.AUDIO_FOLDER + audioFileName, new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                CToast.showShort(DialogActivity.this, "播放完毕...");
//                MediaRecorderUtils.getInstance().setPlaying(false);
//            }
//        });
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case TAKEPIC_REQUEST:
//                    listPic.add(imgName);
//                    showPicture();
//                    break;
//                case VIDEO_REQUEST:
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            videoBitmap = VideoRecordUtils.getVideoThumbnail(Config.VIDEO_FOLDER + videoName);
//                            handler.sendEmptyMessage(LOAD_DATA);
//                        }
//                    }).start();
//                    break;
//                case CANCEL_RESULT_LOAD_IMAGE:
//                    ArrayList<String> cancleImagList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
//                    for (String imageUrl : cancleImagList) {
//                        listPic.remove(imageUrl.replace(Config.PICTURES_FOLDER, ""));
//                        if(listPic.size()!=0){
//                            imgName=listPic.get(0);
//                        }
//
//                    }
//                    showPicture();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    private Bitmap bm = null;
//
//    private void showPicture() {
//        if(listPic==null||listPic.size()==0){
//            tvPicNum.setVisibility(View.GONE);
//            imgShow.setVisibility(View.GONE);
//        }else{
//            File file = new File(Config.PICTURES_FOLDER,imgName);
//            if (file.exists()) {
//                BitmapUtil.compressImage(file.getAbsolutePath(), 3);
//            }
//            bm = BitmapUtil.getOptimizedBitmap(Config.PICTURES_FOLDER + imgName);
//            imgShow.setVisibility(View.VISIBLE);
//            imgShow.setImageBitmap(bm);
//            tvPicNum.setVisibility(View.VISIBLE);
//            tvPicNum.setText(listPic.size() + "");
//        }
//    }
//
//    /**
//     * 显示录音对话框
//     */
//    ViewHolder audioHolder;
//
//    private void showRecordAudioDialog() {
//        if (mRecordAudioDialg == null) {
//            int dialogWidth = ScreenUtils.getScreenWidth(this) / 2;
//            mRecordAudioDialg = DialogUtils.createDialog(this, null, R.layout.record_audio_dialog, audioHolder == null ? audioHolder = new ViewHolder() : audioHolder, dialogWidth, dialogWidth, false);
//            mRecordAudioDialg.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    MediaRecorderUtils.getInstance().stopRecordAudio();
//                    initAudioLayout();
//                }
//            });
//        }
//        mRecordAudioDialg.show();
//
//    }
//
//    /**
//     * 录音完后显示录音时间
//     **/
//    private void initAudioLayout() {
//        tvAudio.setVisibility(View.VISIBLE);
//        tvAudio.setText(String.valueOf(MediaRecorderUtils.getInstance().getDurationSuc(this, Config.AUDIO_FOLDER + audioFileName) + "”"));
//
//    }
//
//    class ViewHolder {
//        @Event({R.id.ll_dialog_container})
//        private void OnViewClick(View view) {
//            mRecordAudioDialg.dismiss();
//        }
//    }
//
//    /**
//     * 显示大图片
//     *
//     * @param position
//     */
//    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList, boolean isShowDelete) {
//        Intent intent = new Intent(this, ImageDetailsActivity.class);
//        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
//        if (mImageUrlList != null) {
//            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
//        }
//        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
//        context.startActivityForResult(intent, CANCEL_RESULT_LOAD_IMAGE);
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        this.overridePendingTransition(R.anim.activity_close,0);
//    }
//}
