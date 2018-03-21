package com.cnksi.sjjc.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cnksi.core.view.LinePathView;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.ActivityHandWriteBinding;
import com.cnksi.sjjc.util.FunctionUtils;

import org.xutils.x;

import java.io.IOException;


public class SignNameActivity extends Activity {
    String mSignName = "";
    private ActivityHandWriteBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hand_write);
        setResult(50);
        mSignName = getIntent().getStringExtra(Config.SIGN_FILENAME);
        if (TextUtils.isEmpty(mSignName)) {
            mSignName = FunctionUtils.getPrimarykey() + ".jpg";
            mSignName = Environment.getExternalStorageDirectory().getAbsolutePath() + mSignName;
        }
//       binding.view.setmPaintWidth(20);
        binding.save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.view.getTouched()) {
                    try {
                        binding.view.save(mSignName, false, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent data = new Intent();
                    //返回全部路径 如果是上个Activity传过来的文件名 则不用处理
                    data.putExtra(Config.SIGN_FILENAME, mSignName);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(SignNameActivity.this, "您没有签名~", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.view.clear();
            }
        });
    }


}
