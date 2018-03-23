//package com.cnksi.sjjc.fragment;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.media.MediaPlayer;
//import android.media.ThumbnailUtils;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.support.v4.app.Fragment;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.cnksi.core.fragment.BaseCoreFragment;
//import com.cnksi.core.utils.BitmapUtil;
//import com.cnksi.core.utils.CToast;
//import com.cnksi.core.utils.CoreConfig;
//import com.cnksi.core.utils.DateUtils;
//import com.cnksi.core.utils.FunctionUtils;
//import com.cnksi.core.utils.ScreenUtils;
//import com.cnksi.sjjc.Config;
//import com.cnksi.sjjc.R;
//import com.cnksi.sjjc.activity.ImageDetailsActivity;
//import com.cnksi.sjjc.activity.VideoPlayer;
//import com.cnksi.sjjc.bean.EvaluationItem;
//import com.cnksi.sjjc.bean.EvaluationItemReport;
//import com.cnksi.sjjc.util.DialogUtils;
//import com.cnksi.sjjc.util.MediaRecorderUtils;
//
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Created by ksi-android on 2016/5/11.
// */
//public class ToolFragment extends BaseCoreFragment {
//    public static final int LOAD_DATA=0x01;
//
//    @ViewInject(R.id.ibtn_take_picture)
//    private ImageView tackPicture;
//    @ViewInject(R.id.img_pc)
//    private ImageView takeAudio;
//    @ViewInject(R.id.img_lx)
//    private ImageView takeVedio;
//    private String imgName="";
//    private List<String> listPic=new ArrayList<String>();
//    private String audioFileName="";
//    private String vedioFileName="";
//    @ViewInject(R.id.iv_show_take_picture)
//    private ImageView imgShow;
//    @ViewInject(R.id.tv_pic_num)
//    private TextView tvPicNum;
//    //显示录音时间长度
//    @ViewInject(R.id.tv_audio)
//    private TextView tvAudio;
//    //显示视频缩略图
//    @ViewInject(R.id.img)
//    private ImageView imgVideo;
//    @ViewInject(R.id.rl_veido)
//    RelativeLayout rlVedio;
//    @ViewInject(R.id.tv_duration)
//    TextView tvDuration;
//    private Bitmap bitmap;
//    private String duration;
//    private  EvaluationItem currentEvaluation;
//    private EvaluationItemReport itemReport;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_gjx, container, false);
//        x.view().inject(this,view);
//
//
//        return view;
//    }
//
//    @Override
//    protected void lazyLoad() {
//
//    }
//
//    @Override
//    protected void initUI() {
//        currentEvaluation = (EvaluationItem) getArguments().getSerializable(Config.DATA);
//        itemReport=(EvaluationItemReport)getArguments().getSerializable(Config.DATA1);
//        if (itemReport!=null)
//        {
//            vedioFileName= itemReport.itemVideo;
//            audioFileName=itemReport.itemAudio;
//            if (!TextUtils.isEmpty(itemReport.itemImages))
//            {
//                String []strs=itemReport.itemImages.split(CoreConfig.COMMA_SEPARATOR);
//                listPic.clear();
//                for (String str:strs)
//                {
//                    listPic.add(str);
//                }
//            }
//            showPicture();
//            showVedio();
//            showAudio();
//        }
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//    @Event(value = {R.id.ibtn_take_picture,R.id.img_pc,R.id.img_lx,R.id.tv_audio,R.id.iv_show_take_picture,R.id.rl_veido})
//    private void OnClick(View v)
//    {
//        switch (v.getId())
//        {
//            case R.id.ibtn_take_picture:
//                FunctionUtils.takePicture(getActivity(), imgName = FunctionUtils.getCurrentImageName(), Config.RESULT_PICTURES_FOLDER, 0x124);
//                break;
//            case R.id.img_pc:
//                if (!MediaRecorderUtils.getInstance().isRecording()) {
//                    audioFileName =FunctionUtils.getPrimarykey()+ CoreConfig.AAC_POSTFIX;
//                    MediaRecorderUtils.getInstance().startRecordAudio(Config.AUDIO_FOLDER + audioFileName);
//                    showRecordAudioDialog();
//                } else {
//                    CToast.showShort(mCurrentActivity, "当前正在录音");
//                }
//                break;
//            case R.id.img_lx:
//                takeVideo(Config.VIDEO_FOLDER + (vedioFileName = DateUtils.getCurrentTime(CoreConfig.dateFormat6) + ".mp4"));
//                break;
//            case R.id.tv_audio:
//                if (MediaRecorderUtils.getInstance().isPlaying()) {
//                    // 正在播放，并且点击的是相同的文件 则停止播放
//                    MediaRecorderUtils.getInstance().stopPlayAudio();
//                    CToast.showShort(mCurrentActivity, "停止播放");
//                }  else {
//                    // 没有播放，开始播放
//                    startPlayAudio(audioFileName);
//                }
//                break;
//            case R.id.iv_show_take_picture:
//                if (listPic != null && !listPic.isEmpty()) {
//                    ArrayList<String> mImageUrlList = new ArrayList<String>();
//                    for (String string : listPic) {
//                        mImageUrlList.add(Config.RESULT_PICTURES_FOLDER + string);
//                    }
//                    showImageDetails(mCurrentFragment,0, mImageUrlList, true,false);
//                }
//                break;
//            case R.id.rl_veido:
//                Intent intent = new Intent(mCurrentActivity, VideoPlayer.class);
//                intent.putExtra("video",Config.VIDEO_FOLDER+ vedioFileName);
//                mCurrentActivity.startActivity(intent);
//                break;
//            default:
//                break;
//        }
//    }
//    protected void takeVideo(String videopath) {
//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// action is capture
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(videopath)));
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        this.getActivity().startActivityForResult(intent, 0x125);
//    }
//
//    /**
//     * 播放音频文件
//     */
//    public  void startPlayAudio(String audioFileName) {
//        MediaRecorderUtils.getInstance().startPlayAudio(Config.AUDIO_FOLDER + audioFileName, new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                CToast.showShort(mCurrentActivity, "播放完毕...");
//                MediaRecorderUtils.getInstance().setPlaying(false);
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == -1) {
//            switch (requestCode) {
//                //拍照回调
//                case 0x124:
//                    listPic.add(imgName);
//                    showPicture();
//                    break;
//                //视频回调
//                case 0x125:
//                  showVedio();
//                    break;
//                //删除图片回调
//                case 0x126:
//                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
//                    for (String imageUrl : cancelList) {
//                        listPic.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
//                    }
//                    showPicture();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//    private void showPicture() {
//        if (listPic.size()>0) {
//            imgShow.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + listPic.get(listPic.size() - 1), 120, 90));
//            tvPicNum.setVisibility(View.VISIBLE);
//            tvPicNum.setText(listPic.size() + "");
//        }else {
//            imgShow.setImageBitmap(null);
//            tvPicNum.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private void showVedio()
//    {
//        if (TextUtils.isEmpty(vedioFileName)) {
//            rlVedio.setVisibility(View.INVISIBLE);
//            return;
//        }
//            mExcutorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                bitmap= ThumbnailUtils.createVideoThumbnail(Config.VIDEO_FOLDER+vedioFileName, MediaStore.Images.Thumbnails.MINI_KIND);
//                duration=MediaRecorderUtils.getInstance().getVedioDurationString(getActivity(),Config.VIDEO_FOLDER+vedioFileName);
//               mCurrentActivity.runOnUiThread(new Runnable() {
//                   @Override
//                   public void run() {
//                       if (bitmap!=null)
//                       {
//                           rlVedio.setVisibility(View.VISIBLE);
//                           imgVideo.setImageBitmap(bitmap);
//                           tvDuration.setText(duration);
//
//
//                       }else
//                       {
//                           rlVedio.setVisibility(View.INVISIBLE);
//                       }
//                   }
//               });
//            }
//        });
//
//    }
//
//    /**
//     * 显示录音对话框
//     */
//    ViewHolder audioHolder;
//    private Dialog mRecordAudioDialg = null;
//    private void showRecordAudioDialog() {
//        if (mRecordAudioDialg == null) {
//            int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) / 2;
//            mRecordAudioDialg = DialogUtils.createDialog(mCurrentActivity, null, R.layout.record_audio_dialog, audioHolder == null ? audioHolder = new ViewHolder() : audioHolder, dialogWidth, dialogWidth, false);
//            mRecordAudioDialg.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    MediaRecorderUtils.getInstance().stopRecordAudio();
//                    showAudio();
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
//    private void showAudio() {
//        if (TextUtils.isEmpty(audioFileName)) {
//            tvAudio.setVisibility(View.INVISIBLE);
//            return;
//        }
//        tvAudio.setVisibility(View.VISIBLE);
//        tvAudio.setText(String.valueOf(MediaRecorderUtils.getInstance().getDurationSuc(mCurrentActivity, Config.AUDIO_FOLDER + audioFileName) + "”"));
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
//    @Override
//    protected void onRefresh(Message msg) {
//        switch (msg.what)
//        {
//            case  LOAD_DATA:
//               showVedio();
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 显示大图片
//     *
//     * @param position
//     */
//    public void showImageDetails(Fragment context, int position, ArrayList<String> mImageUrlList, boolean isShowDelete,boolean isDeleteFile) {
//        Intent intent = new Intent(context.getActivity(), ImageDetailsActivity.class);
//        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
//        if (mImageUrlList != null) {
//            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
//        }
//        intent.putExtra(Config.IS_DELETE_FILE,isDeleteFile);
//        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
//        context.getActivity().startActivityForResult(intent, 0x126);
//    }
//
//    public String[] getSaveData()
//    {
//        String[] str=new String[3];
//        str[0]="";
//        for (String string:listPic)
//        {
//            str[0]=str[0]+string+CoreConfig.COMMA_SEPARATOR;
//        }
//        if (!TextUtils.isEmpty(str[0]))
//        str[0]=str[0].substring(0,str[0].length()-1);
//        str[1]=audioFileName;
//        str[2]=vedioFileName;
//        return str;
//    }
//
//}
