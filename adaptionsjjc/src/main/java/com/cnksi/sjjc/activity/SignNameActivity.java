package com.cnksi.sjjc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.view.LinePathView;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;


public class SignNameActivity extends Activity {
    @ViewInject(R.id.view)
    LinePathView mLinePathView;
    @ViewInject(R.id.clear1)
    Button mClear;
    @ViewInject(R.id.save1)
    Button mSave;
    @ViewInject(R.id.ll)
    LinearLayout ll;
    String mSignName="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hand_write);
        x.view().inject(this);
        setResult(50);
        mSignName=getIntent().getStringExtra(Config.SIGN_FILENAME);
        if (TextUtils.isEmpty(mSignName))
        {
            mSignName= FunctionUtils.getPrimarykey()+".jpg";
            mSignName= Environment.getExternalStorageDirectory().getAbsolutePath()+mSignName;
        }
//       mLinePathView.setmPaintWidth(20);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (mLinePathView.getTouched())
            {
                try {
                    mLinePathView.save(mSignName,false,10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent data=new Intent();
                //返回全部路径 如果是上个Activity传过来的文件名 则不用处理
                data.putExtra(Config.SIGN_FILENAME,mSignName);
                setResult(RESULT_OK);
                finish();
            }else
            {
                Toast.makeText(SignNameActivity.this,"您没有签名~",Toast.LENGTH_SHORT).show();
            }
        }});

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinePathView.clear();
            }
        });
    }



}
