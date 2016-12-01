package com.cnksi.sjjc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.UriUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.JSObject;
import com.cnksi.sjjc.MyWebChromeClient;
import com.cnksi.sjjc.R;

import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by han on 2016/5/5.
 */
public class WorkPlanInformationActivity extends BaseActivity {

    @ViewInject(R.id.web_view)
    private WebView webView;
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> uploadMessage;
    private String imageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_webview);
        initUI();

    }
    JSObject jsInterface;
    // 初始化webView
    @SuppressLint("NewApi")
    private void initUI() {
        tvTitle.setText("变电站（发电厂）第一种工作票");
        webView.getSettings().setJavaScriptEnabled(true);
        jsInterface = new JSObject(_this);
        // webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
       // webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webView.setWebChromeClient(new ChromeClient());
        jsInterface.setWvClientClickListener(new webviewClick());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWillNotCacheDrawing(true);
//        String imagePath="file:///storage/emulated/0/BdzInspection/result_pictures/35143e8efd8bfa9f946a7a541a79d35a778c1.jpg";
//        String html = "<html><head></head><body><img src=\""+ imagePath + "\"></body></html>";
//        webView.loadData(html, "text/html","utf-8");
        String htmlPath = "file:///sdcard/BdzInspection/www/index.html";
        String baseUrl = "file:///sdcard/BdzInspection/www/";
        webView.loadUrl(htmlPath);
        //webView.loadDataWithBaseURL(htmlPath, "", "text/html", "utf-8", null);
        webView.addJavascriptInterface(jsInterface, "JsTest");
    }
//
//    class ChromeClient extends WebChromeClient {
//        // For Android 4.1
//        @TargetApi(19)
//        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//            mUploadMessage = uploadMsg;
//
//            Intent intent=new Intent(_this,SignNameActivity.class);
//            imageName= FunctionUtils.getPrimarykey()+".jpg";
//            intent.putExtra("name",imageName);
//            // TODO Auto-generated method stub
//            startActivityForResult(intent,100);
//        }
//
//        // For Lollipop 5.0 Devices
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
//            if (uploadMessage != null) {
//                uploadMessage.onReceiveValue(null);
//                uploadMessage = null;
//            }
//            uploadMessage = filePathCallback;
//            Intent intent=new Intent(_this,SignNameActivity.class);
//            imageName=FunctionUtils.getPrimarykey()+".jpg";
//            intent.putExtra("name",imageName);
//            // TODO Auto-generated method stub
//            startActivityForResult(intent,100);
//            return true;
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            switch (requestCode) {
                //签名
                case 100:
                    Uri result = UriUtils.getImageContentUri(this, Config.WORKTICKIT_PIC + imageName);
                    callJsSign(jsId,result.toString());
                break;
                case CoreConfig.ACTION_IMAGE:
                    Uri pic=UriUtils.getImageContentUri(this,Config.WORKTICKIT_PIC+imageName);
                    File file=new File(Config.WORKTICKIT_PIC+imageName);
                    callJs(jsId,file.getAbsolutePath() );

                break;
            }
        }
    }
    private String jsId="";
    public void callJs(final  String id,final String path)
    {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //调用JS中的 函数，当然也可以不传参
                String str="javascript:androidCallJS('"+id+"','"+path+"')";
                webView.loadUrl(str);
                jsId="";
            }
        });
    }

    public void callJsSign(final String id,final String path)
    {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //调用JS中的 函数，当然也可以不传参
                String str="javascript:androidCallqx('"+id+"','"+path+"')";
                webView.loadUrl(str);
                jsId="";
            }
        });
    }



    class webviewClick implements JSObject.wvClientClickListener {

        @Override
        public void signName(String id) {
            jsId=id;
            Intent intent=new Intent(_this,SignNameActivity.class);
            imageName=jsId+".jpg";
            File file=new File(Config.WORKTICKIT_PIC+imageName);
            if (file.exists())
            {
                file.delete();
            }
            intent.putExtra(Config.SIGN_FILENAME,Config.WORKTICKIT_PIC+imageName);
            // TODO Auto-generated method stub
            startActivityForResult(intent,100);
        }

        @Override
        public void takePicture(String id) {
           jsId=id;
            imageName=jsId+".jpg";
            File file=new File(Config.WORKTICKIT_PIC+imageName);
            if (file.exists())
           {
                file.delete();
            }
//            FunctionUtils.takePicture(WorkPlanInformationActivity.this,
//                   imageName,Config.WORKTICKIT_PIC);
           Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri imageUri = Uri.fromFile(new File(Config.WORKTICKIT_PIC,imageName));
//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CoreConfig.ACTION_IMAGE);
        }

        @Override
        public void showIamge(String id) {
            ArrayList<String> pics=new ArrayList<String>();
            pics.add(Config.WORKTICKIT_PIC+id+".jpg");
            showImageDetails(mCurrentActivity,0,pics,false);
        }

        @Override
        public void save() {
            finish();
        }

    }
}
